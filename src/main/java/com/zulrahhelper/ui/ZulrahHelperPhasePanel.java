package com.zulrahhelper.ui;

import com.zulrahhelper.Phase;
import com.zulrahhelper.ZulrahHelperConfig;
import com.zulrahhelper.ZulrahHelperPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class ZulrahHelperPhasePanel extends JPanel implements MouseListener {

    private final ZulrahHelperPlugin plugin;
    private final Phase phase;
    private final JLabel picLabel;
    private final ImageIcon phaseIcon;
    private final ImageIcon phaseIconHover;

    ZulrahHelperPhasePanel(ZulrahHelperPlugin plugin, Phase phase, int columns) {
        this.plugin = plugin;
        this.phase = phase;

        BufferedImage img = processImg(phase.getImage(plugin.getConfig()), columns);
        phaseIcon = new ImageIcon(img);
        phaseIconHover = new ImageIcon(ImageUtil.luminanceScale(img, .75f));

        picLabel = new JLabel(phaseIcon);
        if (phase.isSelectable()) {
            picLabel.addMouseListener(this);
        }

        if (phase.isCurrent()) {
            setBorder(new LineBorder(ColorScheme.PROGRESS_COMPLETE_COLOR));

        }
        if (columns >= 2) {
            setBorder(new LineBorder(ColorScheme.PROGRESS_INPROGRESS_COLOR));
        }
        add(picLabel);
    }

    private BufferedImage processImg(BufferedImage img, int columns) {
        int size = 95;
        // make the images smaller if we're showing more than 3 images on the same row
        if (columns >= 3) {
            size = 60;
        }
        img = ImageUtil.resizeImage(img, size, size);
        if (phase.isCompleted()) {
            img = ImageUtil.luminanceScale(img, 0.40f);
        }

        return img;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        plugin.setState(phase);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        picLabel.setIcon(phaseIconHover);
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        picLabel.setIcon(phaseIcon);
    }
}
