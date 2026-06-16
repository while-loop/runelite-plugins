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

import com.zulrahhelper.ZulrahHelperConfig;
import com.zulrahhelper.options.Strategy;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Path implements Point
{
	private final Strategy mode;
	private final List<StandLocation> positions = new ArrayList<>();

	Path(Strategy mode)
	{
		this.mode = mode;
	}

	public Path at(StandLocation pos)
	{
		positions.add(pos);
		return this;
	}

	public Path to(StandLocation pos)
	{
		positions.add(pos);
		return this;
	}

	@Override
	public Strategy getMode()
	{
		return mode;
	}

	@Override
	public void draw(Graphics2D g, int px, int py, ZulrahHelperConfig config)
	{
		if (!config.strategy().isVisible(mode))
		{
			return;
		}

		for (StandLocation pos : positions)
		{
			Point.drawShape(g, pos, px, py, mode, config);
		}

		if (config.displayMovementPaths() && positions.size() > 1)
		{
			for (int i = 0; i < positions.size() - 1; i++)
			{
				Arrows.drawArrow(g,
					px + positions.get(i).getX(), py + positions.get(i).getY(),
					px + positions.get(i + 1).getX(), py + positions.get(i + 1).getY());
			}
		}
	}
}
