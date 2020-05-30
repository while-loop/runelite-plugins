package com.ttl;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import static com.ttl.TimeToLevelConfig.CONFIG_GROUP;

@ConfigGroup(CONFIG_GROUP)
public interface TimeToLevelConfig extends Config {
    String CONFIG_GROUP = "timetolevel";
    String RATES_KEY = "rates";
    String BUYABLE_KEY = "buyableSkills";
    String VIRTUAL_LEVEL_KEY = "virtualLevels";
    static final String DEFAULT_BUYABLES = "cooking,smithing,fletching,firemaking,herblore,crafting,construction,prayer,ranged,magic,farming";

    @ConfigItem(
            keyName = RATES_KEY,
            name = RATES_KEY,
            hidden = true,
            description = ""
    )
    default String rates() {
        return null;
    }


    @ConfigItem(
            keyName = VIRTUAL_LEVEL_KEY,
            name = "Support virtual levels",
            description = "Add TTL for skills greater than 99",
            position = 1
    )
    default boolean virtualLevels() {
        return false;
    }

    @ConfigItem(
            keyName = BUYABLE_KEY,
            name = "Buyable skills",
            description = "List of Buyable skills",
            position = 2
    )
    default String buyableSkills() {
        return DEFAULT_BUYABLES;
    }
}
