package com.ttl.ui;

import com.ttl.TimeToLevelConfig;
import com.ttl.TimeToLevelPlugin;
import com.ttl.RateTTL;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class LevelsListPanel extends JPanel {
    static final int SKILL_COLUMN_WIDTH = 39;
    static final int SKILL_COLUMN_HEIGHT = 25;
    static final int LEVEL_COLUMN_WIDTH = 35;
    static final int XP_COLUMN_WIDTH = 57;
    static final int XP_RATE_COLUMN_WIDTH = 52;
    static final int TTL_COLUMN_WIDTH = 59;

    private final JPanel ratesListPanel = new JPanel();
    private final LevelsListHeader ratesHeaders;
    private List<RateTTL> ttls = new ArrayList<>();
    private final Consumer<Skill> showSkillListener;
    private final Filters filters;

    public LevelsListPanel(TimeToLevelPlugin plugin, TimeToLevelConfig config, Consumer<Skill> showSkillListener, Runnable showRatesConfig) {
        this.showSkillListener = showSkillListener;
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel(new BorderLayout());
        ratesHeaders = new LevelsListHeader(this, this::onSortChanged);
        filters = new Filters(config, this::updatePanel);

        northPanel.add(new LevelsListPanelHeader(plugin::recalculate, showRatesConfig), BorderLayout.NORTH);
        northPanel.add(filters, BorderLayout.CENTER);
        northPanel.add(ratesHeaders, BorderLayout.SOUTH);

        JPanel wrapped = new JPanel(new BorderLayout());
        wrapped.add(ratesListPanel, BorderLayout.NORTH);
        JScrollPane scroller = new JScrollPane(wrapped);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(northPanel, BorderLayout.NORTH);
        add(scroller, BorderLayout.CENTER);
    }

    private void onSortChanged() {
        update(ttls);
    }

    private List<RateTTL> sortTTLs(List<RateTTL> ts) {
        SortOrder sortOrder = ratesHeaders.getSort();
        Comparator<RateTTL> comp = sortOrder.comparator;
        if (!sortOrder.asc) {
            comp = comp.reversed();
        }
        ts.sort(comp);
        return ts;
    }

    public void update(List<RateTTL> ttls) {
        this.ttls = sortTTLs(ttls);
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

        List<Skill> vis = filters.getVisibleSkills();
        List<RateTTL> shown = ttls
                .stream()
                .filter(rc -> vis.contains(rc.getSkill()))
                .collect(Collectors.toList());

        for (int i = 0; i < shown.size(); i++) {
            RateTTL calc = shown.get(i);
            LevelsListCalcBox llcb = new LevelsListCalcBox(calc, () -> showSkillListener.accept(calc.getSkill()), i % 2 == 1);
            ratesListPanel.add(llcb, constraints);
            constraints.gridy++;
        }

        repaint();
        revalidate();
    }

    private static class Filters extends JPanel {
        private final TimeToLevelConfig config;
        private final JCheckBox cmb;
        private final JCheckBox art;
        private final JCheckBox gath;
        private final JCheckBox supp;
        private final JToggleButton buy;

        private Filters(TimeToLevelConfig config, Runnable refresh) {
            this.config = config;
            setBorder(new EmptyBorder(0, 0, 8, 0));
            setLayout(new GridLayout(3, 2, 2, 4));
            final int height = 18;
            cmb = new JCheckBox("Combat", true);
            SwingUtil.removeButtonDecorations(cmb);
            cmb.setToolTipText(TimeToLevelPlugin.COMBAT_SKILLS.stream().map(Skill::getName).collect(Collectors.joining(",")));
            cmb.addActionListener(l -> refresh.run());
            cmb.setHorizontalAlignment(JLabel.CENTER);
            cmb.setPreferredSize(new Dimension(cmb.getPreferredSize().width, height));

            art = new JCheckBox("Artisan", true);
            SwingUtil.removeButtonDecorations(art);
            art.setToolTipText(TimeToLevelPlugin.ARTISAN_SKILLS.stream().map(Skill::getName).collect(Collectors.joining(",")));
            art.addActionListener(l -> refresh.run());
            art.setHorizontalAlignment(JLabel.CENTER);
            art.setPreferredSize(new Dimension(cmb.getPreferredSize().width, height));

            gath = new JCheckBox("Gathering", true);
            SwingUtil.removeButtonDecorations(gath);
            gath.setToolTipText(TimeToLevelPlugin.GATHERING_SKILLS.stream().map(Skill::getName).collect(Collectors.joining(",")));
            gath.addActionListener(l -> refresh.run());
            gath.setHorizontalAlignment(JLabel.CENTER);
            gath.setPreferredSize(new Dimension(cmb.getPreferredSize().width, height));

            supp = new JCheckBox("Support", true);
            SwingUtil.removeButtonDecorations(supp);
            supp.setToolTipText(TimeToLevelPlugin.SUPPORT_SKILLS.stream().map(Skill::getName).collect(Collectors.joining(",")));
            supp.addActionListener(l -> refresh.run());
            supp.setHorizontalAlignment(JLabel.CENTER);
            supp.setPreferredSize(new Dimension(cmb.getPreferredSize().width, height));

            buy = new ToggleButton("Buyables", true);
            buy.setFocusPainted(false);
            buy.setToolTipText(getBuyables().stream().map(Skill::getName).collect(Collectors.joining(",")));
            buy.addActionListener(l -> refresh.run());
            buy.setHorizontalAlignment(JLabel.CENTER);
            buy.setPreferredSize(new Dimension(cmb.getPreferredSize().width, height));

            add(cmb);
            add(art);
            add(supp);
            add(gath);
            add(buy);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(PluginPanel.WIDTH, super.getPreferredSize().height);
        }

        private List<Skill> getVisibleSkills() {
            List<Skill> vis = new ArrayList<>();
            if (cmb.isSelected()) {
                vis.addAll(TimeToLevelPlugin.COMBAT_SKILLS);
            }
            if (art.isSelected()) {
                vis.addAll(TimeToLevelPlugin.ARTISAN_SKILLS);
            }
            if (gath.isSelected()) {
                vis.addAll(TimeToLevelPlugin.GATHERING_SKILLS);
            }
            if (supp.isSelected()) {
                vis.addAll(TimeToLevelPlugin.SUPPORT_SKILLS);
            }
            if (!buy.isSelected()) {
                vis.removeAll(getBuyables());
            }

            return vis;
        }

        private List<Skill> getBuyables() {
            List<Skill> buyables = new ArrayList<>();
            for (String s : config.buyableSkills().split(",")) {
                s = s.trim();
                if (s.isEmpty()) {
                    continue;
                }

                try {
                    buyables.add(Skill.valueOf(s.toUpperCase()));
                } catch (Exception ignored) {
                }
            }

            return buyables;
        }
    }
}
