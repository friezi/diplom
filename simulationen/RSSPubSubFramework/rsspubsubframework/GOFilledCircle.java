/**
 * 
 */
package rsspubsubframework;

import java.awt.Graphics;
import java.awt.*;

/**
 * @author friezi
 * 
 */
public class GOFilledCircle extends GraphicalObject {

	private int x;

	private int y;

	private int radius;

	/**
	 * 
	 */
	public GOFilledCircle(int x, int y, int radius) {
		super();
		setValues(x, y, radius);
	}

	private void setValues(int x, int y, int radius) {
		this.x = x - (radius / 2);
		this.y = y - (radius / 2);
		this.radius = radius;
		setColor(Color.black);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.GraphicalObject#drawobj(java.awt.Graphics)
	 */
	@Override
	synchronized final void drawobj(Graphics g) {

		int x = Engine.scaleX(this.x);
		int y = Engine.scaleY(this.y);
		g.setColor(color);
		g.fillOval(x, y, radius, radius);

	}

	/**
	 * @return Returns the radius.
	 */
	public int getRadius() {
		return radius;
	}

	/**
	 * @return Returns the x.
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return Returns the y.
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param x
	 *            The x to set.
	 */
	public synchronized void setX(int x) {
		this.x = x;
	}

	/**
	 * @param y
	 *            The y to set.
	 */
	public synchronized void setY(int y) {
		this.y = y;
	}

}
