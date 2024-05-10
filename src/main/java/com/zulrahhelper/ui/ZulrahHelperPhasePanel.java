/*
 * Copyright (c) 2020, Anthony Alves
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

import com.zulrahhelper.ZulrahHelperPlugin;
import com.zulrahhelper.tree.Node;
import com.zulrahhelper.tree.PatternTree;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

@Slf4j
public class ZulrahHelperPhasePanel extends JPanel implements MouseListener
{
	private final ZulrahHelperPlugin plugin;
	private final Node node;

	private final JLabel picLabel;
	private final ImageIcon phaseIcon;
	private final ImageIcon phaseIconHover;

	private final PatternTree tree;

	ZulrahHelperPhasePanel(ZulrahHelperPlugin plugin, PatternTree tree, Node node, int columns)
	{
		this.plugin = plugin;
		this.node = node;
		this.tree = tree;

		BufferedImage img = processImg(Images.createImage(node.getValue(), plugin.getConfig()), columns);
		phaseIcon = new ImageIcon(img);
		phaseIconHover = new ImageIcon(ImageUtil.luminanceScale(img, .75f));

		picLabel = new JLabel(phaseIcon);
		picLabel.addMouseListener(this);
//
		if (node.equals(tree.getState()))
		{
			setBorder(new LineBorder(ColorScheme.PROGRESS_COMPLETE_COLOR));
		}

		if (columns >= 2)
		{
			setBorder(new LineBorder(ColorScheme.PROGRESS_INPROGRESS_COLOR));
		}

		add(picLabel);
	}

	private BufferedImage processImg(BufferedImage img, int columns)
	{
		int size = 95;
		// make the images smaller if we're showing more than 3 images on the same row
		if (columns >= 3)
		{
			size = 60;
		}
		img = ImageUtil.resizeImage(img, size, size);
		if (tree.depth(node) < tree.depth(tree.getState()))
		{
			img = ImageUtil.luminanceScale(img, 0.35f);
		}

		return img;
	}

	@Override
	public void mouseClicked(MouseEvent mouseEvent)
	{
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent)
	{
		plugin.setState(node);
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent)
	{

	}

	@Override
	public void mouseEntered(MouseEvent mouseEvent)
	{
		picLabel.setIcon(phaseIconHover);
	}

	@Override
	public void mouseExited(MouseEvent mouseEvent)
	{
		picLabel.setIcon(phaseIcon);
	}
}
