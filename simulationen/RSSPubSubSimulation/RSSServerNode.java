import rsspubsubframework.*;

import java.awt.*;
import java.util.*;

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
	};

	// just to avoid NullPointerException
	protected RSSFeedRepresentationFactory rssFeedRepresentationFactory = new RSSFeedRepresentationFactory() {
		public RSSFeedRepresentation newRSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed) {
			return new RSSFeedRepresentation(null, null);
		}
	};

	protected RSSServerNodeStatistics statistics = new RSSServerNodeStatistics();

	protected RSSFeedRepresentation rssFeedRepresentation;

	protected SimParameters params;

	protected int minUpIntv;

	protected int maxUpIntv;

	protected int ttl;

	protected float uploadScalingFactor = 1.0F;

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
		this.minUpIntv = params.minUpIntv;
		this.maxUpIntv = params.maxUpIntv;
		this.ttl = params.ttl;
		setColor(java.awt.Color.orange);
	}

	@Override
	public void init() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.Node#receiveMessage(rsspubsubframework.Message)
	 */
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
			Thread.sleep((int)(Engine.getSingleton().getTimerPeriod() * m.getRuntime() * getUploadScalingFactor()));
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.DisplayableObject#size()
	 */
	protected int size() {
		return 20;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.DisplayableObject#draw(java.awt.Graphics, int,
	 *      int)
	 */
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
	 * @return Returns the uploadScalingFactor.
	 */
	public synchronized float getUploadScalingFactor() {
		return uploadScalingFactor;
	}

	/**
	 * @param uploadScalingFactor
	 *            The uploadScalingFactor to set.
	 */
	public synchronized void setUploadScalingFactor(float uploadScalingFactor) {
		this.uploadScalingFactor = uploadScalingFactor;
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

	protected void showMoreInfo(InfoWindow moreinfowindow) {
	}

}
