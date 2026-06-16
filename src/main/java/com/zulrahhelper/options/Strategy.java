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

import com.zulrahhelper.ZulrahHelperConfig;
import java.awt.Color;

public enum Strategy
{
	MELEE_ONLY("Melee only"),
//	RANGE_ONLY("Zulrah Range mode only"),
	RANGE_MAGE("Range/Mage"),
//	TRIBRID_MELEE("Tribrid melee optimized"),
	;

	private final String displayName;

	Strategy(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}

	public boolean isVisible(Strategy pointMode)
	{
		switch (this)
		{
			case MELEE_ONLY:
				return pointMode == MELEE_ONLY;
//			case RANGE_ONLY:
			case RANGE_MAGE:
				return pointMode == RANGE_MAGE;
//			case TRIBRID_MELEE:
//				return true;
			default:
				return true;
		}
	}

	public Color getColor(ZulrahHelperConfig config)
	{
		switch (this)
		{
			case MELEE_ONLY:
				return config.meleePointColor();
			default:
				return config.rangePointColor();
		}
	}

	public PointShape getShape(ZulrahHelperConfig config)
	{
		switch (this)
		{
			case MELEE_ONLY:
				return config.meleePointShape();
			default:
				return config.rangePointShape();
		}
	}
}
