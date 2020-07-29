package com.runewatch;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup(RuneWatchConfig.CONFIG_GROUP)
public interface RuneWatchConfig extends Config {
    String CONFIG_GROUP = "runewatch";
    String PLAYER_OPTION = "playerOption";
    String PLAYER_TEXT_COLOR = "playerTextColor";

    @ConfigSection(
            name = "Screenshot",
            description = "All the options for screenshot behavior",
            position = 80
    )
    String SCREENSHOT_SECTION = "Screenshot";

    @ConfigItem(
            position = 1,
            keyName = "screenshotTrades",
            name = "Screenshot Trades",
            description = "Enable screenshots on accepted trades",
            section = SCREENSHOT_SECTION
    )
    default boolean screenshotTrades() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "notifyWhenScreenshotTaken",
            name = "Notify When Taken",
            description = "Configures whether or not you are notified when a screenshot has been taken",
            section = SCREENSHOT_SECTION
    )
    default boolean notifyWhenScreenshotTaken() {
        return true;
    }

    @ConfigSection(
            name = "Menu",
            description = "All the options for menu behavior",
            position = 99
    )
    String MENU_SECTION = "Menu";

    @ConfigItem(
            position = 3,
            keyName = PLAYER_OPTION,
            name = "Player option",
            description = "Add investigate option to players",
            section = MENU_SECTION
    )
    default boolean playerOption() {
        return true;
    }

    @ConfigItem(
            position = 4,
            keyName = "menuOption",
            name = "Menu option",
            description = "Show investigate option in menus",
            section = MENU_SECTION
    )
    default boolean menuOption() {
        return true;
    }

    @ConfigItem(
            position = 5,
            keyName = "useHotkey",
            name = "Require Shift-Click",
            description = "Require Shift-Right-Click to view investigate option in menus",
            section = MENU_SECTION
    )
    default boolean useHotkey() {
        return false;
    }

    @ConfigItem(
            position = 6,
            keyName = PLAYER_TEXT_COLOR,
            name = "Watchlist color",
            description = "Allows you to change the color of the reported player's rsn in most player lists"
    )
    default Color playerTextColor() {
        return new Color(255, 77, 0);
    }
}
