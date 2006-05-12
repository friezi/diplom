/*
 * msgnet - a simulation framework for message passing networks.
 * Copyright (C) 2005  Robert Schiele <rschiele@uni-mannheim.de>
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */



/**
 * Some useful topologies.
 */
abstract public class Topo {
	private Topo() {
	}

	/**
	 * The topology for the flooding example in the script.
	 * 
	 * Places all the nodes and edges into the topology system and returns a
	 * specific node to be initialized.
	 * 
	 * @param nf
	 *            The NodeFactory that should be used to fetch nodes from.
	 * @return Node to be initialized.
	 */
	public static Node graph1(NodeFactory nf) {
		return graph1(nf, new EdgeFactory());
	}

	/**
	 * The topology for the flooding example in the script.
	 * 
	 * Places all the nodes and edges into the topology system and returns a
	 * specific node to be initialized.
	 * 
	 * @param nf
	 *            The NodeFactory that should be used to fetch nodes from.
	 * @param ef
	 *            The EdgeFactory that should be used to fetch nodes from.
	 * @return Node to be initialized.
	 */
	public static Node graph1(NodeFactory nf, EdgeFactory ef) {
		Node a = nf.newNode(100, 100);
		Node b = nf.newNode(367, 100);
		Node c = nf.newNode(367, 900);
		Node d = nf.newNode(633, 100);
		Node e = nf.newNode(633, 900);
		Node f = nf.newNode(900, 100);
		Node g = nf.newNode(900, 900);
		ef.newEdge(a, b);
		ef.newEdge(b, c);
		ef.newEdge(b, d);
		ef.newEdge(c, d);
		ef.newEdge(c, e);
		ef.newEdge(d, f);
		ef.newEdge(e, f);
		ef.newEdge(e, g);
		ef.newEdge(f, g);
		return d;
	}

	/**
	 * The topology for the flooding example with acknowledgement packages and
	 * the echo example in the script.
	 * 
	 * Places all the nodes and edges into the topology system and returns a
	 * specific node to be initialized.
	 * 
	 * @param nf
	 *            The nodefactory that should be used to fetch nodes from.
	 * @return Node to be initialized.
	 */
	public static Node graph2(NodeFactory nf) {
		return graph2(nf, new EdgeFactory());
	}

	/**
	 * The topology for the flooding example with acknowledgement packages and
	 * the echo example in the script.
	 * 
	 * Places all the nodes and edges into the topology system and returns a
	 * specific node to be initialized.
	 * 
	 * @param nf
	 *            The nodefactory that should be used to fetch nodes from.
	 * @param ef
	 *            The EdgeFactory that should be used to fetch nodes from.
	 * @return Node to be initialized.
	 */
	public static Node graph2(NodeFactory nf, EdgeFactory ef) {
		Node a = nf.newNode(100, 100);
		Node b = nf.newNode(367, 100);
		Node c = nf.newNode(367, 900);
		Node d = nf.newNode(633, 100);
		Node e = nf.newNode(633, 900);
		Node f = nf.newNode(900, 100);
		Node g = nf.newNode(900, 900);
		ef.newEdge(a, b);
		ef.newEdge(b, c);
		ef.newEdge(b, d);
		ef.newEdge(c, d);
		ef.newEdge(c, e);
		ef.newEdge(d, f);
		ef.newEdge(e, f);
		ef.newEdge(f, g);
		return d;
	}

	/**
	 * The topology for the flooding example with acknowledgement packages and
	 * the echo example in the script.
	 * 
	 * Places all the nodes and edges into the topology system and returns a
	 * specific node to be initialized.
	 * 
	 * @param nf
	 *            The nodefactory that should be used to fetch nodes from.
	 * @return Node to be initialized.
	 */
	public static Node tree1(NodeFactory nf) {
		return tree1(nf, new EdgeFactory());
	}

	/**
	 * The topology for the flooding example with acknowledgement packages and
	 * the echo example in the script.
	 * 
	 * Places all the nodes and edges into the topology system and returns a
	 * specific node to be initialized.
	 * 
	 * @param nf
	 *            The nodefactory that should be used to fetch nodes from.
	 * @param ef
	 *            The EdgeFactory that should be used to fetch nodes from.
	 * @return Node to be initialized.
	 */
	public static Node tree1(NodeFactory nf, EdgeFactory ef) {
		Node n01 = nf.newNode(450, 100);
		Node n11 = nf.newNode(250, 300);
		Node n12 = nf.newNode(600, 300);
		Node n21 = nf.newNode(250, 500);
		Node n22 = nf.newNode(500, 500);
		Node n23 = nf.newNode(750, 500);
		Node n31 = nf.newNode(150, 700);
		Node n32 = nf.newNode(250, 700);
		Node n33 = nf.newNode(350, 700);
		Node n34 = nf.newNode(450, 700);
		Node n35 = nf.newNode(550, 700);
		Node n36 = nf.newNode(650, 700);
		Node n37 = nf.newNode(750, 700);
		Node n38 = nf.newNode(850, 700);
		Node n41 = nf.newNode(100, 900);
		Node n42 = nf.newNode(200, 900);
		Node n43 = nf.newNode(300, 900);
		Node n44 = nf.newNode(400, 900);
		ef.newEdge(n01, n11);
		ef.newEdge(n01, n12);
		ef.newEdge(n11, n21);
		ef.newEdge(n12, n22);
		ef.newEdge(n12, n23);
		ef.newEdge(n21, n31);
		ef.newEdge(n21, n32);
		ef.newEdge(n21, n33);
		ef.newEdge(n22, n34);
		ef.newEdge(n22, n35);
		ef.newEdge(n23, n36);
		ef.newEdge(n23, n37);
		ef.newEdge(n23, n38);
		ef.newEdge(n31, n41);
		ef.newEdge(n31, n42);
		ef.newEdge(n33, n43);
		ef.newEdge(n33, n44);
		return n01;
	}

	/**
	 * A ring topology with a certain number of nodes.
	 * 
	 * Places all the nodes and edges into the topology system and returns one
	 * node to be initialized.
	 * 
	 * @param nf
	 *            The nodefactory that should be used to fetch nodes from.
	 * @param num
	 *            Number of nodes.
	 * @return Node to be initialized.
	 */
	public static Node ring(NodeFactory nf, int num) {
		return ring(nf, new EdgeFactory(), num);
	}

	/**
	 * A ring topology with a certain number of nodes.
	 * 
	 * Places all the nodes and edges into the topology system and returns one
	 * node to be initialized.
	 * 
	 * @param nf
	 *            The nodefactory that should be used to fetch nodes from.
	 * @param ef
	 *            The EdgeFactory that should be used to fetch nodes from.
	 * @param num
	 *            Number of nodes.
	 * @return Node to be initialized.
	 */
	public static Node ring(NodeFactory nf, EdgeFactory ef, int num) {
		Node first = nf.newNode(500, 100);
		Node prev = first;
		for ( int i = 1; i < num; ++i ) {
			Node cur = nf.newNode(500 + (int) (400 * Math.sin(i * 2 * Math.PI / num)),
					500 - (int) (400 * Math.cos(i * 2 * Math.PI / num)));
			ef.newEdge(prev, cur);
			prev = cur;
		}
		ef.newEdge(prev, first);
		return first;
	}

	/**
	 * A complete graph topology with a certain number of nodes.
	 * 
	 * Places all the nodes and edges into the topology system and returns one
	 * node to be initialized.
	 * 
	 * @param nf
	 *            The nodefactory that should be used to fetch nodes from.
	 * @param num
	 *            Number of nodes.
	 * @return Node to be initialized.
	 */
	public static Node completeGraph(NodeFactory nf, int num) {
		return completeGraph(nf, new EdgeFactory(), num);
	}

	/**
	 * A complete graph topology with a certain number of nodes.
	 * 
	 * Places all the nodes and edges into the topology system and returns one
	 * node to be initialized.
	 * 
	 * @param nf
	 *            The nodefactory that should be used to fetch nodes from.
	 * @param ef
	 *            The EdgeFactory that should be used to fetch nodes from.
	 * @param num
	 *            Number of nodes.
	 * @return Node to be initialized.
	 */
	public static Node completeGraph(NodeFactory nf, EdgeFactory ef, int num) {
		final Node nl[] = new Node[num];
		for ( int i = 0; i < num; ++i ) {
			nl[i] = nf.newNode(500 + (int) (400 * Math.sin(i * 2 * Math.PI / num)), 500 - (int) (400 * Math
					.cos(i * 2 * Math.PI / num)));
			for ( int j = 0; j < i; ++j )
				ef.newEdge(nl[j], nl[i]);
		}
		return nl[0];
	}

	/**
	 * A hypercube topology with a certain dimension.
	 * 
	 * Places all the nodes and edges into the topology system and returns one
	 * node to be initialized. The current implementation does only support
	 * dimensions ranging from 0 to 5. Specifying a different dimension will
	 * have no result.
	 * 
	 * @param nf
	 *            The nodefactory that should be used to fetch nodes from.
	 * @param d
	 *            The dimension of the hypercube.
	 * @return Node to be initialized.
	 */
	public static Node hypercube(NodeFactory nf, int d) {
		return hypercube(nf, new EdgeFactory(), d);
	}

	/**
	 * A hypercube topology with a certain dimension.
	 * 
	 * Places all the nodes and edges into the topology system and returns one
	 * node to be initialized. The current implementation does only support
	 * dimensions ranging from 0 to 5. Specifying a different dimension will
	 * have no result.
	 * 
	 * @param nf
	 *            The nodefactory that should be used to fetch nodes from.
	 * @param ef
	 *            The EdgeFactory that should be used to fetch nodes from.
	 * @param d
	 *            The dimension of the hypercube.
	 * @return Node to be initialized.
	 */
	public static Node hypercube(NodeFactory nf, EdgeFactory ef, int d) {
		final int r[][] = {
				{ 0 },
				{ 0, 0 },
				{ 0, 0, 0, 0 },
				{ 1, 1, 1, 1, 0, 0, 0, 0 },
				{ 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0 },
				{ 3, 1, 3, 2, 2, 1, 1, 1, 3, 2, 2, 0, 3, 3, 1, 2, 2, 1, 3, 3, 0, 2, 2, 3, 1, 1, 1, 2, 2, 3,
						1, 3 } };
		final int p[][] = {
				{ 0 },
				{ 1, 0 },
				{ 3, 0, 2, 1 },
				{ 3, 0, 2, 1, 3, 0, 2, 1 },
				{ 4, 1, 5, 6, 7, 0, 6, 7, 3, 2, 4, 3, 2, 1, 5, 0 },
				{ 6, 6, 7, 8, 5, 3, 7, 0, 5, 4, 6, 0, 4, 3, 4, 2, 7, 9, 8, 9, 5, 1, 9, 0, 5, 2, 8, 0, 3, 2,
						1, 1 } };
		final int rv[][] = { { 0 }, { 400 }, { 566 }, { 236, 566 }, { 200, 400 }, { 50, 170, 310, 450 } };
		final double ov[][] = { { 0 }, { 0.5 }, { 0.5 }, { 0.5, 0.5 }, { 0.5, 0.5 }, { 0, 0.8, 0.4, 0 } };
		final int pp[] = { 1, 1, 2, 2, 4, 5 };
		final int sz = 1 << d;
		final Node nl[] = new Node[sz];
		for ( int i = 0; i < sz; ++i ) {
			final int abs = rv[d][r[d][i]];
			final double arc = (p[d][i] + ov[d][r[d][i]]) * Math.PI / pp[d];
			nl[i] = nf.newNode(500 + (int) (abs * Math.sin(arc)), 500 - (int) (abs * Math.cos(arc)));
			for ( int j = 0; j < d; ++j ) {
				int k = i & ~(1 << j);
				if ( i != k )
					ef.newEdge(nl[i], nl[k]);
			}
		}
		return nl[0];
	}

	public interface TreeStructure {
		public int numChildren(int level);
	}

	static private class TreeTmpNode {
		private final TreeTmpNode parent;

		private java.util.Set<TreeTmpNode> children = new java.util.HashSet<TreeTmpNode>();

		TreeTmpNode(TreeTmpNode p) {
			parent = p;
		}

		void addChild(TreeTmpNode c) {
			children.add(c);
		}

		int numChildren(int l) {
			if ( l == 0 )
				return 1;
			int sum = 0;
			for ( TreeTmpNode cn : children )
				sum += cn.numChildren(l - 1);
			return sum;
		}
	}

	/**
	 * A tree topology.
	 * 
	 * 
	 * @param nf
	 *            The nodefactory that should be used to fetch nodes from.
	 * @param s ?
	 * @return Node to be initialized.
	 */
	public static Node tree(NodeFactory nf, TreeStructure s) {
		return tree(nf, new EdgeFactory(), s);
	}

	/**
	 * A tree topology.
	 * 
	 * @param nf
	 *            The nodefactory that should be used to fetch nodes from.
	 * @param ef
	 *            The EdgeFactory that should be used to fetch nodes from.
	 * @param s ?
	 * @return Node to be initialized.
	 */
	public static Node tree(NodeFactory nf, EdgeFactory ef, TreeStructure s) {
		TreeTmpNode root = new TreeTmpNode(null);
		return null;
	}
}
