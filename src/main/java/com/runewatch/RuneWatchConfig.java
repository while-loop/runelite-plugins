package com.runewatch;

import net.runelite.client.config.*;
import net.runelite.client.util.ImageUploadStyle;

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

    @ConfigItem(
            position = 2,
            keyName = "screenshotToClipboard",
            name = "Upload",
            description = "Configures whether or not screenshots are placed on your clipboard",
            section = SCREENSHOT_SECTION
    )
    default boolean screenshotToClipboard() {
        return false;
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
            name = "Highlight color",
            description = "Allows you to change the color of the reported player's rsn in most player lists"
    )
    default Color playerTextColor() {
        return new Color(255, 77, 0);
    }

    @ConfigSection(
            name = "Notifications",
            description = "All the notification options",
            position = 70
    )
    String NOTIFICATIONS_SECTION = "Notifications";

    @ConfigItem(
            position = 7,
            keyName = "notifyOnJoin",
            name = "Alert On Join",
            description = "Send an alert message when a player on the watchlist enters a Clan/Friends Chat",
            section = NOTIFICATIONS_SECTION
    )
    default boolean notifyOnJoin() {
        return true;
    }

    @ConfigItem(
            position = 8,
            keyName = "notifyOnNearby",
            name = "Alert On Nearby",
            description = "Send an alert message when you're nearby a player on the watch list",
            section = NOTIFICATIONS_SECTION
    )
    default boolean notifyOnNearby() {
        return true;
    }

    @ConfigItem(
            position = 9,
            keyName = "notificationCooldown",
            name = "Cooldown",
            description = "How many minutes should pass before re-alerting when a player is on a watch list (0 always notifies)",
            section = NOTIFICATIONS_SECTION
    )
    @Units(Units.MINUTES)
    default int notificationCooldown()
    {
        return 60;
    }

    @ConfigSection(
            name = "Sources",
            description = "Configure sources for investigating reported players",
            position = 50
    )
    String SOURCES_SECTION = "Sources";

    @ConfigItem(
            position = 9,
            keyName = "useRW",
            name = "Use RuneWatch",
            description = "Check for reported players against the RuneWatch watchlist",
            section = SOURCES_SECTION
    )
    default boolean useRW() {
        return true;
    }

    @ConfigItem(
            position = 10,
            keyName = "useWDR",
            name = "Use We Do Raids",
            description = "Check for reported players against the We Do Raids banlist",
            section = SOURCES_SECTION
    )
    default boolean useWDR() {
        return true;
    }
}
