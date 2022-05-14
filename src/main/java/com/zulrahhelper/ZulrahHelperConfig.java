package com.zulrahhelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup(ZulrahHelperPlugin.CONFIG_GROUP)
public interface ZulrahHelperConfig extends Config {
    @ConfigItem(
            keyName = ZulrahHelperPlugin.DISPLAY_PRAYER_KEY,
            name = "Prayer Icons",
            description = "Set phase images to use prayer icons, " +
                    "denoting what overhead prayer to use per phase. " +
                    "no prayer icon means the phase is safe to turn overheads off.",
            position = 0
    )
    default boolean displayPrayerIcons() { return true; }
    @ConfigItem(
            keyName = ZulrahHelperPlugin.DARK_MODE_KEY,
            name = "Dark Mode",
            description = "Set phase images to dark mode",
            position = 1
    )
    default boolean darkMode() {
        return true;
    }

    @ConfigItem(
            keyName = ZulrahHelperPlugin.AUTO_HIDE_KEY,
            name = "Hide when outside of Zul-Andra",
            description = "Don't show the button in the sidebar when you're not in Zul-Andra",
            position = 2
    )
    default boolean autoHide() {
        return true;
    }

    @ConfigItem(
            keyName = "resetPhasesHotkey",
            name = "Reset Phases",
            description = "Set phases back to start",
            position = 3
    )
    default Keybind resetPhasesHotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "nextPhaseHotkey",
            name = "Next Phase",
            description = "Increment the phase number by 1",
            position = 4
    )
    default Keybind nextPhaseHotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "phaseSelection1Hotkey",
            name = "Phase Selection 1",
            description = "Choose the first option in phase selection",
            position = 5
    )
    default Keybind phaseSelection1Hotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "phaseSelection2Hotkey",
            name = "Phase Selection 2",
            description = "Choose the second option in phase selection",
            position = 6
    )
    default Keybind phaseSelection2Hotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "phaseSelection3Hotkey",
            name = "Phase Selection 3",
            description = "Choose the third option in phase selection",
            position = 7
    )
    default Keybind phaseSelection3Hotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "imageOrientation",
            name = "Orientation",
            description = "Rotate the phase images to the specified cardinal direction.",
            position = 8
    )
    default ImageOrientation imageOrientation()
    {
        return ImageOrientation.SOUTH;
    }
}
