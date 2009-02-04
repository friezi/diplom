package de.zintel.diplom.rps.pubsub;

/**
 * 
 */
import de.zintel.diplom.feed.RSSEventFeedFactory;
import de.zintel.diplom.simulation.SimParameters;

/**
 * Adjustment to congestion happens in polynomial time without
 * Sequence-handling. Deprecated.
 * 
 * @author Friedemann Zintel
 * 
 */
public class NoSeqPolynomialCongestionControlEventPubSub extends CongestionControlEventPubSub {

	public NoSeqPolynomialCongestionControlEventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {
		super(xp, yp, rssEventFeedFactory, params);
	}

	@Override
	protected void recalculateRtt() {

		long delta_t = rssFeedMessageDate.getTime() - requestFeedMessageDate.getTime();

		if ( requestFeedTimerCounter > 1 ) {
			// we had to request several times -> set the timeout to meanvalue

			// setRtt(getRtt() * (long) ((Math.pow(requestFeedTimerCounter, 2) -
			// 1) / 3) + delta_t);
			// setRtt((long) (((requestFeedTimerCounter - 1) * getCpp() +
			// delta_t) / (requestFeedTimerCounter)));
			setRtt(getIcpp() * (requestFeedTimerCounter - 1) * (2 * requestFeedTimerCounter + 2) / 6 + delta_t);

			if ( getRtt() < 0 )
				System.err.println("WARNING!!!: CongestionControlEventPubSub.recalculateRtt(): getRtt() is negative: " + getRtt());

		} else {

			setRtt(delta_t);

		}

	}

	@Override
	protected void recalculateArtt() {
		setArtt(getRtt());
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
