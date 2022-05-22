package com.zulrahhelper.options;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.FontManager;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.zulrahhelper.options.AttackSource.*;

@Slf4j
public enum Attack {

    NONE(NORMAL, 0),

    NORMAL_2(NORMAL, 2),
    NORMAL_4(NORMAL, 4),
    NORMAL_5(NORMAL, 5),
    NORMAL_6(NORMAL, 6),
    NORMAL_8(NORMAL, 8),
    NORMAL_10(NORMAL, 10),

    VENOM_2(VENOM, 2),
    VENOM_3(VENOM, 3),
    VENOM_4(VENOM, 4),

    SNAKELING_3(SNAKELING, 2),
    SNAKELING_4(SNAKELING, 2),
    SNAKELING_6(SNAKELING, 2);

    private static final int SIZE = 70;
    private static final int PADDING = 5;

    private final AttackSource source;
    private final BufferedImage darkImg;
    private final BufferedImage lightImg;

    Attack(AttackSource source, int hits) {
        this.source = source;
        this.darkImg = generateImage(hits, true);
        this.lightImg = generateImage(hits, false);
    }

    public AttackSource getSource() {
        return source;
    }

    public BufferedImage getImage(boolean darkMode) {
        return (darkMode) ? darkImg : lightImg;
    }

    private BufferedImage generateImage(int hits, boolean darkMode) {
        BufferedImage out = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);

        BufferedImage splat = source.getImage();
        BufferedImage hit = createHit(hits, darkMode);

        Graphics g = out.createGraphics();
        int splatX = 0;
        int splatY = 0;
        int splatSize = SIZE;
        if (source == SNAKELING) {
            splatX -= PADDING * 2;
            splatY += PADDING * 2;
            splatSize /= 1.5;
        }

        g.drawImage(splat, splatX, splatY, splatSize, splatSize, null);
        g.drawImage(hit, 0, 0, null);
        return out;
    }

    public BufferedImage applyToPhase(BufferedImage phaseImg, boolean darkMode) {
        if (this == NONE) return phaseImg;

        Graphics g = phaseImg.getGraphics();
        g.drawImage(this.getImage(darkMode), getX(phaseImg.getWidth(), source), getY(phaseImg.getHeight()), SIZE, SIZE, null);
        return phaseImg;
    }

    private BufferedImage createHit(int hits, boolean darkMode) {
        BufferedImage hit = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        final float fontSize = (float) (SIZE / 2);

        final Graphics g = hit.getGraphics();
        g.setFont(FontManager.getDefaultFont().deriveFont(fontSize));

        // Align the text in the center of the hitsplat
        final FontMetrics metrics = g.getFontMetrics();
        final String text = String.valueOf(hits);

        int x = (hit.getWidth() - metrics.stringWidth(text)) / 2;
        int y = (hit.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

        if (source == SNAKELING) {
            x += (PADDING / 2);
            y += PADDING;
        }

        // draw text shadow
        g.setColor(Color.BLACK);
        g.drawString(text, x + 1, y + 1);

        // snakeling light mode should have black text
        if (source == SNAKELING && !darkMode) {
            g.setColor(Color.BLACK);
        } else {
            g.setColor(Color.WHITE);
        }
        // draw text
        g.drawString(text, x, y);
        return hit;
    }


    private static int getX(int phaseImgWidth, AttackSource source) {
        int offset = phaseImgWidth / 3;

        switch (source) {
            case NORMAL:
                offset *= 0;
                break;
            case VENOM:
                offset *= 1;
                break;
            case SNAKELING:
                offset *= 2;
                break;
        }

        return (PADDING * 3) + offset;
    }

    private static int getY(int phaseImgHeight) {
        return phaseImgHeight - SIZE - PADDING;
    }
}
