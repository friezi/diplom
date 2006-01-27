import rsspubsubframework.DisplayableObject;
import rsspubsubframework.Message;
import rsspubsubframework.Node;

public class PubSubNode extends Node {

	// just to avoid NullPointerException
	protected RSSFeedRepresentationFactory rssFeedRepresentationFactory = new RSSFeedRepresentationFactory() {
		public RSSFeedRepresentation newRSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed) {
			return new RSSFeedRepresentation(null, null);
		}
	};

	protected RSSServerNode rssServer;

	protected BrokerNode broker;

	public PubSubNode(int xp, int yp) {
		super(xp, yp);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	public void setRSSServer(RSSServerNode rss_server) {
		this.rssServer = rss_server;
	}

	public void setBroker(BrokerNode broker) {
		this.broker = broker;
	}

	@Override
	protected void receiveMessage(Message m) {
		// TODO Auto-generated method stub

	}

	protected java.awt.Color color() {
		return java.awt.Color.blue;
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

	public RSSServerNode getRssServer() {
		return rssServer;
	}

}
