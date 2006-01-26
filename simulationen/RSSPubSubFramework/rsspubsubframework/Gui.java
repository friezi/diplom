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

/**
 * Gui object for the simulation.
 *
 * This object handles actual drawing of the user interface.
 */
class Gui extends javax.swing.JComponent {
    /**
     * 
     */
    private static final long serialVersionUID = -6099692865286022422L;
    /// Width of the drawing pane.
    private int currentWidth;
    /// Height of the drawing pane.
    private int currentHeight;
    /**
     * Create the gui window.
     */
    public Gui() {
	final Gui guip = this;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    javax.swing.JFrame frame =
			new javax.swing.JFrame("Messaging Network");
		    frame.setDefaultCloseOperation
			(javax.swing.JFrame.EXIT_ON_CLOSE);
		    frame.getContentPane().add(guip);
		    frame.setBounds(new java.awt.Rectangle(400,400));
		    frame.setVisible(true);
		}
	    });
    }
    /**
     * Return the width of the drawing pane.
     *
     * @return Width of the drawing pane.
     */
    int guiWidth() {
	return currentWidth;
    }
    /**
     * Return the height of the drawing pane.
     *
     * @return Height of the drawing pane.
     */
    int guiHeight() {
	return currentHeight;
    }
    /**
     * Draw the user interface.
     *
     * Calculates the drawing pane size and triggers the engine to draw all
     * objects.
     *
     * @param g The Graphics object to draw on.
     */
    protected void paintComponent(java.awt.Graphics g) {
        java.awt.Insets insets = getInsets();
	currentWidth = getWidth() - insets.left - insets.right;
	currentHeight = getHeight() - insets.top - insets.bottom;
	Engine.getSingleton().draw(g);
    }
}
