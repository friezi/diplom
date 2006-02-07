import java.awt.*;
import java.util.*;

import rsspubsubframework.*;

public class PubSubNode extends Node implements PubSubType, Observer {

	// a dummy to avoid NullPointerException
	protected RSSFeedRepresentationFactory rssFeedRepresentationFactory = new RSSFeedRepresentationFactory() {
		public RSSFeedRepresentation newRSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed) {
			return new RSSFeedRepresentation(null, null);
		}
	};

	protected RSSFeedRepresentation rssFeedRepresentation;

	protected SimParameters params;

	protected RSSServerNode rssServer;

	private BrokerNode broker;

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
		setColor(Color.blue);

		// add observers
		peers.newAddObserver(this);
		peers.newRemoveObserver(this);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	public void setRSSServer(RSSServerNode rss_server) {
		this.rssServer = rss_server;
	}

	@Override
	protected void receiveMessage(Message m) {
		// TODO Auto-generated method stub

	}

	protected int size() {
		return 10;
	}

	protected void draw(java.awt.Graphics g, int x, int y) {

		g.setColor(color());
		int s = size();
		g.fillRect(x - (s + 5) / 2, y - s / 2, s + 5, s);
		g.setColor(java.awt.Color.black);
		g.drawRect(x - (s + 5) / 2, y - s / 2, s + 5, s);
		String t = text();
		java.awt.FontMetrics fm = g.getFontMetrics();
		g.setColor(textColor());
		g.drawString(t, x - fm.stringWidth(t) / 2, y + fm.getHeight() / 2);

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

	/**
	 * @return Returns the broker.
	 */
	public BrokerNode getBroker() {
		return broker;
	}

	/**
	 * @param broker
	 *            The broker to set.
	 */
	public void setBroker(BrokerNode broker) {
		this.broker = broker;
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
		int x1 = this.xPos() - (s + 5) / 2;
		int y1 = this.yPos() - s / 2;
		int x2 = this.xPos() + (s + 5) / 2;
		int y2 = this.yPos() + s / 2;

		if ( x1 <= point.getX() && x2 >= point.getX() && y1 <= point.getY() && y2 >= point.getY() )
			return true;
		else
			return false;
	}

	public void setDefaultColor() {
		setColor(Color.blue);
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
	 * @see rsspubsubframework.PubSubType#register(rsspubsubframework.BrokerType)
	 * 
	 * does nothing so far!
	 * 
	 */
	public void register(BrokerType arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.PubSubType#unregister(rsspubsubframework.BrokerType)
	 * 
	 * does nothing so far!
	 */
	public void unregister(BrokerType arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}
