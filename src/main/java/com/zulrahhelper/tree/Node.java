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

package com.zulrahhelper.tree;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;

@Getter
@Builder()
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Node
{
	protected Node parent;

	@ToString.Include
	@EqualsAndHashCode.Include
	protected Step value;

	@Singular
	protected List<Node> children;

	public int size()
	{
		return this.getChildren().size();
	}

	public Node find(Step val)
	{
		if (this.value.equals(val))
		{
			return this;
		}

		for (var c : children)
		{
			var n = c.find(val);
			if (n != null)
			{
				return n;
			}
		}

		return null;
	}

	public boolean find(Node node)
	{
		if (node == null)
		{
			return false;
		}

		if (this == node || this.value.equals(node.value))
		{
			return true;
		}

		for (var c : children)
		{
			if (c.find(node))
			{
				return true;
			}
		}

		return false;
	}

	public static Node of(@Nonnull Node n)
	{
		return new Node(null, n.value, new ArrayList<>());
	}

	public static class NodeBuilder
	{
		NodeBuilder parentBuilder;

		public NodeBuilder node()
		{
			var c = Node.builder();
			c.parentBuilder = this;
			return c;
		}

		public Node buildUp()
		{
			NodeBuilder b = this;
			NodeBuilder p;
			Node n = null;
			while (b != null)
			{
				n = b.build();
				p = b.parentBuilder;
				if (p != null)
				{
					p.child(n);
				}
				b = p;
			}

			return n;
		}

		public Node build()
		{
			if (this.children == null)
			{
				this.children = new ArrayList<>();
			}

			Node n = new Node(null, this.value, this.children);

			for (var c : n.children)
			{
				c.parent = n;
			}

			return n;
		}
	}
}
