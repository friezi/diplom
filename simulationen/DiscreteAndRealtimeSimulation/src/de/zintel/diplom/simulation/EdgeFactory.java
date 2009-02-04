package de.zintel.diplom.simulation;
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
 * Base class for all edge factory implementations.
 * 
 * Implement a subtype of this class to specify how new edges are created.
 */
public class EdgeFactory {
	/**
	 * Returns a new edge.
	 * 
	 * Overwrite this method by generating a new edge and returning it.
	 * 
	 * @param a
	 *            First node to peer.
	 * @param b
	 *            Second node to peer.
	 * @return The new Edge.
	 */
	public Edge newEdge(Node a, Node b) {
		return new Edge(a, b);
	}

	public Edge newEdge(Node a, Node b, double bandwidth) {
		return new Edge(a, b, bandwidth);
	}

	/**
	 * Creates a new edge between nodes with node-ids.
	 * 
	 * @param id1
	 *            ID 1
	 * @param id2
	 *            ID 2
	 * @return The new Edge
	 */
	public Edge newEdge(int id1, int id2, double bandwidth) {
		return new Edge(Engine.getSingleton().getNode(id1), Engine.getSingleton().getNode(id2), bandwidth);
	}
}
