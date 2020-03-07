package com.zulrahhelper.ui;

import com.zulrahhelper.Phase;
import com.zulrahhelper.ZulrahHelperPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.function.Predicate;

public class ZulrahHelperPhasePanel extends JPanel implements MouseListener {

    private final ZulrahHelperPlugin plugin;
    private final Phase phase;

    ZulrahHelperPhasePanel(ZulrahHelperPlugin plugin, Phase phase, int rows) {
        this.plugin = plugin;
        this.phase = phase;

        JLabel picLabel = new JLabel(new ImageIcon(processImg(phase.getImage(), rows)));
        if (phase.isSelectable()) {
            picLabel.addMouseListener(this);
        }

        if (phase.isCurrent()) {
            setBorder(new LineBorder(ColorScheme.PROGRESS_COMPLETE_COLOR));

        }
        if (rows >= 2) {
            setBorder(new LineBorder(ColorScheme.PROGRESS_INPROGRESS_COLOR));
        }
        add(picLabel);
    }

    private BufferedImage processImg(BufferedImage img, int rows) {
        int size = 95;
        if (rows >= 3) {
            size = 60;
        }
        img = ImageUtil.resizeImage(img, size, size);
        if (phase.isCompleted()) {
            img = ImageUtil.luminanceScale(img, 0.50f);
        }
        return img;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        plugin.setState(phase);
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
