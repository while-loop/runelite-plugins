package com.zulrahhelper.options;

import net.runelite.client.util.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public enum OverheadProtection {
    NONE(),
    PROTECT_FROM_MAGIC("/options/protect-from-magic.png"),
    PROTECT_FROM_MISSILES("/options/protect-from-missiles.png");

    private static final int SIZE = 75;
    private static final int PADDING = 5;

    private final BufferedImage image;

    OverheadProtection(String imgPath) {
        this.image = ImageUtil.loadImageResource(getClass(), imgPath);
    }

    OverheadProtection() {
        this.image = null;
    }

    public BufferedImage applyToPhase(BufferedImage phaseImg, boolean left) {
        if (this == NONE) return phaseImg;

        Graphics g = phaseImg.getGraphics();
        g.drawImage(this.image, getX(phaseImg.getWidth(), left), getY(phaseImg.getHeight(), left), SIZE, SIZE, null);
        return phaseImg;
    }

    private static int getX(int phaseWidth, boolean left) {
        if (left) {
            return PADDING;
        }

        return phaseWidth - SIZE - PADDING;
    }

    private static int getY(int phaseHeight, boolean left) {
        return PADDING;
    }
}
