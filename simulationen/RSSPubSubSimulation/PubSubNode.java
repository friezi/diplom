import java.awt.*;
import java.util.*;

import rsspubsubframework.*;

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

	protected class PreferredPollingRateNotifier extends Observable {

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

	protected PubSubNodeStatistics statistics;

	protected RSSFeedRepresentation rssFeedRepresentation;

	protected SimParameters params;

	protected RSSServerNode rssServer;

	protected int preferredPollingRate;

	private PreferredPollingRateNotifier preferredPollingRateNotifier = new PreferredPollingRateNotifier();

	//
	// private BrokerNode broker;

	protected HashSet<BrokerNode> brokerlist = new HashSet<BrokerNode>();

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
		setPreferredPollingRate(params.preferredPollingRate);
		this.statistics = new PubSubNodeStatistics(params);
		setColor(Color.blue);

		// add observers
		peers.newAddObserver(this);
		peers.newRemoveObserver(this);
	}

	@Override
	public void init() {

	}

	public void setRSSServer(RSSServerNode rss_server) {
		this.rssServer = rss_server;
	}

	@Override
	protected void receiveMessage(Message m) {

	}

	protected int size() {
		return 10;
	}

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
	 * @see rsspubsubframework.PubSubType#register(rsspubsubframework.BrokerType)
	 * 
	 * does nothing so far!
	 * 
	 */
	public void callbackRegisterAtBroker(BrokerType arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.PubSubType#unregister(rsspubsubframework.BrokerType)
	 * 
	 * does nothing so far!
	 */
	public void callbackUnregisterFromBroker(BrokerType arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {

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

	protected void showMoreInfo(InfoWindow moreinfowindow) {
	}

	/**
	 * @return Returns the preferredPollingRate.
	 */
	public synchronized int getPreferredPollingRate() {
		return preferredPollingRate;
	}

	/**
	 * @param preferredPollingRate
	 *            The preferredPollingRate to set.
	 */
	public synchronized void setPreferredPollingRate(int preferredPollingRate) {
		this.preferredPollingRate = preferredPollingRate;
		preferredPollingRateNotifier.notifyObservers(new Integer(preferredPollingRate));
	}

	/**
	 * @return Returns the preferredPollingRateNotifier.
	 */
	public PreferredPollingRateNotifier getPreferredPollingRateNotifier() {
		return preferredPollingRateNotifier;
	}

	/**
	 * @param preferredPollingRateNotifier
	 *            The preferredPollingRateNotifier to set.
	 */
	public void setPreferredPollingRateNotifier(PreferredPollingRateNotifier preferredPollingRateNotifier) {
		this.preferredPollingRateNotifier = preferredPollingRateNotifier;
	}

	public synchronized void addPreferredPollingRateObserver(Observer observer) {
		preferredPollingRateNotifier.addObserver(observer);
	}

	public synchronized void deletePreferredPollingRateObserver(Observer observer) {
		preferredPollingRateNotifier.deleteObserver(observer);
	}

}
