package de.zintel.diplom.rps.broker;
import java.awt.Color;
import java.awt.Point;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import de.zintel.diplom.graphics.OverlayEdge;
import de.zintel.diplom.gui.InfoWindow;
import de.zintel.diplom.rps.pubsub.PubSubNode;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.InternalMessage;
import de.zintel.diplom.simulation.Message;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.simulation.TransferMessage;
import de.zintel.diplom.synchronization.AbstractTimerFactory;

public abstract class BrokerNode extends Node implements BrokerType, Observer {

	public static Color BLOCKEDCOLOR = new Color((float) 0.5, 0, 0);

	public static final Color DFLT_COLOR = new java.awt.Color((float) 0.5, (float) 0.5, 0);

	protected SimParameters params;

	// A set of all connected brokers
	private Set<BrokerNode> brokers = new HashSet<BrokerNode>();

	// A set of all connected subscribers;
	private Set<PubSubNode> subscribers = new HashSet<PubSubNode>();

	private AbstractTimerFactory timerfactory;

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
		setColor(DFLT_COLOR);

		timerfactory = new AbstractTimerFactory(params);

		// add observers
		peers.newAddObserver(this);
		peers.newRemoveObserver(this);

	}

	@Override
	public void start() {

	}

	public BrokerNode register() {

		Engine.getSingleton().addBroker(this);
		return (BrokerNode) super.register();
	}

	@Override
	public void receiveMessage(Message m) {

		if ( m instanceof InternalMessage )
			handleInternalMessage((InternalMessage) m);
		else if ( m instanceof TransferMessage )
			handleTransferMessage((TransferMessage) m);
		else
			System.err.println("got message from unsupported type!");

	}

	protected void handleInternalMessage(InternalMessage m) {
		handleStandardMessage(m);
	}

	protected void handleTransferMessage(TransferMessage m) {

		if ( isBlocked() == true )
			return;

		if ( m.getDestination() == this ) {

			handleStandardMessage(m);

		} else {

			// forward the message to next node on the way
			m.forward(this, m.getDestination());

		}

	}

	protected void handleStandardMessage(Message m) {
	}

	public int size() {
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

		if ( isBlocked() == true ) {
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

	protected void removeConnection(BrokerNode node1, BrokerNode node2) {
		getEngine().removeEdgeFromNodes(node1, node2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.BrokerType#showInfo()
	 */
	public void showInfo() {

	}

	protected void displayOverlayConnection(Node a, Node b) {
		new OverlayEdge(a, b).register();
	}

	protected void undisplayOverlayConnection(Node a, Node b) {
		Engine.getSingleton().removeOverlayEdgeFromNodes(a, b);
	}

	public void showMoreInfo(InfoWindow moreinfowindow) {
	}

	/**
	 * @return Returns the timerfactory.
	 */
	public AbstractTimerFactory getTimerfactory() {
		return timerfactory;
	}

}
