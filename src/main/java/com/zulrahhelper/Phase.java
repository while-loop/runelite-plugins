package com.zulrahhelper;


import com.zulrahhelper.options.Attack;
import com.zulrahhelper.options.OverheadProtection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.util.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Slf4j
public class Phase {
    public enum Rotation {
        START, NORMAL, MAGMA, MAGMA_A, MAGMA_B, TANZ
    }

    public static final String IMG_PATH = "/phases/%s-%s.png";

    private final Rotation rotation;
    private final int number;

    // ordered set of prayers.
    private final OverheadProtection[] prayers;

    // ordered set of attacks.
    // 0 - normal
    // 1 - venom
    // 2 - snakeling
    private final Attack[] attacks;
    private BufferedImage image;
    private boolean current = false;
    private boolean selectable = false;
    private boolean completed = false;
    private ImageOptions lastImageOpts;

    public static PhaseBuilder builder(Rotation rotation, int number) {
        return new PhaseBuilder().setRotation(rotation).setNumber(number);
    }

    public Phase(Rotation rotation, int number, OverheadProtection[] prayers, Attack[] attacks) {
        this.rotation = rotation;
        this.number = number;
        this.prayers = prayers;
        this.attacks = attacks;
        this.image = getImage(new ImageOptions());
    }

    public Phase copy() {
        return new Phase(rotation, number, prayers, attacks);
    }

    private String getImgPath(Rotation rotation, int number) {
        return String.format(IMG_PATH, rotation.name().toLowerCase(), number);
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
        this.completed = number < state.getNumber();
    }

    public void setCurrent(State state) {
        this.current = state.getNumber() == number && state.getRotation() == rotation;
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
        return getImage(new ImageOptions(config));
    }

    public BufferedImage getImage(ImageOptions imgOpts) {
        if (image != null && imgOpts.equals(lastImageOpts)) {
            return image;
        }

        String imgPath = getImgPath(rotation, number);
        lastImageOpts = imgOpts;
        image = ImageUtil.loadImageResource(getClass(), imgPath);
        image = applyImageOptions(image, lastImageOpts);
        return image;
    }

    private BufferedImage applyImageOptions(BufferedImage image, ImageOptions imageOpts) {
        final double theta = imageOpts.getOrientation().getRotation();
        if (theta != 0) {
            image = ImageUtil.rotateImage(image, theta);
        }

        if (imageOpts.isAttackIcons()) {
            for (Attack attack : attacks) {
                image = attack.applyToPhase(image, imageOpts.isDarkMode());
            }
        }

        if (imageOpts.isPrayerIcons()) {
            for (int i = 0; i < prayers.length; i++) {
                image = prayers[i].applyToPhase(image, i == 0);
            }
        }

        if (!imageOpts.isDarkMode()) {
            // convert RGBA to RGB
            if (image != null && image.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage out = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = out.createGraphics();
                g2d.drawImage(image, 0, 0, Color.WHITE, null);
                g2d.dispose();
                image = out;
            }
        }

        if (image != null) image.flush();
        return image;
    }
}
