package de.zintel.diplom.rps.pubsub;

/**
 * 
 */
import de.zintel.diplom.feed.RSSEventFeedFactory;
import de.zintel.diplom.simulation.SimParameters;

/**
 * Adjustment to congestion happens in polynomial time.
 * 
 * @author Friedemann Zintel
 * 
 */
public class PolynomialCongestionControlEventPubSub extends CongestionControlEventPubSub {

	public PolynomialCongestionControlEventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {
		super(xp, yp, rssEventFeedFactory, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see CongestionControlEventPubSub#incRto()
	 */
	@Override
	protected void incRto() {
		setRto((requestFeedTimerCounter + 1) * getIcpp());
	}

}
