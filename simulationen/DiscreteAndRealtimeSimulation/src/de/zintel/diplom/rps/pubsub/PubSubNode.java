package de.zintel.diplom.rps.pubsub;
import java.awt.*;
import java.util.*;

import de.zintel.diplom.feed.RSSFeed;
import de.zintel.diplom.feed.RSSFeedRepresentation;
import de.zintel.diplom.feed.RSSFeedRepresentationFactory;
import de.zintel.diplom.graphics.DisplayableObject;
import de.zintel.diplom.graphics.OverlayEdge;
import de.zintel.diplom.gui.InfoWindow;
import de.zintel.diplom.rps.broker.BrokerNode;
import de.zintel.diplom.rps.broker.BrokerType;
import de.zintel.diplom.rps.server.RSSServerNode;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.InternalMessage;
import de.zintel.diplom.simulation.Message;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.RSSServerConnectionEdge;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.simulation.TransferMessage;
import de.zintel.diplom.statistics.PubSubNodeStatistics;
import de.zintel.diplom.synchronization.AbstractTimerFactory;

/**
 * A base-class for Publish/Subscribe-nodes
 * 
 * @author Friedemann Zintel
 * 
 */
public abstract class PubSubNode extends Node implements PubSubType, Observer {

	static Color BLOCKEDCOLOR = new Color((float) 0.5, 0, 0);

	// a dummy to avoid NullPointerException
	protected RSSFeedRepresentationFactory rssFeedRepresentationFactory = new RSSFeedRepresentationFactory() {
		public RSSFeedRepresentation newRSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed) {
			return new RSSFeedRepresentation(null, null);
		}

		public RSSFeedRepresentation newRSSFeedRepresentation(RSSFeed feed) {
			return newRSSFeedRepresentation(null, null);
		}
	};

	protected class PreferredPollingPeriodNotifier extends Observable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observable#notifyObservers(java.lang.Object)
		 */
		public void notifyObservers(Integer value) {
			setChanged();
			super.notifyObservers(value);
		}

	}

	protected PubSubNodeStatistics statistics = null;

	protected RSSFeedRepresentation rssFeedRepresentation = null;

	protected SimParameters params = null;

	protected RSSServerNode rssServer = null;

	/**
	 * the preferred polling-period in milliseconds
	 */
	protected int ppp = 0;

	/**
	 * current polling-period in milliseconds
	 */
	protected long cpp = 0;

	/**
	 * maximal polling-period in milliseconds
	 */
	protected long mpp = 0;

	private PreferredPollingPeriodNotifier preferredPollingPeriodNotifier = new PreferredPollingPeriodNotifier();

	private AbstractTimerFactory timerfactory = null;

	//
	// private BrokerNode broker;

	protected HashSet<BrokerNode> brokerlist = new HashSet<BrokerNode>();

	protected int registering = 0;

	/**
	 * @param xp
	 *            x-position
	 * @param yp
	 *            y-position
	 * @param params
	 *            relevant parameters
	 */
	public PubSubNode(int xp, int yp, SimParameters params) {

		super(xp, yp);
		this.params = params;
		this.statistics = new PubSubNodeStatistics(this, params);

		// add observers
		peers.newAddObserver(this);
		peers.newRemoveObserver(this);

		init();
	}

	public void init() {

		setPppSecs(params.getPreferredPollingPeriod());
		setMppSecs(params.getMaxPollingPeriod());
		setColor(Color.blue);

		timerfactory = new AbstractTimerFactory(params);
	}

	/* (non-Javadoc)
	 * @see Node#reset()
	 */
	public void reset() {

		super.reset();
		registering = 0;
		brokerlist.clear();
		resetCpp();
		rssFeedRepresentation = null;

	}

	@Override
	public void start() {

	}

	@Override
	public PubSubNode register() {

		// inform Engine
		Engine.getSingleton().addPubSub(this);
		return (PubSubNode) super.register();

	}

	public void setRSSServer(RSSServerNode rssServer) {
		this.rssServer = rssServer;
		displayRSSServerConnection(this, rssServer);
	}

	@Override
	public void receiveMessage(Message m) {

		if ( m instanceof InternalMessage )
			handleInternalMessage((InternalMessage) m);
		else if ( m instanceof TransferMessage )
			handleTransferMessage((TransferMessage) m);
		else
			System.err.println("WARNING: PubSubNode: got message from unsupported type!");

	}

	protected void handleInternalMessage(InternalMessage m) {
		handleStandardMessage(m);
	}

	protected void handleTransferMessage(TransferMessage m) {

		if ( m.getDestination() == this ) {

			if ( isBlocked() == true )
				return;

			handleStandardMessage(m);

		} else {

			// forward the message to next node on the way
			m.forward(this, m.getDestination());

		}

	}

	protected void handleStandardMessage(Message m) {
	}

	@Override
	public int size() {
		return 10;
	}

	@Override
	protected void draw(java.awt.Graphics g, int x, int y) {

		g.setColor(color());
		int s = size();
		int x1 = x - (s + 5) / 2;
		int y1 = y - s / 2;
		int width = s + 5;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSubType#getBrokers()
	 */
	public HashSet<BrokerType> getBrokers() {
		return new HashSet<BrokerType>(brokerlist);
	}

	/* (non-Javadoc)
	 * @see PubSubType#getNumberOfBrokers()
	 */
	public int getNumberOfBrokers() {
		return brokerlist.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSubType#isRegistering()
	 */
	public boolean isRegistering() {
		return (registering != 0);
	}

	public void setRssFeedRepresentationFactory(RSSFeedRepresentationFactory rssFeedRepresentationFactory) {
		this.rssFeedRepresentationFactory = rssFeedRepresentationFactory;
	}

	public RSSFeedRepresentationFactory getRssFeedRepresentationFactory() {
		return rssFeedRepresentationFactory;
	}

	public RSSServerNode getRssServer() {
		return rssServer;
	}

	public RSSFeedRepresentation getRssFeedRepresentation() {
		return rssFeedRepresentation;
	}

	public void setRssFeedRepresentation(RSSFeedRepresentation rssFeedRepresentation) {
		this.rssFeedRepresentation = rssFeedRepresentation;
	}

	//
	// /**
	// * @return Returns the broker.
	// */
	// public BrokerNode getBroker() {
	// return broker;
	// }
	//
	// /**
	// * @param broker
	// * The broker to set.
	// */
	// public void setBroker(BrokerNode broker) {
	// this.broker = broker;
	// }

	/**
	 * This method checks if a given point is whithin the borders of the node.
	 * 
	 * @param point
	 *            the point to be checked
	 * @return true, if whithin borders, false otherwise
	 */
	@Override
	public boolean pointWhithin(Point point) {

		int s = size();
		int x1 = this.xPos() - (s + 5) / 2;
		int y1 = this.yPos() - s / 2;
		int x2 = this.xPos() + (s + 5) / 2;
		int y2 = this.yPos() + s / 2;

		if ( x1 <= point.getX() && x2 >= point.getX() && y1 <= point.getY() && y2 >= point.getY() )
			return true;
		else
			return false;
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
		int x1 = this.xPos() - (s + 5) / 2;
		int y1 = this.yPos() - s / 2;
		int x2 = this.xPos() + (s + 5) / 2;
		int y2 = this.yPos() + s / 2;

		if ( point1.getX() <= x1 && point2.getX() >= x2 && point1.getY() <= y1 && point2.getY() >= y2 )
			return true;
		else
			return false;

	}

	@Override
	public void setDefaultColor() {
		setColor(Color.blue);
	}

	/**
	 * deletes all the observers which are observing gui-operations.
	 */
	public void deleteGuiObservers() {
		peers.deleteAddObserver(this);
		peers.deleteRemoveObserver(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {

	}

	protected void displayOverlayConnection(Node a, Node b) {
		new OverlayEdge(a, b).register();
	}

	protected void undisplayOverlayConnection(Node a, Node b) {
		Engine.getSingleton().removeOverlayEdgeFromNodes(a, b);
	}

	protected void displayRSSServerConnection(Node a, Node b) {
		new RSSServerConnectionEdge(a, b).register();
	}

	protected void undisplayRSSServerConnection(Node a, Node b) {
		Engine.getSingleton().removeRSSServerConnectionEdgeFromNodes(a, b);
	}

	/**
	 * @return Returns the statistics.
	 */
	public PubSubNodeStatistics getStatistics() {
		return statistics;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.PubSubType#showInfo()
	 */
	public void showInfo() {

	}

	public void showMoreInfo(InfoWindow moreinfowindow) {
	}

	/**
	 * @return Returns the preferredPollingPeriod.
	 */
	public synchronized int getPppSecs() {
		return ppp / 1000;
	}

	/**
	 * @param preferredPollingPeriod
	 *            The preferredPollingPeriod to set.
	 */
	public synchronized void setPppSecs(int preferredPollingPeriod) {
		this.ppp = preferredPollingPeriod * 1000;
		preferredPollingPeriodNotifier.notifyObservers(new Integer(preferredPollingPeriod));
	}

	/**
	 * @return Returns the preferredPollingPeriodNotifier.
	 */
	public PreferredPollingPeriodNotifier getPreferredPollingPeriodNotifier() {
		return preferredPollingPeriodNotifier;
	}

	/**
	 * @param preferredPollingPeriodNotifier
	 *            The preferredPollingPeriodNotifier to set.
	 */
	public void setPreferredPollingPeriodNotifier(PreferredPollingPeriodNotifier preferredPollingPeriodNotifier) {
		this.preferredPollingPeriodNotifier = preferredPollingPeriodNotifier;
	}

	public synchronized void addPreferredPollingPeriodObserver(Observer observer) {
		preferredPollingPeriodNotifier.addObserver(observer);
	}

	public synchronized void deletePreferredPollingPeriodObserver(Observer observer) {
		preferredPollingPeriodNotifier.deleteObserver(observer);
	}

	/**
	 * @return Returns the timerfactory.
	 */
	public AbstractTimerFactory getTimerfactory() {
		return timerfactory;
	}

	/**
	 * returns the preferredPollingPeriod in miliseconds
	 * 
	 * @return preferredPollingPeriod in miliseconds
	 */
	protected long getPpp() {
		return ppp;
	}

	/**
	 * @return Returns the mpp.
	 */
	public synchronized long getMpp() {
		return mpp;
	}

	/**
	 * @return Returns the maxPollingPeriod.
	 */
	public synchronized long getMppSecs() {
		return mpp / 1000;
	}

	/**
	 * @param maxPollingPeriod
	 *            The maxPollingPeriod to set.
	 */
	public synchronized void setMppSecs(long maxPollingPeriod) {
		this.mpp = maxPollingPeriod * 1000;
	}

	/**
	 * @return Returns the currentPollingPeriod.
	 */
	public synchronized long getCpp() {
		return cpp;
	}

	/**
	 * @param currentPollingPeriod
	 *            The currentPollingPeriod to set.
	 */
	public synchronized void setCpp(long currentPollingPeriod) {
		this.cpp = currentPollingPeriod;
	}

	protected void resetCpp() {
		setCpp(getPpp());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSubType#changeCpp()
	 */
	public void callbackResetCpp() {
		resetCpp();
	}

}
