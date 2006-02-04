import rsspubsubframework.*;
import java.util.*;

public class Broker extends BrokerNode {

	// a dummy-feed to start with and prevent NullPointerException
	private RSSFeed feed = new RSSFeed(new RSSFeedGeneralContent());

	public Broker(int xp, int yp, SimParameters params) {
		super(xp, yp, params);
		// TODO Auto-generated constructor stub
	}

	public void receiveMessage(Message m) {

		// process only if not blocked
		if ( isBlocked() == true )
			return;

		if ( m instanceof RSSFeedMessage ) {

			RSSFeedMessage fm = (RSSFeedMessage) m;

			// if message-feed is newer than our one, set it and send it to all
			// other peers
			if ( fm.getFeed().isNewerThan(getFeed()) ) {

				setFeed(fm.getFeed());

				// send a new RSSFeedMessage to all other Brokers and
				// Subscribers
				Set<Node> peers = getPeers();
				for ( Node peer : peers ) {
					if ( peer != fm.getSrc() )
						new RSSFeedMessage(this, peer, getFeed(), fm.getRssFeedRepresentation().copyWith(
								null, getFeed()), params);
				}

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
