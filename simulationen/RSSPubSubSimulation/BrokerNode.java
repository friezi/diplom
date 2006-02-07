import rsspubsubframework.*;

import java.util.*;
import java.awt.*;

public class BrokerNode extends Node implements BrokerType, Observer {

	protected SimParameters params;

	// A set of all connected brokers
	private Set<BrokerNode> brokers = new HashSet<BrokerNode>();

	// A set of all connected subscribers;
	private Set<PubSubNode> subscribers = new HashSet<PubSubNode>();

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

	synchronized protected void addToBrokers(BrokerNode broker) {
		brokers.add(broker);
	}

	synchronized protected void addToSubscribers(PubSubNode subscriber) {
		subscribers.add(subscriber);
	}

	synchronized protected void removeFromBrokers(BrokerNode broker) {
		brokers.remove(broker);
	}

	synchronized protected void removeFromSubscribers(PubSubNode subscriber) {
		subscribers.remove(subscriber);
	}

	synchronized protected int getSubscribersSize() {
		return subscribers.size();
	}

	synchronized protected int getBrokersSize() {
		return brokers.size();
	}

	synchronized protected Set<BrokerNode> getBrokers() {
		Set<BrokerNode> newbrokers = new HashSet<BrokerNode>();
		newbrokers.addAll(brokers);
		return newbrokers;
	}

	synchronized protected Set<PubSubNode> getSubscribers() {
		Set<PubSubNode> newsubscribers = new HashSet<PubSubNode>();
		newsubscribers.addAll(subscribers);
		return newsubscribers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {

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

		if ( x1 <= point.x && x2 >= point.x && y1 <= point.y && y2 >= point.y ) {
			return true;
		} else {
			return false;
		}
	}

	public void setDefaultColor() {
		setColor(new java.awt.Color((float) 0.5, (float) 0.5, 0));
	}

	/**
	 * deletes all the obserevrs which are observing gui-operations.
	 */
	public void deleteGuiObservers() {
		peers.deleteAddObserver(this);
		peers.deleteRemoveObserver(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.BrokerType#register(rsspubsubframework.BrokerType)
	 * 
	 * does nothing so far!
	 */
	public void register(BrokerType arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.BrokerType#unregister(rsspubsubframework.BrokerType)
	 * 
	 * does nothing so far!
	 */
	public void unregister(BrokerType arg0) {
		// TODO Auto-generated method stub

	}

	protected void removeConnection(BrokerNode node1, BrokerNode node2) {
		Engine.getSingleton().removeEdgeFromNodes(node1, node2);
	}

}
