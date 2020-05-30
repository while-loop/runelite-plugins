package com.ttl.ui;

import com.ttl.TimeToLevelConfig;
import com.ttl.TimeToLevelPlugin;
import com.ttl.RateTTL;
import net.runelite.api.Skill;
import net.runelite.client.ui.PluginPanel;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;


public class LevelsPanel extends PluginPanel {
    private final LevelsListPanel levelsListPanel;
    private final RatesConfigPanel ratesConfigPanel;

    private final TimeToLevelPlugin plugin;

    public LevelsPanel(TimeToLevelPlugin plugin, TimeToLevelConfig config) {
        super(false);
        this.plugin = plugin;
        this.levelsListPanel = new LevelsListPanel(plugin, config, (skill) -> showSkillRates(skill, true), this::showRatesConfig);
        this.ratesConfigPanel = new RatesConfigPanel(plugin, () -> showList(true), (skill) -> showSkillRates(skill, false));

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 0, 0, 0));
        showList(true);
    }

    public void update(List<RateTTL> ttls) {
        levelsListPanel.update(ttls);
    }

    public void showList(boolean levelsList) {
        removeAll();
        if (levelsList) {
            add(levelsListPanel, BorderLayout.CENTER);
        } else {
            add(ratesConfigPanel, BorderLayout.CENTER);
        }
        repaint();
        revalidate();
    }

    public void showSkillRates(Skill skill, boolean levelsList) {
        removeAll();
        add(new SkillRatePanel(plugin, plugin.getRates(skill), () -> showList(levelsList)));
        repaint();
        revalidate();
    }

    public void showRatesConfig() {
        removeAll();
        add(ratesConfigPanel);
        repaint();
        revalidate();
    }
}
