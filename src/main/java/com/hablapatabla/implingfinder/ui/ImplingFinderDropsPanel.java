package com.hablapatabla.implingfinder.ui;

import com.hablapatabla.implingfinder.model.ImplingFinderEnum;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Static reference panel listing notable drops (clue tiers, rates, unique
 * loot) for every impling type. Built once from ImplingFinderEnum.DROP_INFO -
 * the same data source the filter button tooltips use - so this and the
 * tooltips can never drift out of sync with each other.
 */
class ImplingFinderDropsPanel extends JPanel {

    // Kept in a sensible order (weakest to strongest) rather than whatever
    // order the underlying HashMap happens to iterate in.
    private static final String[] ORDER = {
        "Baby", "Young", "Gourmet", "Earth", "Essence", "Eclectic",
        "Nature", "Magpie", "Ninja", "Crystal", "Dragon", "Lucky"
    };

    ImplingFinderDropsPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        listPanel.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel heading = new JLabel("Notable Drops by Type");
        heading.setForeground(Color.WHITE);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 14f));
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.setBorder(new EmptyBorder(0, 0, 8, 0));
        listPanel.add(heading);

        for (String type : ORDER) {
            String info = ImplingFinderEnum.DROP_INFO.get(type);
            if (info == null) continue;

            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            row.setBorder(new EmptyBorder(6, 8, 6, 8));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            JLabel typeLabel = new JLabel(type);
            typeLabel.setForeground(ColorScheme.GRAND_EXCHANGE_PRICE);
            typeLabel.setFont(typeLabel.getFont().deriveFont(Font.BOLD));

            JLabel infoLabel = new JLabel("<html><body style='width: 180px'>" + info + "</body></html>");
            infoLabel.setForeground(Color.LIGHT_GRAY);

            row.add(typeLabel, BorderLayout.NORTH);
            row.add(infoLabel, BorderLayout.CENTER);

            listPanel.add(row);
            listPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        }

        JLabel footnote = new JLabel(
            "<html><body style='width: 180px'>Rates verified against the OSRS Wiki. " +
            "Where no exact rate is listed, that's stated rather than guessed.</body></html>");
        footnote.setForeground(Color.GRAY);
        footnote.setBorder(new EmptyBorder(10, 0, 0, 0));
        footnote.setAlignmentX(Component.LEFT_ALIGNMENT);
        listPanel.add(footnote);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(ColorScheme.DARK_GRAY_COLOR);

        add(scrollPane, BorderLayout.CENTER);
    }
}
