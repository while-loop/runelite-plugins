package com.zulrahhelper.options;

import java.awt.image.BufferedImage;
import net.runelite.client.util.ImageUtil;

public enum AttackSource
{
	NORMAL("/options/hitsplat.png"),
	VENOM("/options/venom.png"),
	SNAKELING("/options/snakeling.png");

	private final BufferedImage image;

	AttackSource(String imgPath)
	{
		this.image = ImageUtil.loadImageResource(getClass(), imgPath);
	}

	public BufferedImage getImage()
	{
		return image;
	}
}
