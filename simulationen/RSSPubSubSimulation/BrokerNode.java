import rsspubsubframework.*;
import java.util.*;
import java.awt.*;

public class BrokerNode extends Node implements Observer {

	protected SimParameters params;

	// A set of all connected brokers
	protected Set<BrokerNode> brokers = new HashSet<BrokerNode>();

	// A set of all connected subscribers;
	protected Set<PubSubNode> subscribers = new HashSet<PubSubNode>();

	/**
	 * @param xp
	 *            x-position
	 * @param yp
	 *            y-position
	 * @param params
	 *            relevat parameters
	 */
	public BrokerNode(int xp, int yp, SimParameters params) {
		super(xp, yp);
		this.params = params;
		setColor(new java.awt.Color((float) 0.5, (float) 0.5, 0));

		// add observers
		peers.newAddObserver(this);
		peers.newRemoveObserver(this);

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void receiveMessage(Message m) {
		// TODO Auto-generated method stub

	}

	protected int size() {
		return 30;
	}

	protected void draw(java.awt.Graphics g, int x, int y) {

		g.setColor(color());
		int s = size();
		g.fillRect(x - s / 2, y - s / 2, s, s);
		g.setColor(java.awt.Color.black);
		g.drawRect(x - s / 2, y - s / 2, s, s);
		String t = text();
		java.awt.FontMetrics fm = g.getFontMetrics();
		g.setColor(textColor());
		g.drawString(t, x - fm.stringWidth(t) / 2, y + fm.getHeight() / 2);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

		if ( o instanceof Peers.AddNotifier ) {

			if ( arg instanceof BrokerNode )
				brokers.add((BrokerNode) arg);
			else if ( arg instanceof PubSubNode )
				subscribers.add((PubSubNode) arg);

		} else if ( o instanceof Peers.RemoveNotifier ) {

			if ( arg instanceof BrokerNode )
				brokers.remove((BrokerNode) arg);
			else if ( arg instanceof PubSubNode )
				subscribers.remove((PubSubNode) arg);
		}

	}

	/**
	 * This method checks if a given point is whithin the borders of the node.
	 * 
	 * @param point
	 *            the point to be checked
	 * @return true, if whithin borders, false otherwise
	 */
	public boolean whithinBorders(Point point) {

		int s = size();
		int x1 = this.xPos() - s / 2;
		int y1 = this.yPos() - s / 2;
		int x2 = this.xPos() + s / 2;
		int y2 = this.yPos() + s / 2;

		if ( x1 <= point.x && x2 >= point.x && y1 <= point.y && y2 >= point.y ){
			return true;
		}
		else {
			return false;
		}
	}

	public void setDefaultColor() {
		setColor(new java.awt.Color((float) 0.5, (float) 0.5, 0));
	}

}
