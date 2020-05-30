package com.ttl;

import net.runelite.api.Skill;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

public class ImgUtils {
    private static final Map<Skill, BufferedImage> SMALL_ICONS = new EnumMap<>(Skill.class);
    private static final Map<Skill, BufferedImage> ICONS = new EnumMap<>(Skill.class);

    public static BufferedImage getSkillImage(String skill, boolean small) {
        return getSkillImage(Skill.valueOf(skill.toUpperCase()), small);
    }

    public static BufferedImage getSkillImage(Skill skill, boolean small) {
        String skillIconPath = (small ? "/skill_icons_small/" : "/skill_icons/")
                + skill.name().toLowerCase() + ".png";

        Map<Skill, BufferedImage> map = small ? SMALL_ICONS : ICONS;
        return map.computeIfAbsent(skill, s -> ImageUtil.getResourceStreamFromClass(TimeToLevelPlugin.class, skillIconPath));
    }
}
