package com.hablapatabla.implingfinder.ui;

import com.hablapatabla.implingfinder.model.ImplingFinderData;
import com.hablapatabla.implingfinder.model.ImplingFinderEnum;
import com.hablapatabla.implingfinder.ImplingFinderPlugin;
import com.hablapatabla.implingfinder.model.ImplingFinderRegion;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.AsyncBufferedImage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ImplingFinderImpPanel extends JPanel {
    private static final Dimension ICON_SIZE = new Dimension(32, 32);

    // Tracks which panel was last clicked so it can be visually highlighted
    // and so a second click on the same panel can toggle the highlight off,
    // matching the existing toggle behaviour of the map marker itself.
    private static ImplingFinderImpPanel selectedPanel = null;

    @Inject
    private WorldMapPointManager worldMapPointManager;

    // npc id to ItemID
    // Expanded from 5 entries (Magpie, Ninja, Crystal, Dragon, Lucky) to all 13
    // impling types now that ImplingFinderEnum tracks every type.
    private static Map<Integer, Integer> thumbnails = new HashMap<Integer, Integer>() {{
        put(NpcID.BABY_IMPLING, ItemID.BABY_IMPLING_JAR);
        put(NpcID.YOUNG_IMPLING, ItemID.YOUNG_IMPLING_JAR);
        put(NpcID.GOURMET_IMPLING, ItemID.GOURMET_IMPLING_JAR);
        put(NpcID.EARTH_IMPLING, ItemID.EARTH_IMPLING_JAR);
        put(NpcID.ESSENCE_IMPLING, ItemID.ESSENCE_IMPLING_JAR);
        put(NpcID.ECLECTIC_IMPLING, ItemID.ECLECTIC_IMPLING_JAR);
        put(NpcID.NATURE_IMPLING, ItemID.NATURE_IMPLING_JAR);
        put(NpcID.MAGPIE_IMPLING, ItemID.MAGPIE_IMPLING_JAR);
        put(NpcID.NINJA_IMPLING, ItemID.NINJA_IMPLING_JAR);
        put(NpcID.CRYSTAL_IMPLING, ItemID.CRYSTAL_IMPLING_JAR);
        put(NpcID.DRAGON_IMPLING, ItemID.DRAGON_IMPLING_JAR);
        put(NpcID.LUCKY_IMPLING, ItemID.LUCKY_IMPLING_JAR);
    }};

    private static final String[] COMPASS_DIRECTIONS = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

    private Logger logger = LoggerFactory.getLogger(ImplingFinderImpPanel.class);
    private List<JPanel> panels = new ArrayList<>();

    protected ImplingFinderPlugin plugin;

    public static int getItemIdFromNpcId(int id) {
        Integer itemId = thumbnails.get(id);
        // Fallback to a generic jar icon if a type is somehow missing
        // from the map, instead of throwing a NullPointerException.
        return itemId != null ? itemId : ItemID.BABY_IMPLING_JAR;
    }

    /**
     * Computes the compass direction (N, NE, E, SE, S, SW, W, NW) from one
     * point to another using standard bearing math: angle = atan2(dx, dy),
     * measured clockwise from north, then snapped to the nearest of 8
     * compass points.
     */
    private static String compassDirection(WorldPoint from, WorldPoint to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        double angle = Math.toDegrees(Math.atan2(dx, dy));
        if (angle < 0)
            angle += 360;
        int index = (int) Math.round(angle / 45.0) % 8;
        return COMPASS_DIRECTIONS[index];
    }

    ImplingFinderImpPanel(ItemManager manager, ImplingFinderData data, Integer defaultId, ImplingFinderPlugin plugin, WorldPoint playerLocation) {
        this.plugin = plugin;
        Color background = getBackground();
        BorderLayout layout = new BorderLayout();
        layout.setHgap(5);
        setLayout(layout);
        setToolTipText(ImplingFinderEnum.findById(data.getNpcid()).getName());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        panels.add(this);
        setBorder(new EmptyBorder(7, 0, 0, 0));


        WorldPoint implingWorldPoint = new WorldPoint(data.getXcoord(), data.getYcoord(), data.getPlane());

        MouseAdapter itemPanelMouseListener = new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (selectedPanel == ImplingFinderImpPanel.this)
                    return;
                for (JPanel p : panels)
                    matchComponentBackground(p, ColorScheme.DARK_GRAY_HOVER_COLOR);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (selectedPanel == ImplingFinderImpPanel.this)
                    return;
                for (JPanel p : panels)
                    matchComponentBackground(p, ColorScheme.DARKER_GRAY_COLOR);
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                plugin.addMapPoints(implingWorldPoint);

                // Toggle highlight: clicking the already-selected panel clears
                // the highlight (matches the map marker's own toggle behaviour),
                // clicking a different panel moves the highlight to it.
                ImplingFinderImpPanel previouslySelected = selectedPanel;
                if (previouslySelected != null) {
                    for (JPanel p : previouslySelected.panels)
                        previouslySelected.matchComponentBackground(p, ColorScheme.DARKER_GRAY_COLOR);
                }

                if (previouslySelected == ImplingFinderImpPanel.this) {
                    selectedPanel = null;
                } else {
                    selectedPanel = ImplingFinderImpPanel.this;
                    for (JPanel p : panels)
                        matchComponentBackground(p, ColorScheme.DARK_GRAY_HOVER_COLOR.brighter());
                }
            }
        };

        addMouseListener(itemPanelMouseListener);

        final JLabel itemIcon = new JLabel();
        itemIcon.setPreferredSize(ICON_SIZE);
        Integer id = thumbnails.get(data.getNpcid());
        AsyncBufferedImage icon;
        if (id != null)
            manager.getImage(id).addTo(itemIcon);
        else
            manager.getImage(defaultId).addTo(itemIcon);

        JPanel iconPanel = new JPanel();
        panels.add(iconPanel);
        iconPanel.add(itemIcon);
        add(iconPanel, BorderLayout.LINE_START);


        JPanel rightPanel = new JPanel(new GridLayout(4, 1));
        panels.add(rightPanel);
        rightPanel.setBackground(background);

        JLabel itemName = new JLabel();
        itemName.setForeground(Color.WHITE);
        itemName.setMaximumSize(new Dimension(0, 0));        // to limit the label's size for
        itemName.setPreferredSize(new Dimension(0, 0));    // items with longer names
        itemName.setText(ImplingFinderEnum.findById(data.getNpcid()).getName());
        rightPanel.add(itemName);

        JPanel middleTextPanel = new JPanel(new BorderLayout());
        middleTextPanel.setBackground(background);
        panels.add(middleTextPanel);

        JLabel middleLeftTextLabel = new JLabel();
        middleLeftTextLabel.setForeground(ColorScheme.GRAND_EXCHANGE_PRICE);

        middleLeftTextLabel.setText("World: " + data.getWorld());
        middleTextPanel.add(middleLeftTextLabel, BorderLayout.WEST);

        JLabel middleRightTextLabel = new JLabel();
        middleRightTextLabel.setForeground(ColorScheme.GRAND_EXCHANGE_ALCH);
        String day;
        long daysBetween = ChronoUnit.DAYS.between(data.getDiscoveredtime(), Instant.now());
        if (daysBetween == 0)
            day = "Today";
        else if (daysBetween == 1)
            day = "Yesterday";
        else
            day = daysBetween + " days ago";

        middleRightTextLabel.setText(day);
        middleTextPanel.add(middleRightTextLabel, BorderLayout.EAST);
        rightPanel.add(middleTextPanel);

        // New row: shows compass direction + tile distance from the player's
        // current location to this impling, e.g. "NE, 340 tiles". Only shown
        // when the player's location and the impling are on the same plane,
        // since cross-plane distance/direction isn't meaningful.
        JPanel directionRowPanel = new JPanel(new BorderLayout());
        directionRowPanel.setBackground(background);
        panels.add(directionRowPanel);

        JLabel directionLabel = new JLabel();
        directionLabel.setForeground(ColorScheme.GRAND_EXCHANGE_PRICE);
        if (playerLocation != null && playerLocation.getPlane() == implingWorldPoint.getPlane()) {
            String direction = compassDirection(playerLocation, implingWorldPoint);
            int distance = (int) Math.round(Math.sqrt(
                    Math.pow(implingWorldPoint.getX() - playerLocation.getX(), 2) +
                    Math.pow(implingWorldPoint.getY() - playerLocation.getY(), 2)));
            directionLabel.setText(direction + ", " + distance + " tiles");
        } else {
            directionLabel.setText("Different area");
        }
        directionRowPanel.add(directionLabel, BorderLayout.WEST);
        rightPanel.add(directionRowPanel);

        JPanel bottomTextRowPanel = new JPanel(new BorderLayout());
        bottomTextRowPanel.setBackground(background);
        panels.add(bottomTextRowPanel);

        JLabel bottomLeftTextLabel = new JLabel();
        String foundTime = data.getDiscoveredtime().atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.LONG));
        bottomLeftTextLabel.setText(foundTime);
        bottomLeftTextLabel.setForeground(ColorScheme.GRAND_EXCHANGE_ALCH);
        bottomTextRowPanel.add(bottomLeftTextLabel, BorderLayout.WEST);

        final int playerRegionId = implingWorldPoint.getRegionID();
        String location;
        if (ImplingFinderRegion.fromRegion(playerRegionId) != null)
            location = ImplingFinderRegion.fromRegion(playerRegionId).getName();
        else
            location = "Unknown";
        location = StringUtils.abbreviate(location, 12);

        JLabel bottomRightTextLabel = new JLabel();
        bottomRightTextLabel.setText(location);
        bottomRightTextLabel.setForeground(ColorScheme.GRAND_EXCHANGE_LIMIT);
        bottomRightTextLabel.setBorder(new CompoundBorder(bottomRightTextLabel.getBorder(), new EmptyBorder(0, 0, 0, 7)));
        bottomTextRowPanel.add(bottomRightTextLabel, BorderLayout.EAST);

        rightPanel.add(bottomTextRowPanel);

        for (JPanel p : panels)
            matchComponentBackground(p, ColorScheme.DARKER_GRAY_COLOR);

        add(rightPanel, BorderLayout.CENTER);
    }

    private void matchComponentBackground(JPanel panel, Color color) {
        panel.setBackground(color);
        for (Component c : panel.getComponents()) {
            c.setBackground(color);
        }
    }
}
