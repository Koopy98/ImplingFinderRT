package com.hablapatabla.implingfinder;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.hablapatabla.implingfinder.model.ImplingFinderData;
import com.hablapatabla.implingfinder.model.ImplingFinderEnum;
import com.hablapatabla.implingfinder.model.ImplingFinderWorldMapPoint;
import com.hablapatabla.implingfinder.ui.ImplingFinderPanel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
        name = "Impling Finder",
        description = "A plugin to crowdsource impling locations",
        tags = {"config", "menu", "impling", "finder", "hunter", "group",
                    "fun", "crowdsource", "crowd", "party", "implingfinder", "impling finder",
                    "clue", "clue scroll", "medium clue", "Impling Finder", "Impling", "Finder"},
        enabledByDefault = true
)
public class ImplingFinderPlugin extends Plugin {
    @Inject
    private ImplingFinderConfig config;

    @Inject
    private ImplingFinderPanel panel;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private Client client;

    @Inject
    private Gson gson;

    @Inject
    private ItemManager itemManager;

    @Inject
    private WorldMapPointManager worldMapPointManager;

    @Inject
    private ImplingFinderWebManager webManager;

    @Inject
    private WorldService worldService;

    @Inject
    private Notifier notifier;

    @Inject
    private ClientThread clientThread;

    @Getter(AccessLevel.PACKAGE)
    private NavigationButton button = null;

    @Getter(AccessLevel.PACKAGE)
    private List<ImplingFinderData> implingsToUpload = new ArrayList<>();

    @Setter(AccessLevel.PACKAGE)
    private List<ImplingFinderData> remotelyFetchedImplings = new ArrayList<>();

    protected static String implingGetAnyEndpoint = "https://puos0bfgxc2lno5-implingdb.adb.us-phoenix-1.oraclecloudapps.com/ords/impling/implingdev/dev";

    protected static String implingGetIdEndpoint = "https://puos0bfgxc2lno5-implingdb.adb.us-phoenix-1.oraclecloudapps.com/ords/impling/implingdev/dev/";

    protected static String implingPostEndpoint = "https://puos0bfgxc2lno5-implingdb.adb.us-phoenix-1.oraclecloudapps.com/ords/impling/implingdev/dev";

    public static final int RECENT_IMPLINGS_ID = -1;

    // Supabase endpoint and INSERT-only anon key for dual-write load testing.
    // The anon key has no SELECT policy — it can only INSERT data, meaning
    // nobody can use it to read impling locations from Supabase.
    // Dual-write is controlled by the "Enable Dual Write" config toggle and
    // is OFF by default so existing behaviour is completely unchanged until
    // Hablapatabla explicitly enables it for testing.
    public static final String SUPABASE_ANON_KEY = "sb_publishable_QOqhhjOdUFAVKDX6x0jmaA__-PwdpKM";
    public static String implingSupabasePostEndpoint = "https://zoorgufqkavyngfhucgc.supabase.co/rest/v1/implings";
    // Reads go against the restricted view, not the raw table - it only ever
    // returns sightings from the last 10 minutes (matching the existing
    // auto-delete window), so polling it faster than that gets no additional
    // data. The anon key still has no SELECT grant on the raw table itself.
    public static String implingSupabaseGetEndpoint = "https://zoorgufqkavyngfhucgc.supabase.co/rest/v1/implings_recent";

    // Puro-Puro minigame area bounds (OSRS world coordinates).
    // Used to filter Puro-Puro sightings out of the results list when
    // the "Hide Puro-Puro" config option is enabled.
    private static final int PURO_PURO_MIN_X = 2540;
    private static final int PURO_PURO_MAX_X = 2870;
    private static final int PURO_PURO_MIN_Y = 4220;
    private static final int PURO_PURO_MAX_Y = 4380;

    @Provides
    ImplingFinderConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ImplingFinderConfig.class);
    }

    private Logger logger;
    private BufferedImage icon;
    private BufferedImage mapArrow = null;

    private boolean mapPointSet = false;
    private boolean displayingButton = true;
    private boolean wantSpawnNotifications = false;
    private long lastGetCall = System.currentTimeMillis();

    protected static final String CONFIG_GROUP = "Impling Finder";
    private static final int NPC_UPLOAD_TIME = 20;
    private static final int PANEL_REFRESH_TIME = 1;
    private static final int GET_REQUEST_COOLDOWN_TIME = 2000;



    @Override
    protected void startUp() throws Exception {
        logger = LoggerFactory.getLogger(ImplingFinderPlugin.class);
        loadPluginPanel();
        if (!config.beenOpened())
            panel.showSplash();
        else
            panel.continuePastSplash();
    }

    @Override
    protected void shutDown() throws Exception {
        clientToolbar.removeNavigation(button);
    }

    private void loadPluginPanel() {
        try {
           icon = ImageUtil.loadImageResource(getClass(), "/icon.png");
        }
        catch (Exception e) {
            logger.error("Couldn't load plugin icon");
            return;
        }
        if (button != null) {
            clientToolbar.removeNavigation(button);
        }

        panel = injector.getInstance(ImplingFinderPanel.class);

        button = NavigationButton.builder().tooltip("Impling Finder").icon(icon).priority(6).panel(panel).build();
        if (displayingButton)
            clientToolbar.addNavigation(button);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals(CONFIG_GROUP)) {
            switch (event.getKey()) {
                case ImplingFinderConfig.HIDE_BUTTON:
                    if (config.hideButton())
                        clientToolbar.removeNavigation(button);
                    else
                        clientToolbar.addNavigation(button);
                    break;
                case ImplingFinderConfig.IMPLING_SPAWN_NOTIFY:
                    wantSpawnNotifications = config.implingSpawnNotify();
                    break;
                case ImplingFinderConfig.HIDE_PURO_PURO:
                    // Re-apply filtering immediately when the toggle changes,
                    // rather than waiting for the next fetch.
                    updatePanels();
                    break;
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        final NPC npc = npcSpawned.getNpc();
        if (npc.getName() == null)
            return;

        if (!isImpling(npc.getName()))
            return;

        if (wantSpawnNotifications)
            notifier.notify("An impling just spawned!");

        ImplingFinderData imp = makeImp(npc);
        logger.error(imp.toString());
        implingsToUpload.add(imp);
    }

    private boolean isImpling(String name) {
        return ImplingFinderEnum.getIdByNameStrict(name) != RECENT_IMPLINGS_ID;
    }

    private boolean isImpling(int id) {
        return ImplingFinderEnum.findById(id) != null;
    }

    private ImplingFinderData makeImp(NPC n) {
        int world = client.getWorld();
        WorldArea area = n.getWorldArea();
        WorldPoint point = area.toWorldPoint();
        return ImplingFinderData.builder()
                                    .npcid(ImplingFinderEnum.getIdByNameStrict(n.getName()))
                                    .world(world)
                                    .xcoord(point.getX())
                                    .ycoord(point.getY())
                                    .plane(point.getPlane())
                                    .discoveredtime(Instant.now())
                                    .build();
        //logger.error("Making Imp:" + n.getName() + " " + datum.toString());
    }

    public BufferedImage getWorldMapImage() {
        return ImageUtil.loadImageResource(getClass(), "/icon.png");
    }

    public void addMapPoints(WorldPoint... points) {
        // World map point manipulation and player location lookups must
        // happen on the client thread, not the Swing UI thread that mouse
        // click handlers run on. Calling these directly from the panel's
        // mouseReleased handler silently failed (assertions are normally
        // disabled in production), which is why clicking an impling in the
        // list stopped placing a marker on the world map. See issue #33.
        clientThread.invoke(() -> {
            WorldPoint p = client.getLocalPlayer().getWorldLocation();
            if (p == null)
                return;

            if (mapPointSet) {
                mapPointSet = false;
                worldMapPointManager.removeIf(ImplingFinderWorldMapPoint.class::isInstance);
                return;
            }

            mapPointSet = true;
            worldMapPointManager.removeIf(ImplingFinderWorldMapPoint.class::isInstance);
            for (final WorldPoint point : points) {
                worldMapPointManager.add(new ImplingFinderWorldMapPoint(point, this));
                // Pan the world map to the impling's location so the new
                // marker is actually visible without manual scrolling.
                client.getWorldMap().setWorldMapPositionTarget(point);
            }
        });
    }

    @Schedule(
            period = NPC_UPLOAD_TIME,
            unit = ChronoUnit.SECONDS,
            asynchronous = true
    )
    public void uploadFoundImplings() {
        // List is cleared by webManager after uploading
        if (implingsToUpload.size() > 0)
            webManager.postImplings();
    }

    @Schedule(
            period = PANEL_REFRESH_TIME,
            unit = ChronoUnit.SECONDS,
            asynchronous = true
    )
    public void checkPanelRequest() {
        long currTime = System.currentTimeMillis();
        if (panel.isClearRequested()) {
            remotelyFetchedImplings.clear();
            updatePanels();
            panel.setClearRequested(false);
        }

        // 2 second wait between requests
        if (panel.isFetchRequested() && currTime - lastGetCall >= GET_REQUEST_COOLDOWN_TIME) {
            remotelyFetchedImplings.clear();
            remotelyFetchedImplings = webManager.getData(panel.getSelectedButtons());
            panel.setFetchRequested(false);
            lastGetCall = System.currentTimeMillis();
            updatePanels();
        }
    }

    private boolean isInPuroPuro(ImplingFinderData data) {
        return data.getXcoord() >= PURO_PURO_MIN_X && data.getXcoord() <= PURO_PURO_MAX_X
                && data.getYcoord() >= PURO_PURO_MIN_Y && data.getYcoord() <= PURO_PURO_MAX_Y;
    }

    // Compile-time lookup table mapping each named location to its config
    // getter, built with direct method references (not reflection - plugin
    // hub review specifically disallows reflection). Built once, lazily, on
    // first use rather than in the constructor, since it needs `config` to
    // already be injected.
    private java.util.Map<String, java.util.function.BooleanSupplier> locationConfigLookup;

    private java.util.Map<String, java.util.function.BooleanSupplier> getLocationConfigLookup() {
        if (locationConfigLookup == null) {
            java.util.Map<String, java.util.function.BooleanSupplier> map = new java.util.HashMap<>();
        map.put("Abyssal Area", config::showAbyssalArea);
        map.put("Abyssal Nexus", config::showAbyssalNexus);
        map.put("Agility Pyramid", config::showAgilityPyramid);
        map.put("Air Altar", config::showAirAltar);
        map.put("Al Kharid", config::showAlKharid);
        map.put("Al Kharid Mine", config::showAlKharidMine);
        map.put("Ape Atoll", config::showApeAtoll);
        map.put("Arandar", config::showArandar);
        map.put("Arceuus", config::showArceuus);
        map.put("Ardougne", config::showArdougne);
        map.put("Asgarnia", config::showAsgarnia);
        map.put("Bandit Camp", config::showBanditCamp);
        map.put("Barbarian Outpost", config::showBarbarianOutpost);
        map.put("Barbarian Village", config::showBarbarianVillage);
        map.put("Battlefield", config::showBattlefield);
        map.put("Battlefront", config::showBattlefront);
        map.put("Bedabin Camp", config::showBedabinCamp);
        map.put("Blast Mine", config::showBlastMine);
        map.put("Body Altar", config::showBodyAltar);
        map.put("Brimhaven", config::showBrimhaven);
        map.put("Burgh de Rott", config::showBurghDeRott);
        map.put("Burthorpe", config::showBurthorpe);
        map.put("Canifis", config::showCanifis);
        map.put("Catherby", config::showCatherby);
        map.put("Chaos Altar", config::showChaosAltar);
        map.put("Corsair Cove", config::showCorsairCove);
        map.put("Cosmic Altar", config::showCosmicAltar);
        map.put("Cosmic Entity's Plane", config::showCosmicEntitySPlane);
        map.put("Crabclaw Isle", config::showCrabclawIsle);
        map.put("Crafting Guild", config::showCraftingGuild);
        map.put("Crandor", config::showCrandor);
        map.put("Crash Island", config::showCrashIsland);
        map.put("Dark Altar", config::showDarkAltar);
        map.put("Darkmeyer", config::showDarkmeyer);
        map.put("Death Altar", config::showDeathAltar);
        map.put("Death Plateau", config::showDeathPlateau);
        map.put("Dense Essence Mine", config::showDenseEssenceMine);
        map.put("Desert Plateau", config::showDesertPlateau);
        map.put("Digsite", config::showDigsite);
        map.put("Dorgesh-Kaan", config::showDorgeshKaan);
        map.put("Dragontooth Island", config::showDragontoothIsland);
        map.put("Draynor", config::showDraynor);
        map.put("Draynor Manor", config::showDraynorManor);
        map.put("Drill Sergeant's Training Camp", config::showDrillSergeantSTrainingCamp);
        map.put("Eagles' Peak", config::showEaglesPeak);
        map.put("Earth Altar", config::showEarthAltar);
        map.put("Edgeville", config::showEdgeville);
        map.put("Enchanted Valley", config::showEnchantedValley);
        map.put("Entrana", config::showEntrana);
        map.put("Etceteria", config::showEtceteria);
        map.put("Evil Twin Crane Room", config::showEvilTwinCraneRoom);
        map.put("Exam Centre", config::showExamCentre);
        map.put("Falador", config::showFalador);
        map.put("Falador Farm", config::showFaladorFarm);
        map.put("Farming Guild", config::showFarmingGuild);
        map.put("Feldip Hills", config::showFeldipHills);
        map.put("Fenkenstrain's Castle", config::showFenkenstrainSCastle);
        map.put("Fight Arena", config::showFightArena);
        map.put("Fire Altar", config::showFireAltar);
        map.put("Fisher Realm", config::showFisherRealm);
        map.put("Fishing Guild", config::showFishingGuild);
        map.put("Fishing Platform", config::showFishingPlatform);
        map.put("Fossil Island", config::showFossilIsland);
        map.put("Freaky Forester's Clearing", config::showFreakyForesterSClearing);
        map.put("Fremennik Isles", config::showFremennikIsles);
        map.put("Fremennik Province", config::showFremennikProvince);
        map.put("Frogland", config::showFrogland);
        map.put("Galvek Shipwrecks", config::showGalvekShipwrecks);
        map.put("Gnome Stronghold", config::showGnomeStronghold);
        map.put("Gnome Village", config::showGnomeVillage);
        map.put("God Wars Dungeon", config::showGodWarsDungeon);
        map.put("Gorak's Plane", config::showGorakSPlane);
        map.put("Grand Exchange", config::showGrandExchange);
        map.put("Great Kourend", config::showGreatKourend);
        map.put("Gu'Tanoth", config::showGuTanoth);
        map.put("Gwenith", config::showGwenith);
        map.put("Harmony Island", config::showHarmonyIsland);
        map.put("Hazelmere's Island", config::showHazelmereSIsland);
        map.put("Hosidius", config::showHosidius);
        map.put("Ice Path", config::showIcePath);
        map.put("Iceberg", config::showIceberg);
        map.put("Icyene Graveyard", config::showIcyeneGraveyard);
        map.put("Isafdar", config::showIsafdar);
        map.put("Island of Stone", config::showIslandOfStone);
        map.put("Isle of Souls", config::showIsleOfSouls);
        map.put("Jatizso", config::showJatizso);
        map.put("Jiggig", config::showJiggig);
        map.put("Kandarin", config::showKandarin);
        map.put("Karamja", config::showKaramja);
        map.put("Kebos Lowlands", config::showKebosLowlands);
        map.put("Kebos Swamp", config::showKebosSwamp);
        map.put("Keldagrim", config::showKeldagrim);
        map.put("Kharazi Jungle", config::showKharaziJungle);
        map.put("Kharidian Desert", config::showKharidianDesert);
        map.put("Killerwatt Plane", config::showKillerwattPlane);
        map.put("Kourend Woodland", config::showKourendWoodland);
        map.put("Land's End", config::showLandSEnd);
        map.put("Law Altar", config::showLawAltar);
        map.put("Legends' Guild", config::showLegendsGuild);
        map.put("Lighthouse", config::showLighthouse);
        map.put("Lithkren", config::showLithkren);
        map.put("Lletya", config::showLletya);
        map.put("Lovakengj", config::showLovakengj);
        map.put("Lumbridge", config::showLumbridge);
        map.put("Lumbridge Swamp", config::showLumbridgeSwamp);
        map.put("Lunar Isle", config::showLunarIsle);
        map.put("Marim", config::showMarim);
        map.put("Max Island", config::showMaxIsland);
        map.put("McGrubor's Wood", config::showMcGruborSWood);
        map.put("Meiyerditch", config::showMeiyerditch);
        map.put("Menaphos", config::showMenaphos);
        map.put("Mime's Stage", config::showMimeSStage);
        map.put("Mind Altar", config::showMindAltar);
        map.put("Miscellania", config::showMiscellania);
        map.put("Misthalin", config::showMisthalin);
        map.put("Molch", config::showMolch);
        map.put("Molch Island", config::showMolchIsland);
        map.put("Mor Ul Rek", config::showMorUlRek);
        map.put("Mort'ton", config::showMortTon);
        map.put("Morytania", config::showMorytania);
        map.put("Mos Le'Harmless", config::showMosLeHarmless);
        map.put("Mount Karuulm", config::showMountKaruulm);
        map.put("Mount Quidamortem", config::showMountQuidamortem);
        map.put("Mountain Camp", config::showMountainCamp);
        map.put("Mr. Mordaut's Classroom", config::showMrMordautSClassroom);
        map.put("Mudskipper Point", config::showMudskipperPoint);
        map.put("Mynydd", config::showMynydd);
        map.put("Mysterious Old Man's Maze", config::showMysteriousOldManSMaze);
        map.put("Myths' Guild", config::showMythsGuild);
        map.put("Nardah", config::showNardah);
        map.put("Nature Altar", config::showNatureAltar);
        map.put("Neitiznot", config::showNeitiznot);
        map.put("Northern Tundras", config::showNorthernTundras);
        map.put("Observatory", config::showObservatory);
        map.put("Odd One Out", config::showOddOneOut);
        map.put("Otto's Grotto", config::showOttoSGrotto);
        map.put("Ourania Hunter Area", config::showOuraniaHunterArea);
        map.put("Pirates' Cove", config::showPiratesCove);
        map.put("Piscatoris", config::showPiscatoris);
        map.put("Piscatoris Hunter Area", config::showPiscatorisHunterArea);
        map.put("Player Owned House", config::showPlayerOwnedHouse);
        map.put("Poison Waste", config::showPoisonWaste);
        map.put("Pollnivneach", config::showPollnivneach);
        map.put("Port Khazard", config::showPortKhazard);
        map.put("Port Phasmatys", config::showPortPhasmatys);
        map.put("Port Piscarilius", config::showPortPiscarilius);
        map.put("Port Sarim", config::showPortSarim);
        map.put("Port Tyras", config::showPortTyras);
        map.put("Prifddinas", config::showPrifddinas);
        map.put("Puro Puro", config::showPuroPuro);
        map.put("Quarry", config::showQuarry);
        map.put("Ranging Guild", config::showRangingGuild);
        map.put("Ratcatchers Mansion", config::showRatcatchersMansion);
        map.put("Rellekka", config::showRellekka);
        map.put("Rimmington", config::showRimmington);
        map.put("Ruins of Unkah", config::showRuinsOfUnkah);
        map.put("Rune Essence Mine", config::showRuneEssenceMine);
        map.put("ScapeRune", config::showScapeRune);
        map.put("Sea Spirit Dock", config::showSeaSpiritDock);
        map.put("Seers' Village", config::showSeersVillage);
        map.put("Shayzien", config::showShayzien);
        map.put("Shilo Village", config::showShiloVillage);
        map.put("Ship Yard", config::showShipYard);
        map.put("Silvarea", config::showSilvarea);
        map.put("Sinclair Mansion", config::showSinclairMansion);
        map.put("Slayer Tower", config::showSlayerTower);
        map.put("Slepe", config::showSlepe);
        map.put("Sophanem", config::showSophanem);
        map.put("Soul Altar", config::showSoulAltar);
        map.put("Tai Bwo Wannai", config::showTaiBwoWannai);
        map.put("Taverley", config::showTaverley);
        map.put("The Forsaken Tower", config::showTheForsakenTower);
        map.put("Troll Arena", config::showTrollArena);
        map.put("Troll Stronghold", config::showTrollStronghold);
        map.put("Trollheim", config::showTrollheim);
        map.put("Trollweiss Mountain", config::showTrollweissMountain);
        map.put("Tutorial Island", config::showTutorialIsland);
        map.put("Underwater", config::showUnderwater);
        map.put("Uzer", config::showUzer);
        map.put("Varrock", config::showVarrock);
        map.put("Ver Sinhaza", config::showVerSinhaza);
        map.put("Void Knights' Outpost", config::showVoidKnightsOutpost);
        map.put("Water Altar", config::showWaterAltar);
        map.put("Waterbirth Island", config::showWaterbirthIsland);
        map.put("Weiss", config::showWeiss);
        map.put("Wintertodt Camp", config::showWintertodtCamp);
        map.put("Witchaven", config::showWitchaven);
        map.put("Wizards' Tower", config::showWizardsTower);
        map.put("Woodcutting Guild", config::showWoodcuttingGuild);
        map.put("Wrath Altar", config::showWrathAltar);
        map.put("Yanille", config::showYanille);
        map.put("Zanaris", config::showZanaris);
        map.put("Zul-Andra", config::showZulAndra);
            locationConfigLookup = map;
        }
        return locationConfigLookup;
    }

    /**
     * Looks up whether the given location should be shown, using the
     * pre-built method-reference lookup table above - no reflection.
     */
    private boolean shouldDisplayLocation(ImplingFinderData data) {
        WorldPoint point = new WorldPoint(data.getXcoord(), data.getYcoord(), data.getPlane());
        com.hablapatabla.implingfinder.model.ImplingFinderRegion region =
                com.hablapatabla.implingfinder.model.ImplingFinderRegion.fromRegion(point.getRegionID());

        if (region == null) {
            return config.showUnknownLocation();
        }

        java.util.function.BooleanSupplier supplier = getLocationConfigLookup().get(region.getName());
        // Fail open (show it) if a location somehow isn't in the map, rather
        // than silently hiding implings due to a lookup gap.
        return supplier != null ? supplier.getAsBoolean() : true;
    }

    public void updatePanels() {
        List<ImplingFinderData> toDisplay = remotelyFetchedImplings;

        // Filter out Puro-Puro sightings if the user has the "Hide Puro-Puro"
        // option enabled. This only affects the local display; it does not
        // change what gets uploaded or what other users see.
        if (config.hidePuroPuro()) {
            toDisplay = toDisplay.stream()
                    .filter(data -> !isInPuroPuro(data))
                    .collect(Collectors.toList());
        }

        // Per-location filtering, driven by the ~193 checkboxes under the
        // "Filter Locations" config section. Persists automatically across
        // restarts since it's just regular RuneLite config, no custom
        // state-tracking needed.
        toDisplay = toDisplay.stream()
                .filter(this::shouldDisplayLocation)
                .collect(Collectors.toList());

        Collections.sort(toDisplay, Collections.reverseOrder());
        List<ImplingFinderData> finalList = toDisplay;

        // The player's location is needed by the panel to show compass
        // direction/distance to each impling. Reading it must happen on the
        // client thread (the same requirement that applies to
        // worldMapPointManager in addMapPoints), so it's captured here and
        // handed to the Swing panel as a plain value rather than letting the
        // panel read `client` directly off the Swing thread.
        clientThread.invoke(() -> {
            WorldPoint playerLocation = client.getLocalPlayer() != null
                    ? client.getLocalPlayer().getWorldLocation()
                    : null;
            SwingUtilities.invokeLater(() -> panel.populateNpcs(finalList, playerLocation));
        });
    }
}
