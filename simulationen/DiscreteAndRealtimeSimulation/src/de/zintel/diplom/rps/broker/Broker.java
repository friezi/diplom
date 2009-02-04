package de.zintel.diplom.rps.broker;
import java.util.*;

import de.zintel.diplom.feed.RSSFeed;
import de.zintel.diplom.feed.RSSFeedGeneralContent;
import de.zintel.diplom.messages.RSSFeedMessage;
import de.zintel.diplom.simulation.Message;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;

/**
 * A simple Broker which just sends all imcoming feeds to all neighbours
 * 
 * @author Friedemann Zintel
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
				if (peer != rfm.getOrigin())
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

	/* (non-Javadoc)
	 * @see BrokerType#callbackRegisterAtBroker(BrokerType)
	 * 
	 * does nothing
	 */
	public void callbackRegisterAtBroker(BrokerType broker) {
		
	}

	/* (non-Javadoc)
	 * @see BrokerType#callbackUnregisterFromBroker(BrokerType)
	 * 
	 * does nothing
	 */
	public void callbackUnregisterFromBroker(BrokerType broker) {
		
	}

	/* (non-Javadoc)
	 * @see BrokerType#callbackUnregisterFromAllBrokers()
	 * 
	 * does nothing
	 */
	public void callbackUnregisterFromAllBrokers() {
		
	}

}
