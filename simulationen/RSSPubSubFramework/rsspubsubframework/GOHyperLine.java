/**
 * 
 */
package rsspubsubframework;

import java.awt.Graphics;
import java.awt.*;

/**
 * A Hyperline has its first point whithin the graphical environment and the
 * second point outside. So it can point outside the simulation-world.
 * 
 * @author friezi
 * 
 */
public class GOHyperLine extends GraphicalObject {

	int x1;

	int y1;

	int x2;

	int y2;

	/**
	 * 
	 */
	public GOHyperLine(int x1, int y1, int x2, int y2) {
		super();
		setValues(x1, y1, x2, y2);
	}

	protected void setValues(int x1, int y1, int x2, int y2) {

		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		setColor(Color.black);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.GraphicalObject#drawobj(java.awt.Graphics)
	 */
	@Override
	void drawobj(Graphics g) {

		int x1 = Engine.scaleX(getX1());
		int y1 = Engine.scaleY(getY1());
		int x2=getX2() - Engine.getSingleton().getDb().guiXPos();
		int y2=getY2() - Engine.getSingleton().getDb().guiYPos();
		g.setColor(color);
		g.drawLine(x1, y1, x2,y2);

	}

	/**
	 * @return Returns the x1.
	 */
	public synchronized int getX1() {
		return x1;
	}

	/**
	 * @param x1
	 *            The x1 to set.
	 */
	public synchronized void setX1(int x1) {
		this.x1 = x1;
	}

	/**
	 * @return Returns the x2.
	 */
	public synchronized int getX2() {
		return x2;
	}

	/**
	 * @param x2
	 *            The x2 to set.
	 */
	public synchronized void setX2(int x2) {
		this.x2 = x2;
	}

	/**
	 * @return Returns the y1.
	 */
	public synchronized int getY1() {
		return y1;
	}

	/**
	 * @param y1
	 *            The y1 to set.
	 */
	public synchronized void setY1(int y1) {
		this.y1 = y1;
	}

	/**
	 * @return Returns the y2.
	 */
	public synchronized int getY2() {
		return y2;
	}

	/**
	 * @param y2
	 *            The y2 to set.
	 */
	public synchronized void setY2(int y2) {
		this.y2 = y2;
	}

}
