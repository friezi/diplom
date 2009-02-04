package de.zintel.diplom.rps.pubsub;

/**
 * 
 */
import de.zintel.diplom.feed.RSSEventFeedFactory;
import de.zintel.diplom.simulation.SimParameters;

/**
 * @author Friedemann Zintel
 * 
 */
public class LogarithmicArttCongestionControlEventPubSub extends CongestionControlEventPubSub {

	/**
	 * @param xp
	 * @param yp
	 * @param rssEventFeedFactory
	 * @param params
	 */
	public LogarithmicArttCongestionControlEventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {
		super(xp, yp, rssEventFeedFactory, params);
	}

	@Override
	protected void recalculateArtt() {
		setArtt((long) (ld(requestFeedTimerCounter + 1) * getRtt()));
	}

}
