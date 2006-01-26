import rsspubsubframework.*;

public class BrokerNode extends Node {

	// just to avoid NullPointerException
	protected RSSFeedRepresentationFactory rssFeedRepresentationFactory = new RSSFeedRepresentationFactory() {
		public RSSFeedRepresentation newRSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed) {
			return new RSSFeedRepresentation(null, null);
		}
	};

	public BrokerNode(int xp, int yp) {
		super(xp, yp);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void receiveMessage(Message m) {
		// TODO Auto-generated method stub

	}

	protected java.awt.Color color() {
		return new java.awt.Color((float) 0.5, (float) 0.5, 0);
	}

	protected int size() {
		return 30;
	}

	protected void draw(java.awt.Graphics g, int x, int y) {

		g.setColor(color());
		int s = size();
		g.fillRect(x - s / 2, y - s / 2, s, s);
		g.setColor(java.awt.Color.black);
		g.drawRect(x - s / 2, y - s / 2, s, s);
		String t = text();
		java.awt.FontMetrics fm = g.getFontMetrics();
		g.setColor(textColor());
		g.drawString(t, x - fm.stringWidth(t) / 2, y + fm.getHeight() / 2);

	}

	public void setRssFeedRepresentationFactory(RSSFeedRepresentationFactory rssFeedRepresentationFactory) {
		this.rssFeedRepresentationFactory = rssFeedRepresentationFactory;
	}

}
