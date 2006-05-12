/*
 * RSSPubSubSim - a simulation framework for message passing networks.
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

/**
 * Base class for Node and Message.
 */
public abstract class DisplayableObject {
	
	protected java.awt.Color color = Color.white;
	
    /**
     * Return the color on display.
     * 
     * Overwrite this method to customize color.
     * 
     * @return Color.
     */
    protected java.awt.Color color() {
        return color;
    }

    /**
     * Return the text color on display.
     * 
     * Overwrite this method to customize text color.
     * 
     * @return Text color.
     */
    protected java.awt.Color textColor() {
        return java.awt.Color.black;
    }

    /**
     * Return the size on display.
     * 
     * Overwrite this method to customize size.
     * 
     * @return Size.
     */
    protected int size() {
        return 30;
    }

    /**
     * Return the text to display.
     * 
     * Overwrite this method to have text.
     * 
     * @return Text.
     */
    protected String text() {
        return "";
    }

    /**
     * Is called to display the object.
     * 
     * Overwrite this method to customize display.
     * 
     * @param g
     *            The Graphics object the object should be drawn on.
     * @param x
     *            Horizontal position.
     * @param y
     *            Vertical position.
     */
    protected void draw(java.awt.Graphics g, int x, int y) {

        g.setColor(color());
        int s = size();
        g.fillOval(x - s / 2, y - s / 2, s, s);
        g.setColor(java.awt.Color.black);
        g.drawOval(x - s / 2, y - s / 2, s, s);
        String t = text();
        java.awt.FontMetrics fm = g.getFontMetrics();
        g.setColor(textColor());
        g.drawString(t, x - fm.stringWidth(t) / 2, y + fm.getHeight() / 2);
        
    }

	public void setColor(java.awt.Color color) {
		this.color = color;
	}

	public void setDefaultColor() {
		this.color = Color.white;
	}
}
