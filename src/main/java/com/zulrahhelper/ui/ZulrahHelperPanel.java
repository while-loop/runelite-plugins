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
import com.zulrahhelper.tree.PatternTree;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

@Slf4j
@Singleton
public class ZulrahHelperPanel extends PluginPanel
{
	private static final ImageIcon RESET_ICON;
	private static final ImageIcon RESET_HOVER_ICON;

	static
	{
		final BufferedImage addIcon = ImageUtil.loadImageResource(ZulrahHelperPlugin.class, "/ui/reset_icon.png");
		RESET_ICON = new ImageIcon(addIcon);
		RESET_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(addIcon, 0.53f));
	}

	private final ZulrahHelperPlugin plugin;
	private final PatternTree tree;

	private final JPanel phasesView = new JPanel(new GridBagLayout());

	@Inject
	ZulrahHelperPanel(ZulrahHelperPlugin plugin, PatternTree tree)
	{
		this.plugin = plugin;
		this.tree = tree;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setBorder(new EmptyBorder(1, 0, 10, 0));

		JLabel title = new JLabel();
		title.setText("Zulrah Helper");
		title.setForeground(Color.WHITE);

		northPanel.add(title, BorderLayout.WEST);
		JButton reset = new JButton(RESET_ICON);
		northPanel.add(reset, BorderLayout.EAST);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		phasesView.setBackground(ColorScheme.DARK_GRAY_COLOR);

		reset.setToolTipText("Reset Zulrah rotation");
		reset.setRolloverIcon(RESET_HOVER_ICON);
		reset.addActionListener(l -> plugin.reset());
		reset.setFocusable(false);

		centerPanel.add(phasesView, BorderLayout.CENTER);

		add(northPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);

		rebuildPanel();
	}

	public void rebuildPanel()
	{
		SwingUtil.fastRemoveAll(phasesView);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;

		JPanel rowPanel = new JPanel(new GridBagLayout());
		GridBagConstraints rowConstraints = resetRowConstraints();

		var path = tree.buildPath();
		int phaseNumber = 1;
		String title = null;
		for (var node : path)
		{
			if (!Strings.isNullOrEmpty(node.getValue().getTitle()))
			{
				title = node.getValue().getTitle();
			}

			if (rowPanel.getComponentCount() >= 2)
			{
				phasesView.add(rowPanel, constraints);
				constraints.gridy++;
				rowPanel = new JPanel(new GridBagLayout());
				rowConstraints = resetRowConstraints();
			}

			if (node.equals(tree.getState()))
			{
				createLabel(String.format("Current Phase: %s #%d", title, phaseNumber), constraints);
			}

			rowPanel.add(new ZulrahHelperPhasePanel(plugin, tree, node, 1), rowConstraints);
			rowConstraints.gridx++;

			if (node.size() >= 2)
			{
				phasesView.add(rowPanel, constraints);
				constraints.gridy++;
				rowPanel = new JPanel(new GridBagLayout());
				rowConstraints = resetRowConstraints();

				createLabel("Select Phase...", constraints);

				for (var nc : node.getChildren())
				{
					rowPanel.add(new ZulrahHelperPhasePanel(plugin, tree, nc, node.size()), rowConstraints);
					rowConstraints.gridx++;
				}
			}

			phasesView.add(rowPanel, constraints);
			constraints.gridy++;
			phaseNumber++;
		}

		repaint();
		revalidate();
	}

	private GridBagConstraints resetRowConstraints()
	{
		var c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;

		return c;
	}

	private void createLabel(String text, GridBagConstraints constraints)
	{
		phasesView.add(Box.createRigidArea(new Dimension(0, 12)), constraints);
		constraints.gridy++;
		phasesView.add(new JLabel(text), constraints);
		constraints.gridy++;
	}
}
