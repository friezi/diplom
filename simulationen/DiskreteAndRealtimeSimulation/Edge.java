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

package rsspubsubframework;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Edge to peer two nodes.
 */
public class Edge {
	/**
	 * First node peered by this edge.
	 * 
	 * This is the first node that is peered by this edge.
	 */
	private final Node a;

	/**
	 * Second node peered by this edge.
	 * 
	 * This is the second node that is peered by this edge.
	 */
	private final Node b;

	/**
	 * An edge-counter
	 * 
	 */
	private static int count = 0;

	/**
	 * The ID of the edge
	 * 
	 */
	private int id = 0;

	/**
	 * Size of id in display
	 */
	private static int IDSIZE = 21;

	/**
	 * if idOn, edge-id will be shown in display
	 */
	private static boolean idOn = false;

	/**
	 * Peer two nodes.
	 * 
	 * Use this constructor with two peer nodes to peer them.
	 * 
	 * @param ap
	 *            First node of the peering.
	 * @param bp
	 *            Second node of the peering.
	 */
	public Edge(Node ap, Node bp) {
		count++;
		id = count;
		a = ap;
		b = bp;
		addConnection();
		Engine.getSingleton().addEdge(this);
	}

	/**
	 * adds the connection between peers.
	 */
	void addConnection() {
		a.addpeer(b);
		b.addpeer(a);
	}

	/**
	 * removes the connection between peers.
	 */
	void removeConnection() {
		a.removepeer(b);
		b.removepeer(a);
	}

	/**
	 * Returns first node of this edge.
	 * 
	 * Call this method if you need the first node of this edge.
	 * 
	 * @return First node of this edge.
	 */
	protected Node node1() {
		return a;
	}

	/**
	 * Returns second node of this edge.
	 * 
	 * Call this method if you need the second node of this edge.
	 * 
	 * @return Second node of this edge.
	 */
	protected Node node2() {
		return b;
	}

	/**
	 * Return the color on display for the edge.
	 * 
	 * Overwrite this method to customize color of your edge.
	 * 
	 * @return Color of the edge.
	 */
	protected java.awt.Color color() {
		return java.awt.Color.black;
	}

	/**
	 * Return the width for the edge on display.
	 * 
	 * Overwrite this method to customize the width of your edge.
	 * 
	 * @return Width of the edge.
	 */
	protected int width() {
		return 1;
	}

	/**
	 * Is called to display the edge.
	 * 
	 * Overwrite this method to customize edge display.
	 * 
	 * @param g
	 *            The Graphics object the node should be drawn on.
	 * @param x1
	 *            Horizontal position of the first node linked by this edge.
	 * @param y1
	 *            Vertical position of the first node linked by this edge.
	 * @param x2
	 *            Horizontal position of the second node linked by this edge.
	 * @param y2
	 *            Vertical position of the second node linked by this edge.
	 */
	protected void draw(java.awt.Graphics g, int x1, int y1, int x2, int y2) {
		g.setColor(color());
		int t = width();
		for ( int i = 0; i < t; ++i ) {
			g.drawLine(x1 - t / 2 + i, y1, x2 - t / 2 + i, y2);
			g.drawLine(x1, y1 - t / 2 + i, x2, y2 - t / 2 + i);
		}
	}

	protected void showId(Graphics g, int x, int y) {

		g.setColor(Color.yellow);
		int s = IDSIZE;
		g.fillOval(x - s / 2, y - s / 2, s, s);
		g.setColor(java.awt.Color.black);
		g.drawOval(x - s / 2, y - s / 2, s, s);
		String t = String.valueOf(id);
		java.awt.FontMetrics fm = g.getFontMetrics();
		g.setColor(Color.black);
		g.drawString(t, x - fm.stringWidth(t) / 2, y + fm.getHeight() / 2);

	}

	/**
	 * Is called by the engine to display the edge.
	 * 
	 * This method is not externally visible. You can neither call nor overwrite
	 * it.
	 * 
	 * @param g
	 *            The Graphics object the node should be drawn on.
	 */
	final void drawobj(java.awt.Graphics g) {
		draw(g, a.xPos(), a.yPos(), b.xPos(), b.yPos());
		if ( isIdOn() )
			showId(g, a.xPos() + (b.xPos() - a.xPos()) / 2, a.yPos() + (b.yPos() - a.yPos()) / 2);
	}

	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return Returns the idOn.
	 */
	public static boolean isIdOn() {
		return idOn;
	}

	/**
	 * @param idOn
	 *            The idOn to set.
	 */
	public static void setIdOn(boolean idOn) {
		Edge.idOn = idOn;
	}
}
