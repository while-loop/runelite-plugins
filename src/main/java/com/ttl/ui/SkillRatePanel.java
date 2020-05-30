package com.ttl.ui;

import com.ttl.TimeToLevelPlugin;
import com.ttl.RateMethod;
import com.ttl.Rates;
import net.runelite.api.Skill;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;


public class SkillRatePanel extends JPanel {
    private static final ImageIcon ADD_ICON;
    private static final ImageIcon ADD_ICON_HOVER;

    static {
        BufferedImage addIcon = ImageUtil.getResourceStreamFromClass(TimeToLevelPlugin.class, "/add_icon.png");
        ADD_ICON = new ImageIcon(addIcon);
        ADD_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(addIcon, 0.53f));
    }

    private final TimeToLevelPlugin plugin;
    private final JPanel ratesListPanel = new JPanel(new BorderLayout());
    private Rates rates;
    private final JScrollPane scroller;
    private boolean scrollBottom = false;
    private final Runnable onBack;

    public SkillRatePanel(TimeToLevelPlugin plugin, Rates rates, Runnable onBack) {
        this.plugin = plugin;
        this.rates = rates;
        this.onBack = onBack;
        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(new SkillRatePanelHeader(rates.getSkill(), onBack, this::saveRates), BorderLayout.NORTH);

        JButton addButton = new JButton(ADD_ICON);
        addButton.setRolloverIcon(ADD_ICON_HOVER);
        SwingUtil.removeButtonDecorations(addButton);
        addButton.setToolTipText("Add rate");

        JButton resetBtn = new JButton("Reset rates");
        resetBtn.setFocusPainted(false);
        resetBtn.addActionListener(l -> resetRates());

        JPanel wrappedBtb = new JPanel(new BorderLayout());
        wrappedBtb.add(addButton, BorderLayout.EAST);
        wrappedBtb.add(resetBtn, BorderLayout.WEST);
        wrappedBtb.setBorder(new EmptyBorder(8, 8, 8, 0));

        ratesListPanel.setBorder(new EmptyBorder(0, 8, 0, 8));

        JPanel wrapped = new JPanel(new BorderLayout());
        wrapped.add(ratesListPanel, BorderLayout.NORTH);
        scroller = new JScrollPane(wrapped);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        addButton.addActionListener(e -> {
            addInfo();
            scrollBottom = true;
        });


        add(northPanel, BorderLayout.NORTH);
        add(scroller, BorderLayout.CENTER);
        add(wrappedBtb, BorderLayout.SOUTH);
        update();
    }

    private void resetRates() {
        Skill skill = rates.getRSSkill();
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                "Are you sure you reset custom XP rates?", "Reset Confirmation",
                JOptionPane.YES_NO_OPTION)) {
            rates = plugin.resetRates(skill);
            update();
        }
    }

    public void update() {
        SwingUtilities.invokeLater(this::updatePanel);
    }

    private void updatePanel() {
        ratesListPanel.removeAll();
        ratesListPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        rates.getMethods().sort(Comparator.comparingInt(RateMethod::getLevel));
        for (RateMethod info : rates.getMethods()) {
            SkillRateBox srb = new SkillRateBox(info, () -> removeInfo(info));
            ratesListPanel.add(srb, constraints);
            constraints.gridy++;

            ratesListPanel.add(Box.createRigidArea(new Dimension(0, 16)), constraints);
            constraints.gridy++;
        }

        repaint();
        revalidate();

        if (scrollBottom) {
            SwingUtilities.invokeLater(() -> {
                scroller.getVerticalScrollBar().setValue(scroller.getVerticalScrollBar().getMaximum());
                scrollBottom = false;
            });
        }
    }

    private void addInfo() {
        rates.getMethods().add(new RateMethod(99, 0, null));
        update();
    }

    private void removeInfo(RateMethod info) {
        rates.getMethods().remove(info);
        update();
    }

    private void saveRates() {
        plugin.saveRates(rates);
        onBack.run();
    }
}
