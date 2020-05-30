package com.ttl.ui;

import com.ttl.ImgUtils;
import com.ttl.TimeToLevelPlugin;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;


public class SkillRatePanelHeader extends JPanel {
    private static final ImageIcon CANCEL_ICON;
    private static final ImageIcon CANCEL_ICON_HOVER;
    private static final ImageIcon CONFIRM_ICON;
    private static final ImageIcon CONFIRM_ICON_HOVER;
    private static final ImageIcon BACK_ICON;
    private static final ImageIcon BACK_ICON_HOVER;

    static {
        final BufferedImage cancelIcon = ImageUtil.getResourceStreamFromClass(TimeToLevelPlugin.class, "/cancel_icon.png");
        CANCEL_ICON = new ImageIcon(cancelIcon);
        CANCEL_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(cancelIcon, 0.53f));

        final BufferedImage confirmIcon = ImageUtil.getResourceStreamFromClass(TimeToLevelPlugin.class, "/confirm_icon.png");
        CONFIRM_ICON = new ImageIcon(confirmIcon);
        CONFIRM_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(confirmIcon, 0.53f));

        final BufferedImage backIcon = ImageUtil.getResourceStreamFromClass(TimeToLevelPlugin.class, "/back_icon.png");
        BACK_ICON = new ImageIcon(backIcon);
        BACK_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(backIcon, 0.53f));
    }

    public SkillRatePanelHeader(String skill, Runnable onBack, Runnable onSave) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8, 4, 8, 0));

        BufferedImage skillImg = ImgUtils.getSkillImage(skill, true);

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));

        {
            JButton backBtn = new JButton(BACK_ICON);
            backBtn.setRolloverIcon(BACK_ICON_HOVER);
            SwingUtil.removeButtonDecorations(backBtn);
            backBtn.addActionListener(e -> onBack.run());
            backBtn.setToolTipText("Back");

            JLabel skillLabel = new JLabel(new ImageIcon(skillImg));
            skillLabel.setToolTipText(StringUtils.capitalize(skill));

            leftActions.add(backBtn);
            leftActions.add(skillLabel);
        }

        {
            JButton confirmBtn = new JButton(CONFIRM_ICON);
            confirmBtn.setRolloverIcon(CONFIRM_ICON_HOVER);
            SwingUtil.removeButtonDecorations(confirmBtn);
            confirmBtn.setToolTipText("Confirm");
            confirmBtn.addActionListener(e -> onSave.run());

            JButton cancelBtn = new JButton(CANCEL_ICON);
            cancelBtn.setRolloverIcon(CANCEL_ICON_HOVER);
            SwingUtil.removeButtonDecorations(cancelBtn);
            cancelBtn.addActionListener(e -> {
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                        "Are you sure you discard changes?", "Discard Changes",
                        JOptionPane.YES_NO_OPTION)) {
                    onBack.run();
                }
            });
            cancelBtn.setToolTipText("Cancel");

            rightActions.add(confirmBtn, BorderLayout.EAST);
            rightActions.add(cancelBtn, BorderLayout.WEST);
        }

        add(leftActions, BorderLayout.WEST);
        add(rightActions, BorderLayout.EAST);

    }
}
