package com.runewatch;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(RuneWatchPlugin.CONFIG_GROUP)
public interface RuneWatchConfig extends Config {
    @ConfigItem(
            position = 1,
            keyName = "playerOption",
            name = "Player option",
            description = "Add investigate option to players"
    )
    default boolean playerOption() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "menuOption",
            name = "Menu option",
            description = "Show investigate option in menus"
    )
    default boolean menuOption() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "warningIcon",
            name = "Warning Icon",
            description = "Show warning icon next players' usernames"
    )
    default boolean warningIcon() {
        return true;
    }
}
