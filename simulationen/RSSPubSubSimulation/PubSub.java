import rsspubsubframework.*;
import java.awt.*;

public class PubSub extends PubSubNode {

	protected RSSFeed feed;

	public PubSub(int xp, int yp) {
		super(xp, yp);
	}

	public void init() {
		new RSSFeedRequestMessage(this, getRssServer());
	}

	public void receiveMessage(Message m) {

		if ( m instanceof RSSFeedMessage ) {

			setFeed(((RSSFeedMessage) m).getFeed());
			setRssFeedRepresentation(getRssFeedRepresentationFactory().newRSSFeedRepresentation(this, getFeed()));
			getRssFeedRepresentation().represent();
			new RSSFeedRequestMessage(this, getRssServer());

		}
	}

	public RSSFeed getFeed() {
		return feed;
	}

	public void setFeed(RSSFeed feed) {
		this.feed = feed;
	}

}
