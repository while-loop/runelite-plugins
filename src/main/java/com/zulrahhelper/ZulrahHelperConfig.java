package com.zulrahhelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup(ZulrahHelperPlugin.CONFIG_GROUP)
public interface ZulrahHelperConfig extends Config {
    enum ImageOrientation
    {
        DEFAULT,
        RIGHT,
        UPSIDE_DOWN,
        LEFT
    }

    @ConfigItem(
            keyName = ZulrahHelperPlugin.DARK_MODE_KEY,
            name = "Dark Mode",
            description = "Set phases phase images to dark mode",
            position = 1
    )
    default boolean darkMode() {
        return true;
    }

    @ConfigItem(
            keyName = "resetPhasesHotkey",
            name = "Reset Phases",
            description = "Set phases back to start",
            position = 2
    )
    default Keybind resetPhasesHotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "nextPhaseHotkey",
            name = "Next Phase",
            description = "Increment the phase number by 1",
            position = 3
    )
    default Keybind nextPhaseHotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "phaseSelection1Hotkey",
            name = "Phase Selection 1",
            description = "Choose the first option in phase selection",
            position = 4
    )
    default Keybind phaseSelection1Hotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "phaseSelection2Hotkey",
            name = "Phase Selection 2",
            description = "Choose the second option in phase selection",
            position = 5
    )
    default Keybind phaseSelection2Hotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "phaseSelection3Hotkey",
            name = "Phase Selection 3",
            description = "Choose the third option in phase selection",
            position = 6
    )
    default Keybind phaseSelection3Hotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "imageOrientation",
            name = "Orientation",
            description = "Rotate the phase images to the specified orientation.",
            position = 7
    )
    default ImageOrientation imageOrientation()
    {
        return ImageOrientation.DEFAULT;
    }
}
