package com.zulrahhelper;

import com.zulrahhelper.options.PointShape;
import com.zulrahhelper.options.Strategy;
import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

@ConfigGroup(ZulrahHelperPlugin.CONFIG_GROUP)
public interface ZulrahHelperConfig extends Config
{
	@ConfigSection(
		name = ZulrahHelperPlugin.SECTION_IMAGE_OPTIONS,
		description = "General phase image appearance",
		position = 3
	)
	String SECTION_IMAGE_OPTIONS = ZulrahHelperPlugin.SECTION_IMAGE_OPTIONS;

	@ConfigSection(
		name = ZulrahHelperPlugin.SECTION_PLAYER_MARKERS,
		description = "Player stand position markers and movement paths",
		position = 0
	)
	String SECTION_PLAYER_MARKERS = ZulrahHelperPlugin.SECTION_PLAYER_MARKERS;

	@ConfigSection(
		name = ZulrahHelperPlugin.SECTION_FORM_COLORS,
		description = "Colors of the Zulrah forms",
		position = 1
	)
	String SECTION_FORM_COLORS = ZulrahHelperPlugin.SECTION_FORM_COLORS;

	@ConfigSection(
		name = ZulrahHelperPlugin.SECTION_PHASE_OVERLAYS,
		description = "Icons and counts overlaid on phase images",
		position = 2
	)
	String SECTION_PHASE_OVERLAYS = ZulrahHelperPlugin.SECTION_PHASE_OVERLAYS;

	@ConfigSection(
		name = ZulrahHelperPlugin.SECTION_HOTKEYS,
		description = "All the options for binding hotkeys",
		position = 4
	)
	String SECTION_HOTKEYS = ZulrahHelperPlugin.SECTION_HOTKEYS;

	@ConfigSection(
		name = ZulrahHelperPlugin.SECTION_MISC,
		description = "Miscellaneous options for the plugin",
		position = 5
	)
	String SECTION_MISC = ZulrahHelperPlugin.SECTION_MISC;


	@ConfigItem(
		keyName = ZulrahHelperPlugin.DARK_MODE_KEY,
		section = ZulrahHelperPlugin.SECTION_IMAGE_OPTIONS,
		name = "Dark Mode",
		description = "Set phase images to dark mode",
		position = 0
	)
	default boolean darkMode()
	{
		return true;
	}

	@ConfigItem(
		keyName = "imageOrientation",
		section = ZulrahHelperPlugin.SECTION_IMAGE_OPTIONS,
		name = "Orientation",
		description = "Rotate the phase images to the specified cardinal direction",
		position = 1
	)
	default ImageOrientation imageOrientation()
	{
		return ImageOrientation.SOUTH;
	}

	@ConfigItem(
		keyName = ZulrahHelperPlugin.STRATEGY_KEY,
		section = ZulrahHelperPlugin.SECTION_PLAYER_MARKERS,
		name = "Strategy",
		description = "Select which positional strategy to display on phase images",
		position = 0
	)
	default Strategy strategy()
	{
		return Strategy.RANGE_MAGE;
	}

	@ConfigItem(
		keyName = ZulrahHelperPlugin.DISPLAY_MOVEMENT_PATHS_KEY,
		section = ZulrahHelperPlugin.SECTION_PLAYER_MARKERS,
		name = "Show Movement Paths",
		description = "Show directional arrows for movement between stand positions",
		position = 1
	)
	default boolean displayMovementPaths()
	{
		return true;
	}

	@ConfigItem(
		keyName = ZulrahHelperPlugin.RANGE_POINT_COLOR_KEY,
		section = ZulrahHelperPlugin.SECTION_PLAYER_MARKERS,
		name = "Range Point Color",
		description = "Color of ranged stand position markers",
		position = 2
	)
	default Color rangePointColor()
	{
		return Color.WHITE;
	}

	@ConfigItem(
		keyName = ZulrahHelperPlugin.MELEE_POINT_COLOR_KEY,
		section = ZulrahHelperPlugin.SECTION_PLAYER_MARKERS,
		name = "Melee Point Color",
		description = "Color of melee stand position markers",
		position = 3
	)
	default Color meleePointColor()
	{
		return new Color(251, 0, 7);
	}

	@ConfigItem(
		keyName = ZulrahHelperPlugin.RANGE_POINT_SHAPE_KEY,
		section = ZulrahHelperPlugin.SECTION_PLAYER_MARKERS,
		name = "Range Point Shape",
		description = "Shape of ranged stand position markers",
		position = 4
	)
	default PointShape rangePointShape()
	{
		return PointShape.X;
	}

	@ConfigItem(
		keyName = ZulrahHelperPlugin.MELEE_POINT_SHAPE_KEY,
		section = ZulrahHelperPlugin.SECTION_PLAYER_MARKERS,
		name = "Melee Point Shape",
		description = "Shape of melee stand position markers",
		position = 5
	)
	default PointShape meleePointShape()
	{
		return PointShape.X;
	}

	@ConfigItem(
		keyName = ZulrahHelperPlugin.POINT_SIZE_KEY,
		section = ZulrahHelperPlugin.SECTION_PLAYER_MARKERS,
		name = "Point Size",
		description = "Size of stand position markers",
		position = 6
	)
	@Range(min = 1, max = 20)
	default int pointSize()
	{
		return 6;
	}

	@ConfigItem(
		keyName = ZulrahHelperPlugin.MAGE_COLOR_KEY,
		section = ZulrahHelperPlugin.SECTION_FORM_COLORS,
		name = "Mage Form Color",
		description = "Color of Zulrah mage form",
		position = 0
	)
	default Color mageColor()
	{
		return new Color(0, 51, 255);
	}

	@ConfigItem(
		keyName = ZulrahHelperPlugin.RANGE_COLOR_KEY,
		section = ZulrahHelperPlugin.SECTION_FORM_COLORS,
		name = "Range Form Color",
		description = "Color of Zulrah range form",
		position = 1
	)
	default Color rangeColor()
	{
		return new Color(25, 194, 4);
	}

	@ConfigItem(
		keyName = ZulrahHelperPlugin.MELEE_COLOR_KEY,
		section = ZulrahHelperPlugin.SECTION_FORM_COLORS,
		name = "Melee Form Color",
		description = "Color of Zulrah melee form",
		position = 2
	)
	default Color meleeColor()
	{
		return new Color(251, 0, 7);
	}

	@ConfigItem(
		keyName = ZulrahHelperPlugin.DISPLAY_PRAYER_KEY,
		section = ZulrahHelperPlugin.SECTION_PHASE_OVERLAYS,
		name = "Prayer Icons",
		description = "Set phase images to use prayer icons, " +
			"denoting what overhead prayer to use per phase. " +
			"No prayer icon means the phase is safe to turn overheads off.",
		position = 0
	)
	default boolean displayPrayerIcons()
	{
		return false;
	}

	@ConfigItem(
		keyName = ZulrahHelperPlugin.DISPLAY_ATTACK_KEY,
		section = ZulrahHelperPlugin.SECTION_PHASE_OVERLAYS,
		name = "Attack Icons",
		description = "Display number of Zulrah attacks",
		position = 1
	)
	default boolean displayAttackIcons()
	{
		return false;
	}

	@ConfigItem(
		keyName = ZulrahHelperPlugin.DISPLAY_VENOM_KEY,
		section = ZulrahHelperPlugin.SECTION_PHASE_OVERLAYS,
		name = "Venom Icons",
		description = "Display number of venom attacks",
		position = 2
	)
	default boolean displayVenom()
	{
		return false;
	}

	@ConfigItem(
		keyName = ZulrahHelperPlugin.DISPLAY_SNAKELINGS_KEY,
		section = ZulrahHelperPlugin.SECTION_PHASE_OVERLAYS,
		name = "Snakeling Icons",
		description = "Display snakeling spawns",
		position = 3
	)
	default boolean displaySnakelings()
	{
		return false;
	}


	@ConfigItem(
		keyName = ZulrahHelperPlugin.AUTO_HIDE_KEY,
		section = ZulrahHelperPlugin.SECTION_MISC,
		name = "Hide when outside of Zul-Andra",
		description = "Don't show the button in the sidebar when you're not in Zul-Andra",
		position = 0
	)
	default boolean autoHide()
	{
		return true;
	}

	@ConfigItem(
		keyName = ZulrahHelperPlugin.RESET_ON_LEAVE_KEY,
		section = ZulrahHelperPlugin.SECTION_MISC,
		name = "Reset on Leave",
		description = "Automatically reset when leaving the Zulrah area",
		position = 1
	)
	default boolean resetOnLeave()
	{
		return true;
	}

	@ConfigItem(
		keyName = "resetPhasesHotkey",
		section = ZulrahHelperPlugin.SECTION_HOTKEYS,
		name = "Reset Phases",
		description = "Set phases back to start",
		position = 0
	)
	default Keybind resetPhasesHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		keyName = "nextPhaseHotkey",
		section = ZulrahHelperPlugin.SECTION_HOTKEYS,
		name = "Next Phase",
		description = "Increment the phase number by 1",
		position = 1
	)
	default Keybind nextPhaseHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		keyName = "phaseSelection1Hotkey",
		section = ZulrahHelperPlugin.SECTION_HOTKEYS,
		name = "Phase Selection 1",
		description = "Choose the first option in phase selection",
		position = 2
	)
	default Keybind phaseSelection1Hotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		keyName = "phaseSelection2Hotkey",
		section = ZulrahHelperPlugin.SECTION_HOTKEYS,
		name = "Phase Selection 2",
		description = "Choose the second option in phase selection",
		position = 3
	)
	default Keybind phaseSelection2Hotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		keyName = "phaseSelection3Hotkey",
		section = ZulrahHelperPlugin.SECTION_HOTKEYS,
		name = "Phase Selection 3",
		description = "Choose the third option in phase selection",
		position = 4
	)
	default Keybind phaseSelection3Hotkey()
	{
		return Keybind.NOT_SET;
	}
}
