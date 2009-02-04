package de.zintel.diplom.graphics;
/**
 * 
 */


import java.awt.*;

import de.zintel.diplom.simulation.Engine;

/**
 * @author Friedemann Zintel
 *
 */
public abstract class GraphicalObject extends DisplayableObject {

	/**
	 * 
	 */
	public GraphicalObject() {
		super();
	}

	/**
	 * You must call display() for displaying the GraphicalObject
	 */
	public void display() {
		Engine.getSingleton().addGraphicalObject(this);
	}
	/**
	 * Removes the GO from display
	 */
	public void undisplay() {
		Engine.getSingleton().removeGraphicalObject(this);
	}

	public abstract void drawobj(Graphics g);

}
