package com.zulrahhelper.ui;

import com.zulrahhelper.Phase;
import com.zulrahhelper.State;
import com.zulrahhelper.ZulrahHelperPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@Slf4j
@Singleton
public class ZulrahHelperPanel extends PluginPanel {
    private final ZulrahHelperPlugin plugin;
    private final JPanel phasesView = new JPanel(new GridBagLayout());

    private static final ImageIcon RESET_ICON;
    private static final ImageIcon RESET_HOVER_ICON;

    static {
        final BufferedImage addIcon = ImageUtil.getResourceStreamFromClass(ZulrahHelperPlugin.class, "/ui/reset_icon.png");
        RESET_ICON = new ImageIcon(addIcon);
        RESET_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(addIcon, 0.53f));
    }

    private final JLabel title = new JLabel();
    private final JButton reset = new JButton(RESET_ICON);

    public ZulrahHelperPanel(ZulrahHelperPlugin plugin) {
        this.plugin = plugin;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBorder(new EmptyBorder(1, 0, 10, 0));

        title.setText("Zulrah Helper");
        title.setForeground(Color.WHITE);

        northPanel.add(title, BorderLayout.WEST);
        northPanel.add(reset, BorderLayout.EAST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        phasesView.setBackground(ColorScheme.DARK_GRAY_COLOR);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;


        reset.setToolTipText("Reset Zulrah rotation");
        reset.setRolloverIcon(RESET_HOVER_ICON);
        reset.addActionListener(l -> plugin.reset());

        centerPanel.add(phasesView, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void update(State state) {
        SwingUtilities.invokeLater(() -> updatePanel(state));
    }

    private void updatePanel(State state) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        phasesView.removeAll();

        JPanel ip = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.VERTICAL;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        for (List<Phase> phases : state.buildTree()) {
            if (ip.getComponentCount() >= 2 || phases.size() >= 2) {
                phasesView.add(ip, constraints);
                constraints.gridy++;
                ip = new JPanel(new GridBagLayout());
                c = new GridBagConstraints();
                c.fill = GridBagConstraints.VERTICAL;
                c.weighty = 1;
                c.gridx = 0;
                c.gridy = 0;
            }

            if (phases.size() >= 2) {
                phasesView.add(Box.createRigidArea(new Dimension(0, 12)), constraints);
                constraints.gridy++;
                phasesView.add(new JLabel(String.format("Select Phase...")), constraints);
                constraints.gridy++;
            }

            for (Phase p : phases) {
                ip.add(new ZulrahHelperPhasePanel(plugin, p, phases.size()), c);
                c.gridx++;

                if (p.isCurrent()) {
                    phasesView.add(Box.createRigidArea(new Dimension(0, 12)), constraints);
                    constraints.gridy++;
                    phasesView.add(new JLabel(String.format("Current Phase: %s #%d", p.getRotation(), p.getNumber())), constraints);
                    constraints.gridy++;
                }
            }

            phasesView.add(ip, constraints);
            constraints.gridy++;
        }

        repaint();
        revalidate();
    }
}
