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
public class GORectangle extends GraphicalObject {

	private int x1;

	private int y1;

	private int x2;

	private int y2;

	/**
	 * 
	 */
	public GORectangle(int x1, int y1, int x2, int y2) {
		setValues(x1, y1, x2, y2);
	}

	public GORectangle(Point point) {
		setValues(point.x, point.y, point.x, point.y);
	}

	private void setValues(int x1, int y1, int x2, int y2) {

		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		setColor(Color.black);

	}

	protected void draw(Graphics g) {
		int x = Engine.scaleX(this.x1);
		int y = Engine.scaleY(this.y1);
		int width = Engine.scaleX(this.x2) - x;
		int height = Engine.scaleY(this.y2) - y;
		g.setColor(color);
		g.drawRect(x, y, width, height);
	}

	/* (non-Javadoc)
	 * @see rsspubsubframework.GraphicalObject#drawobj(java.awt.Graphics)
	 */
	@Override
	public synchronized final void drawobj(Graphics g) {
		draw(g);
	}

	/**
	 * @param x1 The x1 to set.
	 */
	public void setX1(int x1) {
		this.x1 = x1;
	}

	/**
	 * @param x2 The x2 to set.
	 */
	public void setX2(int x2) {
		this.x2 = x2;
	}

	/**
	 * @param y1 The y1 to set.
	 */
	public void setY1(int y1) {
		this.y1 = y1;
	}

	/**
	 * @param y2 The y2 to set.
	 */
	public void setY2(int y2) {
		this.y2 = y2;
	}

	/**
	 * @return Returns the y1.
	 */
	public synchronized int getY1() {
		return y1;
	}

	/**
	 * @return Returns the y2.
	 */
	public synchronized int getY2() {
		return y2;
	}

	public synchronized void resetX2Y2(Point point) {
		this.x2 = point.x;
		this.y2 = point.y;
	}

	/**
	 * @return Returns the x1.
	 */
	public synchronized int getX1() {
		return x1;
	}

	/**
	 * @return Returns the x2.
	 */
	public synchronized int getX2() {
		return x2;
	}

}
