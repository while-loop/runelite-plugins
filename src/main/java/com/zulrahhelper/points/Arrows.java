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

package com.zulrahhelper.points;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.QuadCurve2D;

public final class Arrows
{
	private static final int ARROWHEAD_SIZE = 5;
	private static final double ARROWHEAD_SPREAD = Math.toRadians(60);
	private static final int TIP_GAP = 6;
	private static final int ARC_CONTROL_SHIFT = 6;

	/**
	 * When both |dx| and |dy| exceed this value the movement is considered
	 * diagonal and rendered as a curved bezier.
	 */
	private static final int DIAGONAL_THRESHOLD = 3;

	private Arrows()
	{
	}

	public static void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2)
	{
		Color prevColor = g.getColor();
		Stroke prevStroke = g.getStroke();
		RenderingHints prevHints = g.getRenderingHints();
		try
		{
			double dx = x2 - x1;
			double dy = y2 - y1;
			boolean diagonal = Math.abs(dx) > DIAGONAL_THRESHOLD && Math.abs(dy) > DIAGONAL_THRESHOLD;

			g.setColor(Color.WHITE);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if (!diagonal)
			{
				double angle = Math.atan2(y2 - y1, x2 - x1);
				int tipX = (int) Math.round(x2 - TIP_GAP * Math.cos(angle));
				int tipY = (int) Math.round(y2 - TIP_GAP * Math.sin(angle));
				g.drawLine(x1, y1, tipX, tipY);
				drawArrowhead(g, tipX, tipY, angle);
			}
			else
			{
				// Bow the curve perpendicular to the travel direction (CW = right of travel)
				double len = Math.sqrt(dx * dx + dy * dy);
				double perpX = dy / len;
				double perpY = -dx / len;

				int cx = (int) Math.round((x1 + x2) / 2.0 + perpX * ARC_CONTROL_SHIFT);
				int cy = (int) Math.round((y1 + y2) / 2.0 + perpY * ARC_CONTROL_SHIFT);

				// Tangent at the end of the bezier: direction from control point to endpoint
				double tangentAngle = Math.atan2(y2 - cy, x2 - cx);
				int tipX = (int) Math.round(x2 - TIP_GAP * Math.cos(tangentAngle));
				int tipY = (int) Math.round(y2 - TIP_GAP * Math.sin(tangentAngle));

				g.setStroke(new BasicStroke(1));
				g.draw(new QuadCurve2D.Float(x1, y1, cx, cy, tipX, tipY));
				drawArrowhead(g, tipX, tipY, tangentAngle);
			}
		}
		finally
		{
			g.setColor(prevColor);
			g.setStroke(prevStroke);
			g.setRenderingHints(prevHints);
		}
	}

	private static void drawArrowhead(Graphics2D g, int tipX, int tipY, double angle)
	{
		int bx1 = (int) Math.round(tipX - ARROWHEAD_SIZE * Math.cos(angle - ARROWHEAD_SPREAD / 2));
		int by1 = (int) Math.round(tipY - ARROWHEAD_SIZE * Math.sin(angle - ARROWHEAD_SPREAD / 2));
		int bx2 = (int) Math.round(tipX - ARROWHEAD_SIZE * Math.cos(angle + ARROWHEAD_SPREAD / 2));
		int by2 = (int) Math.round(tipY - ARROWHEAD_SIZE * Math.sin(angle + ARROWHEAD_SPREAD / 2));
		g.fillPolygon(new int[]{tipX, bx1, bx2}, new int[]{tipY, by1, by2}, 3);
	}
}
