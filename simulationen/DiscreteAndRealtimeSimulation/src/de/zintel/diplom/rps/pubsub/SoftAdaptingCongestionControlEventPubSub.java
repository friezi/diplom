package de.zintel.diplom.rps.pubsub;

/**
 * 
 */
import de.zintel.diplom.feed.RSSEventFeedFactory;
import de.zintel.diplom.simulation.SimParameters;

/**
 * Soft adaption of current polling period: division by 2
 * 
 * @author Friedemann Zintel
 * 
 */
public class SoftAdaptingCongestionControlEventPubSub extends CongestionControlEventPubSub {

	/**
	 * @param xp
	 * @param yp
	 * @param rssEventFeedFactory
	 * @param params
	 */
	public SoftAdaptingCongestionControlEventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {
		super(xp, yp, rssEventFeedFactory, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see CongestionControlEventPubSub#recalculateRtt()
	 */
	@Override
	protected void recalculateRtt() {

		// if the counter is 0, the Feed is from an old sequence, calculation
		// would be faulty
		if ( requestFeedTimerCounter == 0 )
			return;

		if ( feedSequenceNumber != sequenceNumber )
			return;

		try {

			long delta_t = rssFeedMessageDate.getTime() - retransmissionSequence.get(feedRetransmissionSequenceNumber).getTime();

			long degrade = Math.max(delta_t, getRtt() / 2);

			if ( degrade < getPpp() )
				setRtt(getPpp());
			else
				setRtt(degrade);

		} catch ( NullPointerException e ) {
			// to prevent double retransmission-acks
		}

	}

	@Override
	protected void recalculateArtt() {
		setArtt(getRtt());
	}

}
