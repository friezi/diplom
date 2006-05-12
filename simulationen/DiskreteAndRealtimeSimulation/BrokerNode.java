import rsspubsubframework.*;
import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import java.util.*;
import java.awt.*;

public abstract class BrokerNode extends Node implements BrokerType, Observer {
	
	static Color BLOCKEDCOLOR= new Color((float) 0.5, 0, 0);

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

	}

	@Override
	protected void receiveMessage(Message m) {

	}

	/**
	 * It simualtions an uploading of a message: it just waits the amount of
	 * time the message will take to arrive
	 * 
	 * @param m
	 *            the message
	 * @throws Exception
	 */
	protected void upload(Message m) {
		try {
			Thread.sleep(Engine.getSingleton().getTimerPeriod() * m.getRuntime());
		} catch ( Exception e ) {
			System.out.println("Exception: " + e);
		}
	}

	protected int size() {
		return 30;
	}

	protected void draw(java.awt.Graphics g, int x, int y) {

		g.setColor(color());
		int s = size();
		int x1 = x - s / 2;
		int y1 = y - s / 2;
		int width = s;
		int height = s;
		g.fillRect(x1, y1, width, height);
		g.setColor(java.awt.Color.black);
		g.drawRect(x1, y1, width, height);
		String t = text();
		java.awt.FontMetrics fm = g.getFontMetrics();
		g.setColor(textColor());
		g.drawString(t, x - fm.stringWidth(t) / 2, y + fm.getHeight() / 2);

		if (isBlocked() == true) {
			g.setColor(BLOCKEDCOLOR);
			crossit(g, x1, y1, width, height);
		}

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
	public boolean pointWhithin(Point point) {

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.Node#whithinRectangle(java.awt.Point,
	 *      java.awt.Point)
	 */
	@Override
	public boolean whithinRectangle(Point point1, Point point2) {

		int s = size();
		int x1 = this.xPos() - s / 2;
		int y1 = this.yPos() - s / 2;
		int x2 = this.xPos() + s / 2;
		int y2 = this.yPos() + s / 2;

		if ( point1.x <= x1 && point2.x >= x2 && point1.y <= y1 && point2.y >= y2 ) {
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
	public void callbackRegisterAtBroker(BrokerType arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.BrokerType#unregister(rsspubsubframework.BrokerType)
	 * 
	 * does nothing so far!
	 */
	public void callbackUnregisterFromBroker(BrokerType arg0) {

	}

	/* (non-Javadoc)
	 * @see rsspubsubframework.BrokerType#callbackUnregisterFromAllBrokers()
	 */
	public void callbackUnregisterFromAllBrokers() {
		
	}

	protected void removeConnection(BrokerNode node1, BrokerNode node2) {
		Engine.getSingleton().removeEdgeFromNodes(node1, node2);
	}

	/* (non-Javadoc)
	 * @see rsspubsubframework.BrokerType#showInfo()
	 */
	public void showInfo() {
		
	}

	protected void showMoreInfo(InfoWindow moreinfowindow) {
	}

}
