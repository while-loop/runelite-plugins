package com.ttl;

import com.google.inject.Provides;
import com.ttl.ui.LevelsPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.Skill;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.runelite.api.Skill.*;

@Slf4j
@PluginDescriptor(
        name = "Time to Level",
        description = "Panel to show how long until you get dopamine fireworks. /s",
        tags = {"xp", "track", "ttl", "skill", "level"}
)
public class TimeToLevelPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private TimeToLevelConfig config;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private RatesManager ratesManager;

    private LevelsPanel panel;

    private NavigationButton navButton;

    public static final List<Skill> COMBAT_SKILLS = Arrays.asList(ATTACK, STRENGTH, DEFENCE, MAGIC, RANGED, PRAYER, HITPOINTS);
    public static final List<Skill> GATHERING_SKILLS = Arrays.asList(MINING, FISHING, WOODCUTTING, HUNTER, FARMING);
    public static final List<Skill> ARTISAN_SKILLS = Arrays.asList(COOKING, SMITHING, FLETCHING, FIREMAKING, HERBLORE, CRAFTING, RUNECRAFT, CONSTRUCTION);
    public static final List<Skill> SUPPORT_SKILLS = Arrays.asList(AGILITY, THIEVING, SLAYER);

    @Override
    protected void startUp() {
        panel = new LevelsPanel(this, config);
        navButton = NavigationButton.builder()
                .tooltip("Time to Level")
                .icon(ImageUtil.getResourceStreamFromClass(getClass(), "/icon.png"))
                .priority(50)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown() {
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    public void onStatChanged(StatChanged event) {
        recalculate();
    }


    @Provides
    TimeToLevelConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TimeToLevelConfig.class);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(TimeToLevelConfig.CONFIG_GROUP)) {
            return;
        }

        recalculate();
    }

    public List<RateTTL> getTTLs() {
        List<RateTTL> ttls = new ArrayList<>();

        for (Rates e : ratesManager.getRates()) {
            Skill skill = Skill.valueOf(e.getSkill().toUpperCase());
            int xp = client.getSkillExperience(skill);
            int level = Experience.getLevelForXp(xp);
            if (level >= 99 && !config.virtualLevels()) {
                continue;
            }

            int xpLeft = Experience.getXpForLevel(level + 1) - client.getSkillExperience(skill);
            RateMethod rate = null;
            for (RateMethod rateMethod : e.getMethods()) {
                if (rateMethod.level > level) {
                    break;
                }
                rate = rateMethod;
            }
            if (rate == null || rate.rate <= 0) {
                log.error("no rate {}", skill);
                continue;
            }

            double secondsLeft = xpLeft / (rate.rate / 3600.0);
            ttls.add(new RateTTL(skill, level, xpLeft, rate.getRate(), (int) secondsLeft, rate));
        }

        return ttls;
    }

    public void recalculate() {
        panel.update(getTTLs());
    }

    public void saveRates(Rates r) {
        ratesManager.saveRates(r);
        recalculate();
    }

    public Rates getRates(Skill skill) {
        return ratesManager.getRates(skill);
    }

    public void resetRates() {
        ratesManager.resetRates();
        recalculate();
    }

    public String getRatesConfig() {
        return ratesManager.getRatesConfig();
    }

    public boolean setRatesConfig(String rates) {
        boolean success = ratesManager.setRatesConfig(rates);
        if (success) {
            recalculate();
        }

        return success;
    }

    public Rates resetRates(Skill skill) {
        Rates r = ratesManager.resetRates(skill);
        recalculate();
        return r;
    }
}
