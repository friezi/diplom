package de.zintel.diplom.rps.pubsub;

/**
 * 
 */
import de.zintel.diplom.feed.RSSEventFeedFactory;
import de.zintel.diplom.simulation.SimParameters;

/**
 * The factor for the artt is quadratic.
 * 
 * @author Friedemann Zintel
 * 
 */
public class QuadraticArttCongestionControlEventPubSub extends CongestionControlEventPubSub {

	/**
	 * @param xp
	 * @param yp
	 * @param rssEventFeedFactory
	 * @param params
	 */
	public QuadraticArttCongestionControlEventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {
		super(xp, yp, rssEventFeedFactory, params);
	}

	@Override
	protected void recalculateArtt() {
		setArtt((long) (Math.pow(requestFeedTimerCounter, 2) * getRtt()));
	}

}
