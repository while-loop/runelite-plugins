/*
 * Copyright (c) 2026, Ron Young <https://github.com/raiyni>
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

public enum PointShape
{
	X,
	CIRCLE,
	DIAMOND,
	SQUARE;

	public void draw(Graphics2D g, int cx, int cy, int size, Color color)
	{
		int half = size / 2;
		g.setColor(color);

		switch (this)
		{
			case X:
				g.drawLine(cx - half, cy - half, cx + half, cy + half);
				g.drawLine(cx - half, cy + half, cx + half, cy - half);
				// second offset pair for a thicker, more visible mark
				g.drawLine(cx - half - 1, cy - half, cx + half - 1, cy + half);
				g.drawLine(cx - half - 1, cy + half, cx + half - 1, cy - half);
				break;
			case CIRCLE:
				g.fillOval(cx - half, cy - half, size, size);
				break;
			case DIAMOND:
				g.fillPolygon(
					new int[]{cx, cx + half, cx, cx - half},
					new int[]{cy - half, cy, cy + half, cy},
					4);
				break;
			case SQUARE:
				g.fillRect(cx - half, cy - half, size, size);
				break;
		}
	}
}
