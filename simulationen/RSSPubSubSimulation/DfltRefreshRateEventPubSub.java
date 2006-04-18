import java.util.Date;
import java.util.Random;

/**
 * 
 */

/**
 * 
 * This PubSub refreshes the feeds according to a default refreshrate, given by the user
 * It doesn't care about the networksize.
 * 
 * @author Friedemann Zintel
 *
 */
public class DfltRefreshRateEventPubSub extends EventPubSub {
	
	

	/**
	 * @param xp
	 * @param yp
	 * @param rssEventFeedFactory
	 * @param params
	 */
	public DfltRefreshRateEventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {
		super(xp, yp, rssEventFeedFactory, params);
	}

	/* (non-Javadoc)
	 * @see PubSub#handleNetworkSizeUpdateMessage(NetworkSizeUpdateMessage)
	 */
	@Override
	protected void handleNetworkSizeUpdateMessage(NetworkSizeUpdateMessage nsum) {
		// don't handle this anymore 
	}

	/* (non-Javadoc)
	 * @see PubSub#calculateInterval()
	 */
	@Override
	protected long calculateNextRequestTimeout() {

		Date now = new Date();
		Date feedDate = getFeed().getGeneralContent().getLastBuiltDate();
		int ttl = getFeed().getGeneralContent().getTtl();
		int diffsecs = (int) ((now.getTime() - feedDate.getTime()) / 1000);
		if (diffsecs > ttl)
			diffsecs = ttl;
//			diff = 0;
		//		return (new Random().nextInt((spreadFactor) * ttl + 1) + (ttl - diff)) * 1000;
		return (long) ((new Random().nextFloat() * (params.maxRefreshRate) + (ttl - diffsecs)) * 1000);
	}

	/* (non-Javadoc)
	 * @see PubSub#updateRequestTimerByNewFeed()
	 */
	@Override
	protected synchronized void updateRequestTimerByNewFeed() {
		updateRequestTimer(calculateNextRequestTimeout());
	}

	/* (non-Javadoc)
	 * @see PubSub#updateRequestTimerByOldFeed()
	 */
	@Override
	protected synchronized void updateRequestTimerByOldFeed() {
		updateRequestTimer(params.maxRefreshRate*1000);
	}

	
	

}
