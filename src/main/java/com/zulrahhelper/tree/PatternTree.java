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

package com.zulrahhelper.tree;

import com.google.common.collect.Iterables;
import com.zulrahhelper.options.Prayer;
import com.zulrahhelper.points.StandLocation;
import com.zulrahhelper.options.ZulrahForm;
import com.zulrahhelper.options.ZulrahLocation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static com.zulrahhelper.options.Strategy.MELEE_ONLY;
import static com.zulrahhelper.points.Point.path;
import static com.zulrahhelper.points.Point.stand;

@Singleton
@Slf4j
public class PatternTree
{
	protected Node root;

	@Getter
	@NonNull
	protected Node state;

	@Inject
	public PatternTree()
	{
		this.root = build();
		this.state = root;
	}

	public Node find(Step value)
	{
		return root.find(value);
	}

	public void setState(@Nonnull Node n)
	{
		if (contains(n))
		{
			state = n;
		}
	}

	public boolean contains(Node node)
	{
		return root.find(node);
	}

	public void reset()
	{
		state = root;
	}

	public int depth(Node node)
	{
		return depth(node.getValue());
	}

	public int depth(Step value)
	{
		Node node = find(value);
		if (node == null)
		{
			return -1;
		}

		int i = 0;
		while (node.parent != null)
		{
			i++;
			node = node.parent;
		}

		return i;
	}

	public List<Node> buildPath()
	{
		return buildPath(state);
	}

	public List<Node> buildPath(Node n)
	{
		List<Node> path = new ArrayList<>();
		if (n.getParent() != null)
		{
			var p = n.getParent();
			while (p != null)
			{
				path.add(0, Node.of(p));
				p = p.getParent();
			}
		}

		path.add(Node.of(n));
		var nc = n.getChildren();
		while (!nc.isEmpty())
		{
			if (nc.size() > 1)
			{
				Iterables.getLast(path)
					.children.addAll(nc.stream()
						.map(Node::of)
						.collect(Collectors.toList()));
				break;
			}
			else
			{
				path.add(Node.of(nc.get(0)));
			}

			nc = nc.get(0).getChildren();
		}

		return path;
	}

	private static Node build()
	{
		return Node.builder()
			.value(Step.builder()
				.title("START")
				.point(stand(StandLocation.START))
				.form(ZulrahForm.RANGE)
				.attacks(5)
				.venom(4)
				.point(stand(StandLocation.START, MELEE_ONLY))
				.build())
			.child(buildMelee())
			.child(buildRange())
			.child(buildMage())
			.build();
	}

	private static Node buildMelee()
	{
		return Node.builder()
			.value(Step.builder()
				.title("MAGMA")
				.form(ZulrahForm.MELEE)
				.point(stand(StandLocation.START))
				.point(stand(StandLocation.START_MAGMA))
				.attacks(2)
				.point(path(MELEE_ONLY).at(StandLocation.START).to(StandLocation.MELEE_START_MAGMA))
				.build())
			.child(Node.builder()
				.value(Step.builder()
					.form(ZulrahForm.MAGE)
					.prayer(Prayer.MAGIC)
					.attacks(4)
					.point(stand(StandLocation.START))
					.point(path(MELEE_ONLY).at(StandLocation.START).to(StandLocation.PILLAR_1_SOUTH))
					.build())
				.child(buildMeleeA())
				.child(buildMeleeB())
				.build())
			.build();
	}

	private static Node buildMeleeA()
	{
		return Node.builder()
			.value(Step.builder()
				.title("MAGMA A")
				.form(ZulrahForm.RANGE)
				.spawn(ZulrahLocation.NORTH)
				.point(stand(StandLocation.PILLAR_2_SOUTH))
				.prayer(Prayer.RANGE)
				.attacks(5)
				.venom(2)
				.snakelings(2)
				.point(path(MELEE_ONLY).at(StandLocation.MELEE_PILLAR_1_NORTH).to(StandLocation.MELEE_PILLAR_2_NORTH).to(StandLocation.PILLAR_2_SOUTH))
				.build())
			.node()
			.value(Step.builder()
				.form(ZulrahForm.MELEE)
				.attacks(2)
				.point(stand(StandLocation.PILLAR_2_SOUTH))
				.point(stand(StandLocation.PILLAR_2_SOUTH, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.form(ZulrahForm.MAGE)
				.attacks(5)
				.prayer(Prayer.MAGIC)
				.spawn(ZulrahLocation.EAST)
				.point(stand(StandLocation.NORTH))
				.point(stand(StandLocation.MELEE_PILLAR_2_EAST, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.spawn(ZulrahLocation.NORTH)
				.form(ZulrahForm.RANGE)
				.venom(3)
				.snakelings(2)
				.point(stand(StandLocation.PILLAR_1_SOUTH))
				.point(stand(StandLocation.PILLAR_1_SOUTH, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.spawn(ZulrahLocation.NORTH)
				.form(ZulrahForm.MAGE)
				.attacks(5)
				.venom(2)
				.snakelings(2)
				.prayer(Prayer.MAGIC)
				.point(stand(StandLocation.PILLAR_1_SOUTH))
				.point(path(MELEE_ONLY).at(StandLocation.PILLAR_1_SOUTH).to(StandLocation.MELEE_PILLAR_1_NORTH).to(StandLocation.MELEE_PILLAR_2_NORTH).to(StandLocation.PILLAR_2_SOUTH))
				.build())
			.node()
			.value(Step.builder()
				.spawn(ZulrahLocation.EAST)
				.form(ZulrahForm.RANGE)
				.attacks(10)
				.venom(4)
				.prayer(Prayer.RANGE)
				.prayer(Prayer.MAGIC)
				.point(stand(StandLocation.PILLAR_2_SOUTH))
				.point(stand(StandLocation.MELEE_PILLAR_2_EAST, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.form(ZulrahForm.MELEE)
				.attacks(2)
				.point(stand(StandLocation.START))
				.point(stand(StandLocation.START_MAGMA))
				.point(path(MELEE_ONLY).at(StandLocation.START).to(StandLocation.MELEE_START_MAGMA))
				.build())
			.node()
			.value(Step.builder()
				.title("RESET")
				.form(ZulrahForm.RANGE)
				.prayer(Prayer.RANGE)
				.point(stand(StandLocation.START))
				.form(ZulrahForm.RANGE)
				.attacks(5)
				.venom(4)
				.reset(true)
				.point(stand(StandLocation.START, MELEE_ONLY))
				.build())
			.buildUp();
	}

	private static Node buildMeleeB()
	{
		return Node.builder()
			.value(Step.builder()
				.title("MAGMA B")
				.form(ZulrahForm.RANGE)
				.spawn(ZulrahLocation.EAST)
				.point(stand(StandLocation.PILLAR_2_SOUTH))
				.venom(3)
				.snakelings(2)
				.point(stand(StandLocation.MELEE_PILLAR_2_EAST, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.spawn(ZulrahLocation.NORTH)
				.form(ZulrahForm.MAGE)
				.attacks(5)
				.venom(2)
				.snakelings(2)
				.prayer(Prayer.MAGIC)
				.point(stand(StandLocation.PILLAR_2_SOUTH))
				.point(path(MELEE_ONLY).at(StandLocation.MELEE_PILLAR_2_NORTH).to(StandLocation.PILLAR_2_SOUTH))
				.build())
			.node()
			.value(Step.builder()
				.form(ZulrahForm.MELEE)
				.attacks(2)
				.point(stand(StandLocation.PILLAR_2_SOUTH))
				.point(stand(StandLocation.PILLAR_2_SOUTH, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.spawn(ZulrahLocation.WEST)
				.form(ZulrahForm.RANGE)
				.attacks(5)
				.prayer(Prayer.RANGE)
				.point(stand(StandLocation.NORTH))
				.point(stand(StandLocation.MELEE_PILLAR_1_WEST, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.spawn(ZulrahLocation.NORTH)
				.form(ZulrahForm.MAGE)
				.attacks(5)
				.venom(2)
				.snakelings(2)
				.prayer(Prayer.MAGIC)
				.point(stand(StandLocation.PILLAR_2_SOUTH))
				.point(path(MELEE_ONLY).at(StandLocation.MELEE_PILLAR_1_NORTH).to(StandLocation.MELEE_PILLAR_2_NORTH).to(StandLocation.PILLAR_2_SOUTH))
				.build())
			.node()
			.value(Step.builder()
				.spawn(ZulrahLocation.EAST)
				.form(ZulrahForm.RANGE)
				.attacks(10)
				.venom(4)
				.prayer(Prayer.RANGE)
				.prayer(Prayer.MAGIC)
				.point(stand(StandLocation.PILLAR_2_SOUTH))
				.point(stand(StandLocation.MELEE_PILLAR_2_EAST, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.form(ZulrahForm.MELEE)
				.attacks(2)
				.point(stand(StandLocation.START))
				.point(stand(StandLocation.START_MAGMA))
				.point(path(MELEE_ONLY).at(StandLocation.START).to(StandLocation.MELEE_START_MAGMA))
				.build())
			.node()
			.value(Step.builder()
				.title("RESET")
				.form(ZulrahForm.RANGE)
				.prayer(Prayer.RANGE)
				.point(stand(StandLocation.START))
				.form(ZulrahForm.RANGE)
				.attacks(5)
				.venom(4)
				.reset(true)
				.point(stand(StandLocation.START, MELEE_ONLY))
				.build())
			.buildUp();
	}

	private static Node buildRange()
	{
		return Node.builder()
			.value(Step.builder()
				.title("SERP")
				.form(ZulrahForm.RANGE)
				.spawn(ZulrahLocation.WEST)
				.point(stand(StandLocation.START))
				.prayer(Prayer.RANGE)
				.attacks(5)
				.snakelings(2)
				.point(stand(StandLocation.MELEE_START_MAGMA, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.form(ZulrahForm.MELEE)
				.attacks(2)
				.venom(3)
				.snakelings(2)
				.point(stand(StandLocation.PILLAR_1_SOUTH))
				.point(stand(StandLocation.PILLAR_1_SOUTH, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.spawn(ZulrahLocation.EAST)
				.form(ZulrahForm.MAGE)
				.attacks(5)
				.prayer(Prayer.MAGIC)
				.point(stand(StandLocation.NORTH))
				.point(stand(StandLocation.MELEE_PILLAR_2_EAST, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.spawn(ZulrahLocation.NORTH)
				.form(ZulrahForm.RANGE)
				.attacks(5)
				.prayer(Prayer.RANGE)
				.point(stand(StandLocation.NORTH))
				.point(path(MELEE_ONLY).at(StandLocation.MELEE_PILLAR_2_NORTH).to(StandLocation.MELEE_PILLAR_1_NORTH))
				.build())
			.node()
			.value(Step.builder()
				.spawn(ZulrahLocation.WEST)
				.form(ZulrahForm.MAGE)
				.attacks(5)
				.prayer(Prayer.MAGIC)
				.point(stand(StandLocation.NORTH))
				.point(stand(StandLocation.MELEE_PILLAR_1_WEST, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.form(ZulrahForm.RANGE)
				.venom(3)
				.snakelings(2)
				.point(stand(StandLocation.PILLAR_2_SOUTH))
				.point(stand(StandLocation.PILLAR_2_SOUTH, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.spawn(ZulrahLocation.EAST)
				.form(ZulrahForm.RANGE)
				.attacks(5)
				.prayer(Prayer.RANGE)
				.point(stand(StandLocation.PILLAR_2_SOUTH))
				.point(stand(StandLocation.MELEE_PILLAR_2_EAST, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.form(ZulrahForm.MAGE)
				.attacks(5)
				.venom(2)
				.snakelings(2)
				.prayer(Prayer.MAGIC)
				.point(stand(StandLocation.PILLAR_1_SOUTH))
				.point(stand(StandLocation.PILLAR_1_SOUTH, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.spawn(ZulrahLocation.WEST)
				.form(ZulrahForm.RANGE)
				.attacks(10)
				.prayer(Prayer.MAGIC)
				.prayer(Prayer.RANGE)
				.point(stand(StandLocation.PILLAR_1_SOUTH))
				.point(stand(StandLocation.MELEE_PILLAR_1_WEST, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.form(ZulrahForm.MAGE)
				.snakelings(2)
				.point(stand(StandLocation.START))
				.point(stand(StandLocation.START, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.title("RESET")
				.form(ZulrahForm.RANGE)
				.prayer(Prayer.RANGE)
				.point(stand(StandLocation.START))
				.form(ZulrahForm.RANGE)
				.attacks(5)
				.venom(4)
				.reset(true)
				.point(stand(StandLocation.START, MELEE_ONLY))
				.build())
			.buildUp();
	}

	private static Node buildMage()
	{
		return Node.builder()
			.value(Step.builder()
				.title("TANZ")
				.form(ZulrahForm.MAGE)
				.spawn(ZulrahLocation.WEST)
				.point(stand(StandLocation.START))
				.prayer(Prayer.MAGIC)
				.attacks(6)
				.snakelings(2)
				.point(stand(StandLocation.MELEE_START_MAGMA, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.spawn(ZulrahLocation.NORTH)
				.form(ZulrahForm.RANGE)
				.attacks(4)
				.venom(2)
				.prayer(Prayer.RANGE)
				.point(stand(StandLocation.PILLAR_2_SOUTH))
				.point(path(MELEE_ONLY).at(StandLocation.MELEE_PILLAR_1_NORTH).to(StandLocation.MELEE_PILLAR_2_NORTH).to(StandLocation.PILLAR_2_SOUTH))
				.build())
			.node()
			.value(Step.builder()
				.spawn(ZulrahLocation.EAST)
				.form(ZulrahForm.MAGE)
				.prayer(Prayer.MAGIC)
				.point(stand(StandLocation.PILLAR_2_SOUTH))
				.attacks(4)
				.snakelings(2)
				.point(stand(StandLocation.MELEE_PILLAR_2_EAST, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.form(ZulrahForm.MELEE)
				.point(stand(StandLocation.PILLAR_1_SOUTH))
				.attacks(2)
				.venom(2)
				.point(stand(StandLocation.PILLAR_1_SOUTH, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.form(ZulrahForm.RANGE)
				.spawn(ZulrahLocation.WEST)
				.point(stand(StandLocation.PILLAR_1_SOUTH))
				.prayer(Prayer.RANGE)
				.attacks(4)
				.point(stand(StandLocation.MELEE_PILLAR_1_WEST, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.form(ZulrahForm.RANGE)
				.spawn(ZulrahLocation.NORTH)
				.point(stand(StandLocation.PILLAR_1_SOUTH))
				.snakelings(2)
				.venom(3)
				.point(path(MELEE_ONLY).at(StandLocation.PILLAR_1_SOUTH).to(StandLocation.MELEE_PILLAR_1_NORTH).to(StandLocation.MELEE_PILLAR_2_NORTH).to(StandLocation.PILLAR_2_SOUTH))
				.build())
			.node()
			.value(Step.builder()
				.spawn(ZulrahLocation.EAST)
				.form(ZulrahForm.MAGE)
				.prayer(Prayer.MAGIC)
				.point(stand(StandLocation.PILLAR_2_EAST))
				.attacks(5)
				.venom(4)
				.point(stand(StandLocation.MELEE_PILLAR_2_EAST, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.form(ZulrahForm.RANGE)
				.prayer(Prayer.RANGE)
				.point(stand(StandLocation.PILLAR_1_SOUTH))
				.attacks(5)
				.point(stand(StandLocation.PILLAR_1_SOUTH, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.form(ZulrahForm.MAGE)
				.prayer(Prayer.MAGIC)
				.point(stand(StandLocation.PILLAR_1_SOUTH))
				.attacks(4)
				.venom(3)
				.point(stand(StandLocation.PILLAR_1_SOUTH, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.spawn(ZulrahLocation.WEST)
				.form(ZulrahForm.RANGE)
				.prayer(Prayer.MAGIC)
				.prayer(Prayer.RANGE)
				.point(stand(StandLocation.PILLAR_1_SOUTH))
				.attacks(8)
				.point(stand(StandLocation.MELEE_PILLAR_1_WEST, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.form(ZulrahForm.MAGE)
				.point(stand(StandLocation.START))
				.snakelings(2)
				.point(stand(StandLocation.START, MELEE_ONLY))
				.build())
			.node()
			.value(Step.builder()
				.title("RESET")
				.form(ZulrahForm.RANGE)
				.prayer(Prayer.RANGE)
				.point(stand(StandLocation.START))
				.form(ZulrahForm.RANGE)
				.attacks(5)
				.venom(4)
				.reset(true)
				.point(stand(StandLocation.START, MELEE_ONLY))
				.build())
			.buildUp();
	}
}
