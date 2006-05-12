import java.util.*;

/**
 * A simple Broker which just sends all imcoming feeds to all neighbours
 * 
 * @author friezi
 *
 */
public class Broker extends BrokerNode {

	// a dummy-feed to start with and prevent NullPointerException
	private RSSFeed feed = new RSSFeed(new RSSFeedGeneralContent());

	public Broker(int xp, int yp, SimParameters params) {
		super(xp, yp, params);
	}

	public void receiveMessage(Message m) {

		// process only if not blocked
		if (isBlocked() == true)
			return;

		if (m instanceof RSSFeedMessage) {

			handleRSSFeedMessage((RSSFeedMessage) m);

		}
	}

	protected void handleRSSFeedMessage(RSSFeedMessage rfm) {

		// if message-feed is newer than our one, set it and send it to all
		// other peers
		if (rfm.getFeed().isNewerThan(getFeed())) {

			setFeed(rfm.getFeed());

			// send a new RSSFeedMessage to all other Brokers and
			// Subscribers
			Set<Node> peers = getPeers();
			for (Node peer : peers) {
				if (peer != rfm.getSrc())
					new RSSFeedMessage(this, peer, getFeed(), rfm.getRssFeedRepresentation().copyWith(null, getFeed()), params).send();
			}

		}

	}

	/**
	 * @return Returns the feed.
	 */
	public RSSFeed getFeed() {
		return feed;
	}

	/**
	 * @param feed
	 *            The feed to set.
	 */
	public void setFeed(RSSFeed feed) {
		this.feed = feed;
	}

}
