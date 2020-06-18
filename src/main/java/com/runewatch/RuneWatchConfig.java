package com.runewatch;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup(RuneWatchConfig.CONFIG_GROUP)
public interface RuneWatchConfig extends Config {
    String CONFIG_GROUP = "runewatch";
    String PLAYER_OPTION = "playerOption";
    String PLAYER_TEXT_COLOR = "playerTextColor";

    @ConfigItem(
            position = 1,
            keyName = PLAYER_OPTION,
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
            keyName = "useHotkey",
            name = "Require Shift-Click",
            description = "Require Shift-Right-Click to view investigate option in menus"
    )
    default boolean useHotkey() {
        return false;
    }

    @ConfigItem(
            position = 4,
            keyName = PLAYER_TEXT_COLOR,
            name = "Watchlist color",
            description = "Allows you to change the color of the reported player's rsn in most player lists"
    )
    default Color playerTextColor() {
        return new Color(255, 77, 0);
    }
}
