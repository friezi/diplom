package de.zintel.diplom.rps.pubsub;

import de.zintel.diplom.feed.RSSEventFeedFactory;
import de.zintel.diplom.messages.RSSFeedRichMessage;
/**
 * 
 */
import de.zintel.diplom.simulation.SimParameters;

/**
 * 
 * This PubSub does no balancing of the rtt-value for congestion-control.
 * 
 * @author Friedemann Zintel
 * 
 */
public class NoBalancingCongestionControlEventPubSub extends CongestionControlEventPubSub {

	/**
	 * @param xp
	 * @param yp
	 * @param rssEventFeedFactory
	 * @param params
	 */
	public NoBalancingCongestionControlEventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {
		super(xp, yp, rssEventFeedFactory, params);
	}

	/*
	 * this method does nothing! (non-Javadoc)
	 * 
	 * @see CongestionControlEventPubSub#storeRichInformation(RSSFeedRichMessage)
	 * 
	 * 
	 */
	@Override
	protected void storeRichInformation(RSSFeedRichMessage rfrm) {
	}

	/* (non-Javadoc)
	 * @see CongestionControlEventPubSub#updateRequestTimerByOldFeedFromBroker()
	 */
	@Override
	protected synchronized void updateFeedRequestTimerByOldFeedFromBroker() {
	}

}
