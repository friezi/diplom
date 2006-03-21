import java.util.Set;

import rsspubsubframework.Node;

/**
 * 
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class AdjustingEventBroker extends AdjustingBroker {

	protected RSSFeedFactory rssFeedFactory;

	public AdjustingEventBroker(int xp, int yp, RSSFeedFactory rssFeedFactory, SimParameters params) {

		super(xp, yp, params);
		this.rssFeedFactory = rssFeedFactory;
		// TODO Auto-generated constructor stub
	}
	
	protected void handleRSSFeedMessage(RSSFeedMessage fm){

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
