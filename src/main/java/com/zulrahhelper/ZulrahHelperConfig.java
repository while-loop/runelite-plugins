package com.zulrahhelper;

import net.runelite.client.config.*;

@ConfigGroup(ZulrahHelperPlugin.CONFIG_GROUP)
public interface ZulrahHelperConfig extends Config {
    @ConfigSection(
            name = ZulrahHelperPlugin.SECTION_IMAGE_OPTIONS,
            description = "All the options for image options",
            position = 0
    )
    String SECTION_IMAGE_OPTIONS = ZulrahHelperPlugin.SECTION_IMAGE_OPTIONS;

    @ConfigSection(
            name = ZulrahHelperPlugin.SECTION_HOTKEYS,
            description = "All the options for binding hotkeys",
            position = 1
    )
    String SECTION_HOTKEYS = "Hotkeys";

    @ConfigSection(
            name = ZulrahHelperPlugin.SECTION_MISC,
            description = "Miscellaneous options for the plugin",
            position = 2
    )
    String SECTION_MISC = ZulrahHelperPlugin.SECTION_MISC;

    @ConfigItem(
            keyName = ZulrahHelperPlugin.DISPLAY_PRAYER_KEY,
            section = ZulrahHelperPlugin.SECTION_IMAGE_OPTIONS,
            name = "Prayer Icons",
            description = "Set phase images to use prayer icons, " +
                    "denoting what overhead prayer to use per phase. " +
                    "no prayer icon means the phase is safe to turn overheads off.",
            position = 0
    )
    default boolean displayPrayerIcons() {
        return false;
    }

    @ConfigItem(
            keyName = ZulrahHelperPlugin.DISPLAY_ATTACK_KEY,
            section = ZulrahHelperPlugin.SECTION_IMAGE_OPTIONS,
            name = "Attack Icons",
            description = "Set phase images to use attack icons, " +
                    "denoting what attacks Zulrah will use per phase.",
            position = 1
    )
    default boolean displayAttackIcons() {
        return false;
    }

    @ConfigItem(
            keyName = ZulrahHelperPlugin.DARK_MODE_KEY,
            section = ZulrahHelperPlugin.SECTION_IMAGE_OPTIONS,
            name = "Dark Mode",
            description = "Set phase images to dark mode",
            position = 2
    )
    default boolean darkMode() {
        return true;
    }

    @ConfigItem(
            keyName = ZulrahHelperPlugin.AUTO_HIDE_KEY,
            section = ZulrahHelperPlugin.SECTION_MISC,
            name = "Hide when outside of Zul-Andra",
            description = "Don't show the button in the sidebar when you're not in Zul-Andra",
            position = 3
    )
    default boolean autoHide() {
        return true;
    }

    @ConfigItem(
            keyName = "resetPhasesHotkey",
            section = ZulrahHelperPlugin.SECTION_HOTKEYS,
            name = "Reset Phases",
            description = "Set phases back to start",
            position = 4
    )
    default Keybind resetPhasesHotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "nextPhaseHotkey",
            section = ZulrahHelperPlugin.SECTION_HOTKEYS,
            name = "Next Phase",
            description = "Increment the phase number by 1",
            position = 5
    )
    default Keybind nextPhaseHotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "phaseSelection1Hotkey",
            section = ZulrahHelperPlugin.SECTION_HOTKEYS,
            name = "Phase Selection 1",
            description = "Choose the first option in phase selection",
            position = 6
    )
    default Keybind phaseSelection1Hotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "phaseSelection2Hotkey",
            section = ZulrahHelperPlugin.SECTION_HOTKEYS,
            name = "Phase Selection 2",
            description = "Choose the second option in phase selection",
            position = 7
    )
    default Keybind phaseSelection2Hotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "phaseSelection3Hotkey",
            section = ZulrahHelperPlugin.SECTION_HOTKEYS,
            name = "Phase Selection 3",
            description = "Choose the third option in phase selection",
            position = 8
    )
    default Keybind phaseSelection3Hotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "imageOrientation",
            section = ZulrahHelperPlugin.SECTION_IMAGE_OPTIONS,
            name = "Orientation",
            description = "Rotate the phase images to the specified cardinal direction.",
            position = 9
    )
    default ImageOrientation imageOrientation() {
        return ImageOrientation.SOUTH;
    }
}
