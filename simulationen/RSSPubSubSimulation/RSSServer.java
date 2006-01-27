import rsspubsubframework.*;

public class RSSServer extends RSSServerNode {

	public RSSServer(int xp, int yp) {
		super(xp, yp);
		// TODO Auto-generated constructor stub
	}

	protected void receiveMessage(Message m) {

		if ( m instanceof RSSFeedRequestMessage ) {
			RSSFeed feed = rssFeedFactory.newRSSFeed();
			new RSSFeedMessage(this, m.getSrc(), feed, rssFeedRepresentationFactory
					.newRSSFeedRepresentation(null, feed), true);
		}

	}

}
