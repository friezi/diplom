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
 * Interface for all node factory implementations.
 *
 * You must implement a subtype of this class for real applications to specify
 * how new nodes are created.
 */
public interface NodeFactory {
    /**
     * Returns a new node.
     *
     * Implement this method by generating a new node and returning it.
     *
     * @param x The horizontal position of the new node.
     * @param y The vertical position of the new node.
     * @return The new node.
     */
    public Node newNode(int x, int y);
}
