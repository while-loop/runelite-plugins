package com.ttl.ui;

import com.ttl.TimeToLevelPlugin;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;


public class LevelsListPanelHeader extends JPanel {
    private static final ImageIcon REFRESH_ICON;
    private static final ImageIcon REFRESH_ICON_HOVER;
    private static final ImageIcon HELP_ICON;
    private static final ImageIcon HELP_ICON_HOVER;
    private static final ImageIcon CONFIG_ICON;
    private static final ImageIcon CONFIG_ICON_HOVER;

    static {
        final BufferedImage refreshIcon = ImageUtil.getResourceStreamFromClass(TimeToLevelPlugin.class, "/refresh_icon.png");
        REFRESH_ICON = new ImageIcon(refreshIcon);
        REFRESH_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(refreshIcon, 0.53f));

        final BufferedImage helpIcon = ImageUtil.getResourceStreamFromClass(TimeToLevelPlugin.class, "/help_icon.png");
        HELP_ICON = new ImageIcon(helpIcon);
        HELP_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(helpIcon, 0.53f));

        final BufferedImage configIcon = ImageUtil.getResourceStreamFromClass(TimeToLevelPlugin.class, "/config_icon.png");
        CONFIG_ICON = new ImageIcon(configIcon);
        CONFIG_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(configIcon, 0.53f));
    }

    public LevelsListPanelHeader(Runnable onRefresh, Runnable showRatesConfig) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8, 16, 8, 8));

        JButton refreshBtn = new JButton(REFRESH_ICON);
        refreshBtn.setRolloverIcon(REFRESH_ICON_HOVER);
        SwingUtil.removeButtonDecorations(refreshBtn);
        refreshBtn.setToolTipText("Refresh");
        refreshBtn.addActionListener(e -> onRefresh.run());

        JButton helpBtn = new JButton(HELP_ICON);
        helpBtn.setRolloverIcon(HELP_ICON_HOVER);
        SwingUtil.removeButtonDecorations(helpBtn);
        helpBtn.setToolTipText("Help");
        helpBtn.addActionListener(e -> LinkBrowser.browse("https://github.com/while-loop/runelite-plugins/tree/ttl#time-to-level"));
        JLabel title = new JLabel("Time to Level");
        title.setForeground(Color.WHITE);

        JButton configBtn = new JButton(CONFIG_ICON);
        SwingUtil.removeButtonDecorations(configBtn);
        configBtn.setRolloverIcon(CONFIG_ICON_HOVER);
        configBtn.setToolTipText("Edit XP Rates");
        configBtn.addActionListener(e -> showRatesConfig.run());

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        leftActions.add(title);
        leftActions.add(helpBtn);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightActions.add(configBtn);
        rightActions.add(refreshBtn);

        add(leftActions, BorderLayout.WEST);
        add(rightActions, BorderLayout.EAST);
    }
}
