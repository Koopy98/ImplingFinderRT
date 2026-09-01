package com.hablapatabla.implingfinder.ui;
import com.hablapatabla.implingfinder.model.ImplingFinderEnum;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.ColorScheme;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
@SuppressWarnings("serial")
class ImplingFinderButton extends JButton {
    @Getter
    @Setter
    private boolean selected;
    @Getter
    @Setter
    private String name;

    ImplingFinderButton(Image i, String name) {
        super.setContentAreaFilled(false);
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setFocusPainted(false);
        this.setRequestFocusEnabled(false);
        this.selected = false;
        this.name = name;
        this.setIcon(new ImageIcon(i));
        setBorder(new EtchedBorder());
        // Previously these buttons had no tooltip, so users had to guess
        // which impling type each jar icon represented. Now hovering shows
        // the type name plus its notable drops (see ImplingFinderEnum.DROP_INFO,
        // the single shared source of truth also used by the Drops panel).
        // "Recent" is a special filter rather than an impling type.
        if (name.equals("Recent")) {
            this.setToolTipText("Show all recent implings");
        } else {
            String dropInfo = ImplingFinderEnum.DROP_INFO.get(name);
            this.setToolTipText(dropInfo != null
                ? "<html>" + name + " impling<br>" + dropInfo + "</html>"
                : name + " impling");
        }
    }

    @Override
    public void paint(Graphics g) {
        Color oldFg = getForeground();
        Color newFg = oldFg;
        ButtonModel mod = getModel();
        if (mod.isPressed()) {
            g.setColor(ColorScheme.DARK_GRAY_COLOR);
        } else if (mod.isRollover())
            g.setColor(ColorScheme.DARKER_GRAY_HOVER_COLOR);
        else {
            if (selected)
                g.setColor(ColorScheme.DARKER_GRAY_COLOR);
            else
                g.setColor(ColorScheme.DARK_GRAY_COLOR);
        }
        g.fillRect(0, 0, getWidth(), getHeight());
        setForeground(newFg);
        super.paintComponent(g);
        setForeground(oldFg);
    }
}
