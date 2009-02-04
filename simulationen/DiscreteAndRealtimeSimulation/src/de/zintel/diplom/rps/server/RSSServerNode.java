package de.zintel.diplom.rps.server;
import java.awt.Color;
import java.awt.Point;
import java.util.Observable;
import java.util.Observer;

import de.zintel.diplom.feed.RSSFeed;
import de.zintel.diplom.feed.RSSFeedFactory;
import de.zintel.diplom.feed.RSSFeedGeneralContent;
import de.zintel.diplom.feed.RSSFeedRepresentation;
import de.zintel.diplom.feed.RSSFeedRepresentationFactory;
import de.zintel.diplom.graphics.DisplayableObject;
import de.zintel.diplom.gui.InfoWindow;
import de.zintel.diplom.simulation.InternalMessage;
import de.zintel.diplom.simulation.Message;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.simulation.TransferMessage;
import de.zintel.diplom.statistics.RSSServerNodeStatistics;
import de.zintel.diplom.synchronization.AbstractTimerFactory;
import de.zintel.diplom.util.ObserverJComponent;

/**
 * A class representing a RSS-Server
 * 
 * @author Friedemann Zintel
 * 
 */
public abstract class RSSServerNode extends Node implements RSSServerType {

	// protected NodeFactory nf;

	// just to avoid NullPointerException
	protected RSSFeedFactory rssFeedFactory = new RSSFeedFactory() {
		public RSSFeed newRSSFeed(RSSFeedGeneralContent generalContent) {
			return new RSSFeed(generalContent);
		}

		public void addFeedObserver(Observer observer) {
			
		}

		public void deleteFeedObserver(Observer observer) {
			
		}

		public Observable getFeedNotifier() {
			return null;
		}

		public ObserverJComponent createAndStartNewFeedDisplay() {
			return null;
		}
	};

	// just to avoid NullPointerException
	protected RSSFeedRepresentationFactory rssFeedRepresentationFactory = new RSSFeedRepresentationFactory() {
		public RSSFeedRepresentation newRSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed) {
			return new RSSFeedRepresentation(null, null);
		}

		public RSSFeedRepresentation newRSSFeedRepresentation(RSSFeed feed) {
			return newRSSFeedRepresentation(null, null);
		}
	};

	protected class ServiceTimeFactorNotifier extends Observable {

		public void notifyObservers(Float value) {
			setChanged();
			super.notifyObservers(value);
		}
	}

	private ServiceTimeFactorNotifier serviceTimeFactorNotifier = new ServiceTimeFactorNotifier();

	protected RSSServerNodeStatistics statistics = new RSSServerNodeStatistics();

	protected RSSFeedRepresentation rssFeedRepresentation;

	protected SimParameters params;

	private AbstractTimerFactory timerfactory;

	protected int minUpIntv;

	protected int maxUpIntv;

	protected int ttl;

	protected float serviceTimeFactor = 1.0F;

	/**
	 * @param xp
	 *            x-position
	 * @param yp
	 *            y-position
	 * @param params
	 *            relevant parameters
	 */
	public RSSServerNode(int xp, int yp, SimParameters params) {
		super(xp, yp);
		this.params = params;
		this.minUpIntv = params.getMinUpIntv();
		this.maxUpIntv = params.getMaxUpIntv();
		this.ttl = params.getTtl();
		setColor(java.awt.Color.orange);

		timerfactory = new AbstractTimerFactory(params);

	}

	@Override
	public RSSServerNode register() {
		return (RSSServerNode) super.register();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Node#receiveMessage(Message)
	 */
	@Override
	protected void receiveMessage(Message m) {

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

	/**
	 * Does nothing! Override it in subclasses!
	 * 
	 * @param m
	 *            the Message
	 */
	protected void handleStandardMessage(Message m) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.DisplayableObject#size()
	 */
	@Override
	public int size() {
		return 20;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.DisplayableObject#draw(java.awt.Graphics, int,
	 *      int)
	 */
	@Override
	protected void draw(java.awt.Graphics g, int x, int y) {

		int s = size();

		g.setColor(Color.red);
		g.fillRect(x - (s + 9) / 2, y - (s + 4) / 2, s + 9, s + 4);
		// g.setColor(java.awt.Color.black);
		// g.drawRect(x - (s + 8) / 2, y - (s+3) / 2, s + 8, s+3);

		g.setColor(color());
		g.fillRect(x - (s + 5) / 2, y - s / 2, s + 5, s);
		g.setColor(java.awt.Color.black);
		g.drawRect(x - (s + 5) / 2, y - s / 2, s + 5, s);

		String t = text();
		java.awt.FontMetrics fm = g.getFontMetrics();
		g.setColor(textColor());
		g.drawString(t, x - fm.stringWidth(t) / 2, y + fm.getHeight() / 2);

	}

	/**
	 * @param rss_feed_representation_factory
	 */
	public void setRssFeedRepresentationFactory(RSSFeedRepresentationFactory rss_feed_representation_factory) {
		this.rssFeedRepresentationFactory = rss_feed_representation_factory;
	}

	/**
	 * @return the rssFeedRepresentationFactory
	 */
	public RSSFeedRepresentationFactory getRssFeedRepresentationFactory() {
		return rssFeedRepresentationFactory;
	}

	/**
	 * @param rssFeedFactory
	 */
	public void setRssFeedFactory(RSSFeedFactory rssFeedFactory) {
		this.rssFeedFactory = rssFeedFactory;
	}

	/**
	 * @return the rssFeedFactory
	 */
	public RSSFeedFactory getRssFeedFactory() {
		return rssFeedFactory;
	}

	/**
	 * @return the rssFeedRepresentation
	 */
	public RSSFeedRepresentation getRssFeedRepresentation() {
		return rssFeedRepresentation;
	}

	/**
	 * @param rssFeedRepresentation
	 */
	public void setRssFeedRepresentation(RSSFeedRepresentation rssFeedRepresentation) {
		this.rssFeedRepresentation = rssFeedRepresentation;
	}

	/**
	 * @return Returns the minUpIntv.
	 */
	public int getMinUpIntv() {
		return minUpIntv;
	}

	/**
	 * @param minUpIntv
	 *            The minUpIntv to set.
	 */
	public void setMinUpIntv(int minUpIntv) {
		this.minUpIntv = minUpIntv;
	}

	/**
	 * @return Returns the maxUpIntv.
	 */
	public int getMaxUpIntv() {
		return maxUpIntv;
	}

	/**
	 * @param maxUpIntv
	 *            The maxUpIntv to set.
	 */
	public void setMaxUpIntv(int maxUpIntv) {
		this.maxUpIntv = maxUpIntv;
	}

	/**
	 * @return Returns the ttl.
	 */
	public int getTtl() {
		return ttl;
	}

	/**
	 * @param ttl
	 *            The ttl to set.
	 */
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	/**
	 * @return Returns the serviceTimeFactor.
	 */
	public synchronized float getServiceTimeFactor() {
		return serviceTimeFactor;
	}

	/**
	 * @param serviceTimeFactor
	 *            The serviceTimeFactor to set.
	 */
	public synchronized void setServiceTimeFactor(float serviceTimeFactor) {

		// notification only on change: it's very important to break circles of notifiers
		if ( this.serviceTimeFactor != serviceTimeFactor ) {
			
			this.serviceTimeFactor = serviceTimeFactor;
			serviceTimeFactorNotifier.notifyObservers(new Float(serviceTimeFactor));
			
		}
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
		setColor(Color.orange);
	}

	/**
	 * @return Returns the statistics.
	 */
	public RSSServerNodeStatistics getStatistics() {
		return statistics;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.RSSServerType#showInfo()
	 */
	public void showInfo() {
	}

	public void showMoreInfo(InfoWindow moreinfowindow) {
	}

	/**
	 * @return Returns the serviceTimeFactorNotifier.
	 */
	public synchronized ServiceTimeFactorNotifier getServiceTimeFactorNotifier() {
		return serviceTimeFactorNotifier;
	}

	/**
	 * @param serviceTimeFactorNotifier
	 *            The serviceTimeFactorNotifier to set.
	 */
	public synchronized void setServiceTimeFactorNotifier(ServiceTimeFactorNotifier serviceTimeFactorNotifier) {
		this.serviceTimeFactorNotifier = serviceTimeFactorNotifier;
	}

	public synchronized void addServiceTimeFactorObserver(Observer observer) {
		serviceTimeFactorNotifier.addObserver(observer);
	}

	public synchronized void deleteServiceTimeFactorObserver(Observer observer) {
		serviceTimeFactorNotifier.deleteObserver(observer);
	}

	/**
	 * @return Returns the timerfactory.
	 */
	public AbstractTimerFactory getTimerfactory() {
		return timerfactory;
	}

}
