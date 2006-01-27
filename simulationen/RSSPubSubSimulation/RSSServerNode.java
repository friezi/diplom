import rsspubsubframework.DisplayableObject;
import rsspubsubframework.Message;
import rsspubsubframework.Node;

public class RSSServerNode extends Node {

	// protected NodeFactory nf;

	// just to avoid NullPointerException
	protected RSSFeedFactory rssFeedFactory = new RSSFeedFactory() {
		public RSSFeed newRSSFeed() {
			return new RSSFeed();
		}
	};

	// just to avoid NullPointerException
	protected RSSFeedRepresentationFactory rssFeedRepresentationFactory = new RSSFeedRepresentationFactory() {
		public RSSFeedRepresentation newRSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed) {
			return new RSSFeedRepresentation(null, null);
		}
	};

	public RSSServerNode(int xp, int yp) {
		super(xp, yp);
		setColor(java.awt.Color.orange);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void receiveMessage(Message m) {
		// TODO Auto-generated method stub

	}

	protected int size() {
		return 20;
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

	public void setRssFeedRepresentationFactory(
			RSSFeedRepresentationFactory rss_feed_representation_factory) {
		this.rssFeedRepresentationFactory = rss_feed_representation_factory;
	}

	public void setRssFeedFactory(RSSFeedFactory rssFeedFactory) {
		this.rssFeedFactory = rssFeedFactory;
	}

}
