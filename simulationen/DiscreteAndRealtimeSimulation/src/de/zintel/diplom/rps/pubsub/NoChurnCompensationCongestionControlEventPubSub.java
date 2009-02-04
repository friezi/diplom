package de.zintel.diplom.rps.pubsub;

import de.zintel.diplom.feed.RSSEventFeedFactory;
import de.zintel.diplom.messages.InitialBrokerRSSFeedMessage;
/**
 * 
 */
import de.zintel.diplom.simulation.SimParameters;

/**
 * It won't try to compensate churn-influence
 * 
 * @author Friedemann Zintel
 * 
 */
public class NoChurnCompensationCongestionControlEventPubSub extends CongestionControlEventPubSub {

	/**
	 * @param xp
	 * @param yp
	 * @param rssEventFeedFactory
	 * @param params
	 */
	public NoChurnCompensationCongestionControlEventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {
		super(xp, yp, rssEventFeedFactory, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see CongestionControlEventPubSub#storeInitialBrokerInformation(InitialBrokerRSSFeedMessage)
	 */
	@Override
	protected void storeInitialBrokerInformation(InitialBrokerRSSFeedMessage ibrm) {
		// don' handle
	}

}
