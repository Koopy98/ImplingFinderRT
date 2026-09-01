package com.hablapatabla.implingfinder;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigSection;
@ConfigGroup(ImplingFinderPlugin.CONFIG_GROUP)
public interface ImplingFinderConfig extends Config
{
    String HIDE_BUTTON = "hideButton";
    String SPLASH_SEEN = "splashSeen";
    String IMPLING_SPAWN_NOTIFY = "spawnNotify";
    String HIDE_PURO_PURO = "hidePuroPuro";
    @ConfigItem(
            position = 0,
            keyName = "noticeHideDontUninstall",
            name = "A note before you go",
            description = "If you decide you no longer want to use this plugin, please use the Hide Button" +
                    " option below instead of uninstalling. Hiding still keeps you contributing sightings to" +
                    " everyone else using the plugin, uninstalling stops that entirely. Every person still" +
                    " running it, even hidden, helps keep the data flowing for the whole community."
    )
    default String noticeHideDontUninstall() {
        return "If you stop using this plugin, please use the Hide Button below instead of uninstalling," +
                " so other people with the plugin can still see implings spawning!";
    }
    @ConfigItem(
            position = 1,
            keyName = IMPLING_SPAWN_NOTIFY,
            name = "Spawn Notification",
            description = "Notifies you if an impling spawns nearby"
    )
    default boolean implingSpawnNotify() { return false; }
    @ConfigItem(
            position = 2,
            keyName = HIDE_BUTTON,
            name = "Hide Button",
            description = "Hides the button from your Runelite sidebar. You'll keep contributing sightings" +
                    " to everyone else using the plugin, you just won't see the panel yourself."
    )
    default boolean hideButton() { return false; }
    @ConfigItem(
            position = 3,
            keyName = "splashSeen",
            name = "I've seen the splash page",
            description = "This plugin has been opened before"
    )
    default boolean beenOpened() { return false; }
    @ConfigItem(
            position = 4,
            keyName = HIDE_PURO_PURO,
            name = "Hide Puro-Puro",
            description = "Filters out implings found inside the Puro-Puro minigame from the results list"
    )
    default boolean hidePuroPuro() { return false; }
    @ConfigItem(
            keyName = "issue",
            position = 7,
            name = "Got an issue?",
            description = "Go to this link if you have an issue or want to request a feature."
    )
    default String issue()
    {
        return "https://github.com/Koopy98/ImplingFinderRT/issues/new";
    }
    @ConfigItem(
            keyName = "discord",
            position = 8,
            name = "Join our Discord",
            description = "Come hang out, get update announcements, and report issues directly."
    )
    default String discord()
    {
        return "https://discord.gg/4cuFQGvDk2";
    }

    @ConfigSection(
            name = "Filter Locations",
            description = "Show or hide implings by named location. Collapsed by default since there are 193 entries.",
            position = 100,
            closedByDefault = true
    )
    String locationFilters = "locationFilters";

    @ConfigItem(keyName="showUnknownLocation", name="Unknown", description="Show implings whose location couldn't be matched to a named area", position=-1, section="locationFilters")
    default boolean showUnknownLocation() { return true; }

    @ConfigItem(keyName="showAbyssalArea", name="Abyssal Area", description="Show implings in Abyssal Area", position=0, section="locationFilters")
    default boolean showAbyssalArea() { return true; }

    @ConfigItem(keyName="showAbyssalNexus", name="Abyssal Nexus", description="Show implings in Abyssal Nexus", position=1, section="locationFilters")
    default boolean showAbyssalNexus() { return true; }

    @ConfigItem(keyName="showAgilityPyramid", name="Agility Pyramid", description="Show implings in Agility Pyramid", position=2, section="locationFilters")
    default boolean showAgilityPyramid() { return true; }

    @ConfigItem(keyName="showAirAltar", name="Air Altar", description="Show implings in Air Altar", position=3, section="locationFilters")
    default boolean showAirAltar() { return true; }

    @ConfigItem(keyName="showAlKharid", name="Al Kharid", description="Show implings in Al Kharid", position=4, section="locationFilters")
    default boolean showAlKharid() { return true; }

    @ConfigItem(keyName="showAlKharidMine", name="Al Kharid Mine", description="Show implings in Al Kharid Mine", position=5, section="locationFilters")
    default boolean showAlKharidMine() { return true; }

    @ConfigItem(keyName="showApeAtoll", name="Ape Atoll", description="Show implings in Ape Atoll", position=6, section="locationFilters")
    default boolean showApeAtoll() { return true; }

    @ConfigItem(keyName="showArandar", name="Arandar", description="Show implings in Arandar", position=7, section="locationFilters")
    default boolean showArandar() { return true; }

    @ConfigItem(keyName="showArceuus", name="Arceuus", description="Show implings in Arceuus", position=8, section="locationFilters")
    default boolean showArceuus() { return true; }

    @ConfigItem(keyName="showArdougne", name="Ardougne", description="Show implings in Ardougne", position=9, section="locationFilters")
    default boolean showArdougne() { return true; }

    @ConfigItem(keyName="showAsgarnia", name="Asgarnia", description="Show implings in Asgarnia", position=10, section="locationFilters")
    default boolean showAsgarnia() { return true; }

    @ConfigItem(keyName="showBanditCamp", name="Bandit Camp", description="Show implings in Bandit Camp", position=11, section="locationFilters")
    default boolean showBanditCamp() { return true; }

    @ConfigItem(keyName="showBarbarianOutpost", name="Barbarian Outpost", description="Show implings in Barbarian Outpost", position=12, section="locationFilters")
    default boolean showBarbarianOutpost() { return true; }

    @ConfigItem(keyName="showBarbarianVillage", name="Barbarian Village", description="Show implings in Barbarian Village", position=13, section="locationFilters")
    default boolean showBarbarianVillage() { return true; }

    @ConfigItem(keyName="showBattlefield", name="Battlefield", description="Show implings in Battlefield", position=14, section="locationFilters")
    default boolean showBattlefield() { return true; }

    @ConfigItem(keyName="showBattlefront", name="Battlefront", description="Show implings in Battlefront", position=15, section="locationFilters")
    default boolean showBattlefront() { return true; }

    @ConfigItem(keyName="showBedabinCamp", name="Bedabin Camp", description="Show implings in Bedabin Camp", position=16, section="locationFilters")
    default boolean showBedabinCamp() { return true; }

    @ConfigItem(keyName="showBlastMine", name="Blast Mine", description="Show implings in Blast Mine", position=17, section="locationFilters")
    default boolean showBlastMine() { return true; }

    @ConfigItem(keyName="showBodyAltar", name="Body Altar", description="Show implings in Body Altar", position=18, section="locationFilters")
    default boolean showBodyAltar() { return true; }

    @ConfigItem(keyName="showBrimhaven", name="Brimhaven", description="Show implings in Brimhaven", position=19, section="locationFilters")
    default boolean showBrimhaven() { return true; }

    @ConfigItem(keyName="showBurghDeRott", name="Burgh de Rott", description="Show implings in Burgh de Rott", position=20, section="locationFilters")
    default boolean showBurghDeRott() { return true; }

    @ConfigItem(keyName="showBurthorpe", name="Burthorpe", description="Show implings in Burthorpe", position=21, section="locationFilters")
    default boolean showBurthorpe() { return true; }

    @ConfigItem(keyName="showCanifis", name="Canifis", description="Show implings in Canifis", position=22, section="locationFilters")
    default boolean showCanifis() { return true; }

    @ConfigItem(keyName="showCatherby", name="Catherby", description="Show implings in Catherby", position=23, section="locationFilters")
    default boolean showCatherby() { return true; }

    @ConfigItem(keyName="showChaosAltar", name="Chaos Altar", description="Show implings in Chaos Altar", position=24, section="locationFilters")
    default boolean showChaosAltar() { return true; }

    @ConfigItem(keyName="showCorsairCove", name="Corsair Cove", description="Show implings in Corsair Cove", position=25, section="locationFilters")
    default boolean showCorsairCove() { return true; }

    @ConfigItem(keyName="showCosmicAltar", name="Cosmic Altar", description="Show implings in Cosmic Altar", position=26, section="locationFilters")
    default boolean showCosmicAltar() { return true; }

    @ConfigItem(keyName="showCosmicEntitySPlane", name="Cosmic Entity's Plane", description="Show implings in Cosmic Entity's Plane", position=27, section="locationFilters")
    default boolean showCosmicEntitySPlane() { return true; }

    @ConfigItem(keyName="showCrabclawIsle", name="Crabclaw Isle", description="Show implings in Crabclaw Isle", position=28, section="locationFilters")
    default boolean showCrabclawIsle() { return true; }

    @ConfigItem(keyName="showCraftingGuild", name="Crafting Guild", description="Show implings in Crafting Guild", position=29, section="locationFilters")
    default boolean showCraftingGuild() { return true; }

    @ConfigItem(keyName="showCrandor", name="Crandor", description="Show implings in Crandor", position=30, section="locationFilters")
    default boolean showCrandor() { return true; }

    @ConfigItem(keyName="showCrashIsland", name="Crash Island", description="Show implings in Crash Island", position=31, section="locationFilters")
    default boolean showCrashIsland() { return true; }

    @ConfigItem(keyName="showDarkAltar", name="Dark Altar", description="Show implings in Dark Altar", position=32, section="locationFilters")
    default boolean showDarkAltar() { return true; }

    @ConfigItem(keyName="showDarkmeyer", name="Darkmeyer", description="Show implings in Darkmeyer", position=33, section="locationFilters")
    default boolean showDarkmeyer() { return true; }

    @ConfigItem(keyName="showDeathAltar", name="Death Altar", description="Show implings in Death Altar", position=34, section="locationFilters")
    default boolean showDeathAltar() { return true; }

    @ConfigItem(keyName="showDeathPlateau", name="Death Plateau", description="Show implings in Death Plateau", position=35, section="locationFilters")
    default boolean showDeathPlateau() { return true; }

    @ConfigItem(keyName="showDenseEssenceMine", name="Dense Essence Mine", description="Show implings in Dense Essence Mine", position=36, section="locationFilters")
    default boolean showDenseEssenceMine() { return true; }

    @ConfigItem(keyName="showDesertPlateau", name="Desert Plateau", description="Show implings in Desert Plateau", position=37, section="locationFilters")
    default boolean showDesertPlateau() { return true; }

    @ConfigItem(keyName="showDigsite", name="Digsite", description="Show implings in Digsite", position=38, section="locationFilters")
    default boolean showDigsite() { return true; }

    @ConfigItem(keyName="showDorgeshKaan", name="Dorgesh-Kaan", description="Show implings in Dorgesh-Kaan", position=39, section="locationFilters")
    default boolean showDorgeshKaan() { return true; }

    @ConfigItem(keyName="showDragontoothIsland", name="Dragontooth Island", description="Show implings in Dragontooth Island", position=40, section="locationFilters")
    default boolean showDragontoothIsland() { return true; }

    @ConfigItem(keyName="showDraynor", name="Draynor", description="Show implings in Draynor", position=41, section="locationFilters")
    default boolean showDraynor() { return true; }

    @ConfigItem(keyName="showDraynorManor", name="Draynor Manor", description="Show implings in Draynor Manor", position=42, section="locationFilters")
    default boolean showDraynorManor() { return true; }

    @ConfigItem(keyName="showDrillSergeantSTrainingCamp", name="Drill Sergeant's Training Camp", description="Show implings in Drill Sergeant's Training Camp", position=43, section="locationFilters")
    default boolean showDrillSergeantSTrainingCamp() { return true; }

    @ConfigItem(keyName="showEaglesPeak", name="Eagles' Peak", description="Show implings in Eagles' Peak", position=44, section="locationFilters")
    default boolean showEaglesPeak() { return true; }

    @ConfigItem(keyName="showEarthAltar", name="Earth Altar", description="Show implings in Earth Altar", position=45, section="locationFilters")
    default boolean showEarthAltar() { return true; }

    @ConfigItem(keyName="showEdgeville", name="Edgeville", description="Show implings in Edgeville", position=46, section="locationFilters")
    default boolean showEdgeville() { return true; }

    @ConfigItem(keyName="showEnchantedValley", name="Enchanted Valley", description="Show implings in Enchanted Valley", position=47, section="locationFilters")
    default boolean showEnchantedValley() { return true; }

    @ConfigItem(keyName="showEntrana", name="Entrana", description="Show implings in Entrana", position=48, section="locationFilters")
    default boolean showEntrana() { return true; }

    @ConfigItem(keyName="showEtceteria", name="Etceteria", description="Show implings in Etceteria", position=49, section="locationFilters")
    default boolean showEtceteria() { return true; }

    @ConfigItem(keyName="showEvilTwinCraneRoom", name="Evil Twin Crane Room", description="Show implings in Evil Twin Crane Room", position=50, section="locationFilters")
    default boolean showEvilTwinCraneRoom() { return true; }

    @ConfigItem(keyName="showExamCentre", name="Exam Centre", description="Show implings in Exam Centre", position=51, section="locationFilters")
    default boolean showExamCentre() { return true; }

    @ConfigItem(keyName="showFalador", name="Falador", description="Show implings in Falador", position=52, section="locationFilters")
    default boolean showFalador() { return true; }

    @ConfigItem(keyName="showFaladorFarm", name="Falador Farm", description="Show implings in Falador Farm", position=53, section="locationFilters")
    default boolean showFaladorFarm() { return true; }

    @ConfigItem(keyName="showFarmingGuild", name="Farming Guild", description="Show implings in Farming Guild", position=54, section="locationFilters")
    default boolean showFarmingGuild() { return true; }

    @ConfigItem(keyName="showFeldipHills", name="Feldip Hills", description="Show implings in Feldip Hills", position=55, section="locationFilters")
    default boolean showFeldipHills() { return true; }

    @ConfigItem(keyName="showFenkenstrainSCastle", name="Fenkenstrain's Castle", description="Show implings in Fenkenstrain's Castle", position=56, section="locationFilters")
    default boolean showFenkenstrainSCastle() { return true; }

    @ConfigItem(keyName="showFightArena", name="Fight Arena", description="Show implings in Fight Arena", position=57, section="locationFilters")
    default boolean showFightArena() { return true; }

    @ConfigItem(keyName="showFireAltar", name="Fire Altar", description="Show implings in Fire Altar", position=58, section="locationFilters")
    default boolean showFireAltar() { return true; }

    @ConfigItem(keyName="showFisherRealm", name="Fisher Realm", description="Show implings in Fisher Realm", position=59, section="locationFilters")
    default boolean showFisherRealm() { return true; }

    @ConfigItem(keyName="showFishingGuild", name="Fishing Guild", description="Show implings in Fishing Guild", position=60, section="locationFilters")
    default boolean showFishingGuild() { return true; }

    @ConfigItem(keyName="showFishingPlatform", name="Fishing Platform", description="Show implings in Fishing Platform", position=61, section="locationFilters")
    default boolean showFishingPlatform() { return true; }

    @ConfigItem(keyName="showFossilIsland", name="Fossil Island", description="Show implings in Fossil Island", position=62, section="locationFilters")
    default boolean showFossilIsland() { return true; }

    @ConfigItem(keyName="showFreakyForesterSClearing", name="Freaky Forester's Clearing", description="Show implings in Freaky Forester's Clearing", position=63, section="locationFilters")
    default boolean showFreakyForesterSClearing() { return true; }

    @ConfigItem(keyName="showFremennikIsles", name="Fremennik Isles", description="Show implings in Fremennik Isles", position=64, section="locationFilters")
    default boolean showFremennikIsles() { return true; }

    @ConfigItem(keyName="showFremennikProvince", name="Fremennik Province", description="Show implings in Fremennik Province", position=65, section="locationFilters")
    default boolean showFremennikProvince() { return true; }

    @ConfigItem(keyName="showFrogland", name="Frogland", description="Show implings in Frogland", position=66, section="locationFilters")
    default boolean showFrogland() { return true; }

    @ConfigItem(keyName="showGalvekShipwrecks", name="Galvek Shipwrecks", description="Show implings in Galvek Shipwrecks", position=67, section="locationFilters")
    default boolean showGalvekShipwrecks() { return true; }

    @ConfigItem(keyName="showGnomeStronghold", name="Gnome Stronghold", description="Show implings in Gnome Stronghold", position=68, section="locationFilters")
    default boolean showGnomeStronghold() { return true; }

    @ConfigItem(keyName="showGnomeVillage", name="Gnome Village", description="Show implings in Gnome Village", position=69, section="locationFilters")
    default boolean showGnomeVillage() { return true; }

    @ConfigItem(keyName="showGodWarsDungeon", name="God Wars Dungeon", description="Show implings in God Wars Dungeon", position=70, section="locationFilters")
    default boolean showGodWarsDungeon() { return true; }

    @ConfigItem(keyName="showGorakSPlane", name="Gorak's Plane", description="Show implings in Gorak's Plane", position=71, section="locationFilters")
    default boolean showGorakSPlane() { return true; }

    @ConfigItem(keyName="showGrandExchange", name="Grand Exchange", description="Show implings in Grand Exchange", position=72, section="locationFilters")
    default boolean showGrandExchange() { return true; }

    @ConfigItem(keyName="showGreatKourend", name="Great Kourend", description="Show implings in Great Kourend", position=73, section="locationFilters")
    default boolean showGreatKourend() { return true; }

    @ConfigItem(keyName="showGuTanoth", name="Gu'Tanoth", description="Show implings in Gu'Tanoth", position=74, section="locationFilters")
    default boolean showGuTanoth() { return true; }

    @ConfigItem(keyName="showGwenith", name="Gwenith", description="Show implings in Gwenith", position=75, section="locationFilters")
    default boolean showGwenith() { return true; }

    @ConfigItem(keyName="showHarmonyIsland", name="Harmony Island", description="Show implings in Harmony Island", position=76, section="locationFilters")
    default boolean showHarmonyIsland() { return true; }

    @ConfigItem(keyName="showHazelmereSIsland", name="Hazelmere's Island", description="Show implings in Hazelmere's Island", position=77, section="locationFilters")
    default boolean showHazelmereSIsland() { return true; }

    @ConfigItem(keyName="showHosidius", name="Hosidius", description="Show implings in Hosidius", position=78, section="locationFilters")
    default boolean showHosidius() { return true; }

    @ConfigItem(keyName="showIcePath", name="Ice Path", description="Show implings in Ice Path", position=79, section="locationFilters")
    default boolean showIcePath() { return true; }

    @ConfigItem(keyName="showIceberg", name="Iceberg", description="Show implings in Iceberg", position=80, section="locationFilters")
    default boolean showIceberg() { return true; }

    @ConfigItem(keyName="showIcyeneGraveyard", name="Icyene Graveyard", description="Show implings in Icyene Graveyard", position=81, section="locationFilters")
    default boolean showIcyeneGraveyard() { return true; }

    @ConfigItem(keyName="showIsafdar", name="Isafdar", description="Show implings in Isafdar", position=82, section="locationFilters")
    default boolean showIsafdar() { return true; }

    @ConfigItem(keyName="showIslandOfStone", name="Island of Stone", description="Show implings in Island of Stone", position=83, section="locationFilters")
    default boolean showIslandOfStone() { return true; }

    @ConfigItem(keyName="showIsleOfSouls", name="Isle of Souls", description="Show implings in Isle of Souls", position=84, section="locationFilters")
    default boolean showIsleOfSouls() { return true; }

    @ConfigItem(keyName="showJatizso", name="Jatizso", description="Show implings in Jatizso", position=85, section="locationFilters")
    default boolean showJatizso() { return true; }

    @ConfigItem(keyName="showJiggig", name="Jiggig", description="Show implings in Jiggig", position=86, section="locationFilters")
    default boolean showJiggig() { return true; }

    @ConfigItem(keyName="showKandarin", name="Kandarin", description="Show implings in Kandarin", position=87, section="locationFilters")
    default boolean showKandarin() { return true; }

    @ConfigItem(keyName="showKaramja", name="Karamja", description="Show implings in Karamja", position=88, section="locationFilters")
    default boolean showKaramja() { return true; }

    @ConfigItem(keyName="showKebosLowlands", name="Kebos Lowlands", description="Show implings in Kebos Lowlands", position=89, section="locationFilters")
    default boolean showKebosLowlands() { return true; }

    @ConfigItem(keyName="showKebosSwamp", name="Kebos Swamp", description="Show implings in Kebos Swamp", position=90, section="locationFilters")
    default boolean showKebosSwamp() { return true; }

    @ConfigItem(keyName="showKeldagrim", name="Keldagrim", description="Show implings in Keldagrim", position=91, section="locationFilters")
    default boolean showKeldagrim() { return true; }

    @ConfigItem(keyName="showKharaziJungle", name="Kharazi Jungle", description="Show implings in Kharazi Jungle", position=92, section="locationFilters")
    default boolean showKharaziJungle() { return true; }

    @ConfigItem(keyName="showKharidianDesert", name="Kharidian Desert", description="Show implings in Kharidian Desert", position=93, section="locationFilters")
    default boolean showKharidianDesert() { return true; }

    @ConfigItem(keyName="showKillerwattPlane", name="Killerwatt Plane", description="Show implings in Killerwatt Plane", position=94, section="locationFilters")
    default boolean showKillerwattPlane() { return true; }

    @ConfigItem(keyName="showKourendWoodland", name="Kourend Woodland", description="Show implings in Kourend Woodland", position=95, section="locationFilters")
    default boolean showKourendWoodland() { return true; }

    @ConfigItem(keyName="showLandSEnd", name="Land's End", description="Show implings in Land's End", position=96, section="locationFilters")
    default boolean showLandSEnd() { return true; }

    @ConfigItem(keyName="showLawAltar", name="Law Altar", description="Show implings in Law Altar", position=97, section="locationFilters")
    default boolean showLawAltar() { return true; }

    @ConfigItem(keyName="showLegendsGuild", name="Legends' Guild", description="Show implings in Legends' Guild", position=98, section="locationFilters")
    default boolean showLegendsGuild() { return true; }

    @ConfigItem(keyName="showLighthouse", name="Lighthouse", description="Show implings in Lighthouse", position=99, section="locationFilters")
    default boolean showLighthouse() { return true; }

    @ConfigItem(keyName="showLithkren", name="Lithkren", description="Show implings in Lithkren", position=100, section="locationFilters")
    default boolean showLithkren() { return true; }

    @ConfigItem(keyName="showLletya", name="Lletya", description="Show implings in Lletya", position=101, section="locationFilters")
    default boolean showLletya() { return true; }

    @ConfigItem(keyName="showLovakengj", name="Lovakengj", description="Show implings in Lovakengj", position=102, section="locationFilters")
    default boolean showLovakengj() { return true; }

    @ConfigItem(keyName="showLumbridge", name="Lumbridge", description="Show implings in Lumbridge", position=103, section="locationFilters")
    default boolean showLumbridge() { return true; }

    @ConfigItem(keyName="showLumbridgeSwamp", name="Lumbridge Swamp", description="Show implings in Lumbridge Swamp", position=104, section="locationFilters")
    default boolean showLumbridgeSwamp() { return true; }

    @ConfigItem(keyName="showLunarIsle", name="Lunar Isle", description="Show implings in Lunar Isle", position=105, section="locationFilters")
    default boolean showLunarIsle() { return true; }

    @ConfigItem(keyName="showMarim", name="Marim", description="Show implings in Marim", position=106, section="locationFilters")
    default boolean showMarim() { return true; }

    @ConfigItem(keyName="showMaxIsland", name="Max Island", description="Show implings in Max Island", position=107, section="locationFilters")
    default boolean showMaxIsland() { return true; }

    @ConfigItem(keyName="showMcGruborSWood", name="McGrubor's Wood", description="Show implings in McGrubor's Wood", position=108, section="locationFilters")
    default boolean showMcGruborSWood() { return true; }

    @ConfigItem(keyName="showMeiyerditch", name="Meiyerditch", description="Show implings in Meiyerditch", position=109, section="locationFilters")
    default boolean showMeiyerditch() { return true; }

    @ConfigItem(keyName="showMenaphos", name="Menaphos", description="Show implings in Menaphos", position=110, section="locationFilters")
    default boolean showMenaphos() { return true; }

    @ConfigItem(keyName="showMimeSStage", name="Mime's Stage", description="Show implings in Mime's Stage", position=111, section="locationFilters")
    default boolean showMimeSStage() { return true; }

    @ConfigItem(keyName="showMindAltar", name="Mind Altar", description="Show implings in Mind Altar", position=112, section="locationFilters")
    default boolean showMindAltar() { return true; }

    @ConfigItem(keyName="showMiscellania", name="Miscellania", description="Show implings in Miscellania", position=113, section="locationFilters")
    default boolean showMiscellania() { return true; }

    @ConfigItem(keyName="showMisthalin", name="Misthalin", description="Show implings in Misthalin", position=114, section="locationFilters")
    default boolean showMisthalin() { return true; }

    @ConfigItem(keyName="showMolch", name="Molch", description="Show implings in Molch", position=115, section="locationFilters")
    default boolean showMolch() { return true; }

    @ConfigItem(keyName="showMolchIsland", name="Molch Island", description="Show implings in Molch Island", position=116, section="locationFilters")
    default boolean showMolchIsland() { return true; }

    @ConfigItem(keyName="showMorUlRek", name="Mor Ul Rek", description="Show implings in Mor Ul Rek", position=117, section="locationFilters")
    default boolean showMorUlRek() { return true; }

    @ConfigItem(keyName="showMortTon", name="Mort'ton", description="Show implings in Mort'ton", position=118, section="locationFilters")
    default boolean showMortTon() { return true; }

    @ConfigItem(keyName="showMorytania", name="Morytania", description="Show implings in Morytania", position=119, section="locationFilters")
    default boolean showMorytania() { return true; }

    @ConfigItem(keyName="showMosLeHarmless", name="Mos Le'Harmless", description="Show implings in Mos Le'Harmless", position=120, section="locationFilters")
    default boolean showMosLeHarmless() { return true; }

    @ConfigItem(keyName="showMountKaruulm", name="Mount Karuulm", description="Show implings in Mount Karuulm", position=121, section="locationFilters")
    default boolean showMountKaruulm() { return true; }

    @ConfigItem(keyName="showMountQuidamortem", name="Mount Quidamortem", description="Show implings in Mount Quidamortem", position=122, section="locationFilters")
    default boolean showMountQuidamortem() { return true; }

    @ConfigItem(keyName="showMountainCamp", name="Mountain Camp", description="Show implings in Mountain Camp", position=123, section="locationFilters")
    default boolean showMountainCamp() { return true; }

    @ConfigItem(keyName="showMrMordautSClassroom", name="Mr. Mordaut's Classroom", description="Show implings in Mr. Mordaut's Classroom", position=124, section="locationFilters")
    default boolean showMrMordautSClassroom() { return true; }

    @ConfigItem(keyName="showMudskipperPoint", name="Mudskipper Point", description="Show implings in Mudskipper Point", position=125, section="locationFilters")
    default boolean showMudskipperPoint() { return true; }

    @ConfigItem(keyName="showMynydd", name="Mynydd", description="Show implings in Mynydd", position=126, section="locationFilters")
    default boolean showMynydd() { return true; }

    @ConfigItem(keyName="showMysteriousOldManSMaze", name="Mysterious Old Man's Maze", description="Show implings in Mysterious Old Man's Maze", position=127, section="locationFilters")
    default boolean showMysteriousOldManSMaze() { return true; }

    @ConfigItem(keyName="showMythsGuild", name="Myths' Guild", description="Show implings in Myths' Guild", position=128, section="locationFilters")
    default boolean showMythsGuild() { return true; }

    @ConfigItem(keyName="showNardah", name="Nardah", description="Show implings in Nardah", position=129, section="locationFilters")
    default boolean showNardah() { return true; }

    @ConfigItem(keyName="showNatureAltar", name="Nature Altar", description="Show implings in Nature Altar", position=130, section="locationFilters")
    default boolean showNatureAltar() { return true; }

    @ConfigItem(keyName="showNeitiznot", name="Neitiznot", description="Show implings in Neitiznot", position=131, section="locationFilters")
    default boolean showNeitiznot() { return true; }

    @ConfigItem(keyName="showNorthernTundras", name="Northern Tundras", description="Show implings in Northern Tundras", position=132, section="locationFilters")
    default boolean showNorthernTundras() { return true; }

    @ConfigItem(keyName="showObservatory", name="Observatory", description="Show implings in Observatory", position=133, section="locationFilters")
    default boolean showObservatory() { return true; }

    @ConfigItem(keyName="showOddOneOut", name="Odd One Out", description="Show implings in Odd One Out", position=134, section="locationFilters")
    default boolean showOddOneOut() { return true; }

    @ConfigItem(keyName="showOttoSGrotto", name="Otto's Grotto", description="Show implings in Otto's Grotto", position=135, section="locationFilters")
    default boolean showOttoSGrotto() { return true; }

    @ConfigItem(keyName="showOuraniaHunterArea", name="Ourania Hunter Area", description="Show implings in Ourania Hunter Area", position=136, section="locationFilters")
    default boolean showOuraniaHunterArea() { return true; }

    @ConfigItem(keyName="showPiratesCove", name="Pirates' Cove", description="Show implings in Pirates' Cove", position=137, section="locationFilters")
    default boolean showPiratesCove() { return true; }

    @ConfigItem(keyName="showPiscatoris", name="Piscatoris", description="Show implings in Piscatoris", position=138, section="locationFilters")
    default boolean showPiscatoris() { return true; }

    @ConfigItem(keyName="showPiscatorisHunterArea", name="Piscatoris Hunter Area", description="Show implings in Piscatoris Hunter Area", position=139, section="locationFilters")
    default boolean showPiscatorisHunterArea() { return true; }

    @ConfigItem(keyName="showPlayerOwnedHouse", name="Player Owned House", description="Show implings in Player Owned House", position=140, section="locationFilters")
    default boolean showPlayerOwnedHouse() { return true; }

    @ConfigItem(keyName="showPoisonWaste", name="Poison Waste", description="Show implings in Poison Waste", position=141, section="locationFilters")
    default boolean showPoisonWaste() { return true; }

    @ConfigItem(keyName="showPollnivneach", name="Pollnivneach", description="Show implings in Pollnivneach", position=142, section="locationFilters")
    default boolean showPollnivneach() { return true; }

    @ConfigItem(keyName="showPortKhazard", name="Port Khazard", description="Show implings in Port Khazard", position=143, section="locationFilters")
    default boolean showPortKhazard() { return true; }

    @ConfigItem(keyName="showPortPhasmatys", name="Port Phasmatys", description="Show implings in Port Phasmatys", position=144, section="locationFilters")
    default boolean showPortPhasmatys() { return true; }

    @ConfigItem(keyName="showPortPiscarilius", name="Port Piscarilius", description="Show implings in Port Piscarilius", position=145, section="locationFilters")
    default boolean showPortPiscarilius() { return true; }

    @ConfigItem(keyName="showPortSarim", name="Port Sarim", description="Show implings in Port Sarim", position=146, section="locationFilters")
    default boolean showPortSarim() { return true; }

    @ConfigItem(keyName="showPortTyras", name="Port Tyras", description="Show implings in Port Tyras", position=147, section="locationFilters")
    default boolean showPortTyras() { return true; }

    @ConfigItem(keyName="showPrifddinas", name="Prifddinas", description="Show implings in Prifddinas", position=148, section="locationFilters")
    default boolean showPrifddinas() { return true; }

    @ConfigItem(keyName="showPuroPuro", name="Puro Puro", description="Show implings in Puro Puro", position=149, section="locationFilters")
    default boolean showPuroPuro() { return true; }

    @ConfigItem(keyName="showQuarry", name="Quarry", description="Show implings in Quarry", position=150, section="locationFilters")
    default boolean showQuarry() { return true; }

    @ConfigItem(keyName="showRangingGuild", name="Ranging Guild", description="Show implings in Ranging Guild", position=151, section="locationFilters")
    default boolean showRangingGuild() { return true; }

    @ConfigItem(keyName="showRatcatchersMansion", name="Ratcatchers Mansion", description="Show implings in Ratcatchers Mansion", position=152, section="locationFilters")
    default boolean showRatcatchersMansion() { return true; }

    @ConfigItem(keyName="showRellekka", name="Rellekka", description="Show implings in Rellekka", position=153, section="locationFilters")
    default boolean showRellekka() { return true; }

    @ConfigItem(keyName="showRimmington", name="Rimmington", description="Show implings in Rimmington", position=154, section="locationFilters")
    default boolean showRimmington() { return true; }

    @ConfigItem(keyName="showRuinsOfUnkah", name="Ruins of Unkah", description="Show implings in Ruins of Unkah", position=155, section="locationFilters")
    default boolean showRuinsOfUnkah() { return true; }

    @ConfigItem(keyName="showRuneEssenceMine", name="Rune Essence Mine", description="Show implings in Rune Essence Mine", position=156, section="locationFilters")
    default boolean showRuneEssenceMine() { return true; }

    @ConfigItem(keyName="showScapeRune", name="ScapeRune", description="Show implings in ScapeRune", position=157, section="locationFilters")
    default boolean showScapeRune() { return true; }

    @ConfigItem(keyName="showSeaSpiritDock", name="Sea Spirit Dock", description="Show implings in Sea Spirit Dock", position=158, section="locationFilters")
    default boolean showSeaSpiritDock() { return true; }

    @ConfigItem(keyName="showSeersVillage", name="Seers' Village", description="Show implings in Seers' Village", position=159, section="locationFilters")
    default boolean showSeersVillage() { return true; }

    @ConfigItem(keyName="showShayzien", name="Shayzien", description="Show implings in Shayzien", position=160, section="locationFilters")
    default boolean showShayzien() { return true; }

    @ConfigItem(keyName="showShiloVillage", name="Shilo Village", description="Show implings in Shilo Village", position=161, section="locationFilters")
    default boolean showShiloVillage() { return true; }

    @ConfigItem(keyName="showShipYard", name="Ship Yard", description="Show implings in Ship Yard", position=162, section="locationFilters")
    default boolean showShipYard() { return true; }

    @ConfigItem(keyName="showSilvarea", name="Silvarea", description="Show implings in Silvarea", position=163, section="locationFilters")
    default boolean showSilvarea() { return true; }

    @ConfigItem(keyName="showSinclairMansion", name="Sinclair Mansion", description="Show implings in Sinclair Mansion", position=164, section="locationFilters")
    default boolean showSinclairMansion() { return true; }

    @ConfigItem(keyName="showSlayerTower", name="Slayer Tower", description="Show implings in Slayer Tower", position=165, section="locationFilters")
    default boolean showSlayerTower() { return true; }

    @ConfigItem(keyName="showSlepe", name="Slepe", description="Show implings in Slepe", position=166, section="locationFilters")
    default boolean showSlepe() { return true; }

    @ConfigItem(keyName="showSophanem", name="Sophanem", description="Show implings in Sophanem", position=167, section="locationFilters")
    default boolean showSophanem() { return true; }

    @ConfigItem(keyName="showSoulAltar", name="Soul Altar", description="Show implings in Soul Altar", position=168, section="locationFilters")
    default boolean showSoulAltar() { return true; }

    @ConfigItem(keyName="showTaiBwoWannai", name="Tai Bwo Wannai", description="Show implings in Tai Bwo Wannai", position=169, section="locationFilters")
    default boolean showTaiBwoWannai() { return true; }

    @ConfigItem(keyName="showTaverley", name="Taverley", description="Show implings in Taverley", position=170, section="locationFilters")
    default boolean showTaverley() { return true; }

    @ConfigItem(keyName="showTheForsakenTower", name="The Forsaken Tower", description="Show implings in The Forsaken Tower", position=171, section="locationFilters")
    default boolean showTheForsakenTower() { return true; }

    @ConfigItem(keyName="showTrollArena", name="Troll Arena", description="Show implings in Troll Arena", position=172, section="locationFilters")
    default boolean showTrollArena() { return true; }

    @ConfigItem(keyName="showTrollStronghold", name="Troll Stronghold", description="Show implings in Troll Stronghold", position=173, section="locationFilters")
    default boolean showTrollStronghold() { return true; }

    @ConfigItem(keyName="showTrollheim", name="Trollheim", description="Show implings in Trollheim", position=174, section="locationFilters")
    default boolean showTrollheim() { return true; }

    @ConfigItem(keyName="showTrollweissMountain", name="Trollweiss Mountain", description="Show implings in Trollweiss Mountain", position=175, section="locationFilters")
    default boolean showTrollweissMountain() { return true; }

    @ConfigItem(keyName="showTutorialIsland", name="Tutorial Island", description="Show implings in Tutorial Island", position=176, section="locationFilters")
    default boolean showTutorialIsland() { return true; }

    @ConfigItem(keyName="showUnderwater", name="Underwater", description="Show implings in Underwater", position=177, section="locationFilters")
    default boolean showUnderwater() { return true; }

    @ConfigItem(keyName="showUzer", name="Uzer", description="Show implings in Uzer", position=178, section="locationFilters")
    default boolean showUzer() { return true; }

    @ConfigItem(keyName="showVarrock", name="Varrock", description="Show implings in Varrock", position=179, section="locationFilters")
    default boolean showVarrock() { return true; }

    @ConfigItem(keyName="showVerSinhaza", name="Ver Sinhaza", description="Show implings in Ver Sinhaza", position=180, section="locationFilters")
    default boolean showVerSinhaza() { return true; }

    @ConfigItem(keyName="showVoidKnightsOutpost", name="Void Knights' Outpost", description="Show implings in Void Knights' Outpost", position=181, section="locationFilters")
    default boolean showVoidKnightsOutpost() { return true; }

    @ConfigItem(keyName="showWaterAltar", name="Water Altar", description="Show implings in Water Altar", position=182, section="locationFilters")
    default boolean showWaterAltar() { return true; }

    @ConfigItem(keyName="showWaterbirthIsland", name="Waterbirth Island", description="Show implings in Waterbirth Island", position=183, section="locationFilters")
    default boolean showWaterbirthIsland() { return true; }

    @ConfigItem(keyName="showWeiss", name="Weiss", description="Show implings in Weiss", position=184, section="locationFilters")
    default boolean showWeiss() { return true; }

    @ConfigItem(keyName="showWintertodtCamp", name="Wintertodt Camp", description="Show implings in Wintertodt Camp", position=185, section="locationFilters")
    default boolean showWintertodtCamp() { return true; }

    @ConfigItem(keyName="showWitchaven", name="Witchaven", description="Show implings in Witchaven", position=186, section="locationFilters")
    default boolean showWitchaven() { return true; }

    @ConfigItem(keyName="showWizardsTower", name="Wizards' Tower", description="Show implings in Wizards' Tower", position=187, section="locationFilters")
    default boolean showWizardsTower() { return true; }

    @ConfigItem(keyName="showWoodcuttingGuild", name="Woodcutting Guild", description="Show implings in Woodcutting Guild", position=188, section="locationFilters")
    default boolean showWoodcuttingGuild() { return true; }

    @ConfigItem(keyName="showWrathAltar", name="Wrath Altar", description="Show implings in Wrath Altar", position=189, section="locationFilters")
    default boolean showWrathAltar() { return true; }

    @ConfigItem(keyName="showYanille", name="Yanille", description="Show implings in Yanille", position=190, section="locationFilters")
    default boolean showYanille() { return true; }

    @ConfigItem(keyName="showZanaris", name="Zanaris", description="Show implings in Zanaris", position=191, section="locationFilters")
    default boolean showZanaris() { return true; }

    @ConfigItem(keyName="showZulAndra", name="Zul-Andra", description="Show implings in Zul-Andra", position=192, section="locationFilters")
    default boolean showZulAndra() { return true; }
}
