package com.zulrahhelper;


import net.runelite.client.util.ImageUtil;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;

public class Phase {
    public enum Rotation {
        START, NORMAL, MAGMA, MAGMA_A, MAGMA_B, TANZ
    }

    public static final String IMG_PATH = "/phases/%s-%s%s.png";

    private final Rotation rotation;
    private final int number;
    private BufferedImage image;
    private boolean current = false;
    private boolean selectable = false;
    private boolean completed = false;
    private String lastImgPath;

    public Phase(Rotation rotation, int number) {
        this.rotation = rotation;
        this.number = number;
        this.lastImgPath = getImgPath(rotation, number);
        this.image = ImageUtil.getResourceStreamFromClass(getClass(), lastImgPath);
    }

    private String getImgPath(Rotation rotation, int number) {
        return getImgPath(rotation, number, new ImageOptions());
    }

    private String getImgPath(Rotation rotation, int number, ImageOptions imgOpts) {
        String options = "";

        if (imgOpts.isDarkMode()) {
            options += "-dark";
        }
        if (imgOpts.isPrayerIcons()) {
            options += "-pray";
        }
        // attack versions of files automatically generated at
        // https://github.com/burkeg/OSRS-Hitsplat-Generator/tree/zulrah
        if (imgOpts.isAttackIcons()) {
            options += "-attack";
        }

        return String.format(IMG_PATH, rotation.name().toLowerCase(), number, options);
    }

    public Rotation getRotation() {
        return rotation;
    }

    public int getNumber() {
        return number;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public boolean isCurrent() {
        return current;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(State state) {
        this.completed = number < state.getSelectedPhase();
    }

    public void setCurrent(State state) {
        this.current = state.getSelectedPhase() == number && state.getRotation() == rotation;
    }

    public void setSelectable(State state) {
        this.selectable = true;
    }

    public void setStates(State state) {
        setCurrent(state);
        setSelectable(state);
        setCompleted(state);
    }

    public BufferedImage getImage(ZulrahHelperConfig config) {
        String currentImgPath = getImgPath(getRotation(), getNumber(), new ImageOptions(config));
        if (currentImgPath.equals(lastImgPath)) {
            return image;
        }

        lastImgPath = currentImgPath;
        image = ImageUtil.getResourceStreamFromClass(getClass(), lastImgPath);
        return image;
    }
}
