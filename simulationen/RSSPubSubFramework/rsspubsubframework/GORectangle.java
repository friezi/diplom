/**
 * 
 */
package rsspubsubframework;

import java.awt.*;

/**
 * @author friezi
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
		// TODO Auto-generated constructor stub
		init(x1, y1, x2, y2);
	}

	public GORectangle(Point point) {
		init(point.x, point.y, point.x, point.y);
	}

	private void init(int x1, int y1, int x2, int y2) {

		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		setColor(Color.black);
		Engine.getSingleton().addGraphicalObject(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.DisplayableObject#draw(java.awt.Graphics, int,
	 *      int)
	 */
	protected void draw(Graphics g) {
		// TODO Auto-generated method stub
		int x = Engine.scaleX(this.x1);
		int y = Engine.scaleY(this.y1);
		int width = Engine.scaleX(this.x2) - x;
		int height = Engine.scaleY(this.y2) - y;
		g.setColor(color);
		g.drawRect(x, y, width, height);
	}

	synchronized final void drawobj(Graphics g) {
		draw(g);
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
