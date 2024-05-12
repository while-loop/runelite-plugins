/*
 * Copyright (c) 2024, Ron Young <https://github.com/raiyni>
 * All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.zulrahhelper.ui;

import com.zulrahhelper.ZulrahHelperConfig;
import com.zulrahhelper.ZulrahHelperPlugin;
import com.zulrahhelper.tree.Step;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

public class Images
{
	private static final BufferedImage FLOOR_IMG = ImageUtil.loadImageResource(Step.class, "/floor.png");
	private static final BufferedImage SNAKELINGS = ImageUtil.loadImageResource(Step.class, "/options/snakeling2.png");
	private static final BufferedImage HITSPLAT = ImageUtil.loadImageResource(Step.class, "/options/hitsplat.png");
	private static final BufferedImage VENOM = ImageUtil.loadImageResource(Step.class, "/options/venom.png");
	private static final BufferedImage RESET = ImageUtil.loadImageResource(ZulrahHelperPlugin.class, "/ui/reset_icon.png");;


	private static final int WIDTH = 105;
	private static final int HEIGHT = 105;

	private static final int PADDING = 2;

	private static final Color DARK_BACKGROUND = new Color(24, 24, 24, 65);
	private static final Color LIGHT_BACKGROUND = new Color(255, 255, 255, 200);

	public static BufferedImage createImage(final Step step, final ZulrahHelperConfig config)
	{
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) image.getGraphics();

		g.setColor(config.darkMode() ? DARK_BACKGROUND : LIGHT_BACKGROUND);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		int px = (WIDTH - FLOOR_IMG.getWidth()) / 2 + 1;
		int py = (HEIGHT - FLOOR_IMG.getHeight()) / 2;

		g.drawImage(FLOOR_IMG, null, px, py);

		for (var p : step.getPoints())
		{
			p.drawX(g, px, py);
		}

		var spawn = step.getSpawn();
		spawn.drawLocation(g, step.getForm().getColor(config), px, py);

		var theta = config.imageOrientation().getRotation();
		if (theta != 0)
		{
			image = ImageUtil.rotateImage(image, theta);
			g.dispose();
			g = (Graphics2D) image.getGraphics();
		}

		var prayers = step.getPrayers();
		if (config.displayPrayerIcons())
		{
			for (int idx = 0; idx < prayers.size(); idx++)
			{
				var p = prayers.get(idx);
				var img = p.getImage();

				int offset = idx == 1 ? WIDTH - img.getWidth() - PADDING : PADDING;
				g.drawImage(img, null, offset, PADDING);
			}
		}

		g.setFont(FontManager.getRunescapeBoldFont());
		if (config.displayAttackIcons() && step.getAttacks() > 0)
		{
			drawSplat(g, Color.WHITE, HITSPLAT, step.getAttacks() + "", 0);
		}

		if (config.displayVenom() && step.getVenom() > 0)
		{
			drawSplat(g, Color.WHITE, VENOM, step.getVenom() + "", WIDTH / 2 - VENOM.getWidth() / 2);
		}

		if (config.displaySnakelings() && step.getSnakelings() > 0)
		{
			var c = config.darkMode() ? Color.WHITE : Color.BLACK;
			var x = WIDTH - SNAKELINGS.getWidth() * 2 + PADDING * 2;
			drawSplat(g, c, SNAKELINGS, step.getSnakelings() + "", x, x + PADDING);
		}

		if (step.isReset())
		{
			var p = 23;
			g.drawImage(RESET, p, p, RESET.getWidth() * 3, RESET.getHeight() * 3, null);
		}

		g.dispose();
		return image;
	}

	private static void drawSplat(Graphics2D g, Color c, BufferedImage img, String text, int splatX, int textX)
	{
		var fm = g.getFontMetrics();
		g.drawImage(img, null, splatX + PADDING, HEIGHT - PADDING - HITSPLAT.getHeight());
		var tw = fm.stringWidth(text);
		var cx = HITSPLAT.getWidth() / 2 - tw / 2 + (tw % 2 == 1 ? -2 : 0);
		var cy = HITSPLAT.getHeight() / 2 - fm.getFont().getSize();

		var tx = textX + PADDING + cx;
		var ty = HEIGHT - PADDING + cy;

		g.setColor(Color.BLACK);
		g.drawString(text, tx + 1, ty + 1);

		g.setColor(c);
		g.drawString(text, tx, ty);
	}

	private static void drawSplat(Graphics2D g, Color c, BufferedImage img, String text, int x)
	{
		drawSplat(g, c, img, text, x, x);
	}
}
