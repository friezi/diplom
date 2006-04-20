/**
 * 
 */
package rsspubsubframework;

import java.awt.*;

/**
 * @author friezi
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

	abstract void drawobj(Graphics g);

}
