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

package com.zulrahhelper.options;

import java.awt.Color;
import java.awt.Graphics2D;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ZulrahLocation
{
	NORTH(39, -10),
	SOUTH(40, 30),
	EAST(82, 25),
	WEST(-5, 35);

	private static final int PHASE_RADIUS = 5;
	private static final Color BORDER_COLOR = new Color(140, 140, 140);

	private final int x;
	private final int y;

	public int getX()
	{
		return x - PHASE_RADIUS;
	}

	public int getY()
	{
		return y - PHASE_RADIUS;
	}

	public int getWidth()
	{
		return PHASE_RADIUS * 2 + 1;
	}

	public int getHeight()
	{
		return PHASE_RADIUS * 2 + 1;
	}

	public void drawLocation(Graphics2D g, Color c, int px, int py)
	{
		// draw a little bit bigger for a sharper circle
		g.setColor(c);
		g.fillOval(getX() + px - 1, getY() + py - 1, getWidth() + 2, getHeight() + 2);

		g.setColor(BORDER_COLOR);
		g.drawOval(getX() + px - 1, getY() + py - 1, getWidth() + 2, getHeight() + 2);

	}
}
