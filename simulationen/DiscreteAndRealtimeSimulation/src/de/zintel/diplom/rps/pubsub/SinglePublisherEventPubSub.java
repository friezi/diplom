package de.zintel.diplom.rps.pubsub;
import de.zintel.diplom.feed.RSSEventFeedFactory;
import de.zintel.diplom.rps.server.RSSServerNode;
import de.zintel.diplom.simulation.SimParameters;

/**
 * This PubSub-class is intended to simulate a network with only one publisher, the other clinets are plain subscribers. The first
 * defined client will be the publisher.
 */

/**
 * @author friezi
 * 
 */
public class SinglePublisherEventPubSub extends DfltRefreshRateEventPubSub {

	protected static final long PUBLISHER_ID = 0;

	protected static long sp_counter = 0;

	protected long sp_id;

	/**
	 * @param xp
	 * @param yp
	 * @param rssEventFeedFactory
	 * @param params
	 */
	public SinglePublisherEventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {

		super(xp, yp, rssEventFeedFactory, params);
		sp_id = sp_counter;
		sp_counter++;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see CongestionControlEventPubSub#updateFeedRequestTimer(long)
	 */
	@Override
	protected synchronized void updateFeedRequestTimer(long interval) {

		if ( sp_id == PUBLISHER_ID )
			super.updateFeedRequestTimer(interval);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#startFeedRequestTimer()
	 */
	@Override
	public void startFeedRequestTimer() {

		if ( sp_id == PUBLISHER_ID )
			super.startFeedRequestTimer();

	}

	/* (non-Javadoc)
	 * @see PubSubNode#setRSSServer(RSSServerNode)
	 */
	@Override
	public void setRSSServer(RSSServerNode rssServer) {

		if ( sp_id == PUBLISHER_ID )
			super.setRSSServer(rssServer);

	}

}
