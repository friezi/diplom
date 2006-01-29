import rsspubsubframework.DisplayableObject;
import rsspubsubframework.Message;
import rsspubsubframework.Node;

/**
 * A class representing a RSS-Server
 * 
 * @author friezi
 * 
 */
public class RSSServerNode extends Node {

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

	protected RSSFeedRepresentation rssFeedRepresentation;

	protected SimParameters params;

	protected int minUpIntv;

	protected int maxUpIntv;

	protected int ttl;

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
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.Node#receiveMessage(rsspubsubframework.Message)
	 */
	@Override
	protected void receiveMessage(Message m) {
		// TODO Auto-generated method stub

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

}
