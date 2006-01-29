import java.awt.Color;

import rsspubsubframework.DisplayableObject;
import rsspubsubframework.Message;
import rsspubsubframework.Node;

public class PubSubNode extends Node {

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

}
