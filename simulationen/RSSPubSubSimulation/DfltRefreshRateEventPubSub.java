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
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see PubSub#handleNetworkSizeUpdateMessage(NetworkSizeUpdateMessage)
	 */
	@Override
	protected void handleNetworkSizeUpdateMessage(NetworkSizeUpdateMessage nsum) {
		// TODO Auto-generated method stub
		// don't handle this anymore 
	}

	/* (non-Javadoc)
	 * @see PubSub#calculateInterval()
	 */
	@Override
	protected long calculateInterval() {
		// TODO Auto-generated method stub

		Date now = new Date();
		Date feedDate = getFeed().getGeneralContent().getLastBuiltDate();
		int ttl = getFeed().getGeneralContent().getTtl();
		int diff = (int) ((now.getTime() - feedDate.getTime()) / 1000);
		if (diff > ttl)
			diff = ttl;
//			diff = 0;
		//		return (new Random().nextInt((spreadFactor) * ttl + 1) + (ttl - diff)) * 1000;
		return (long) ((new Random().nextFloat() * (params.maxRefreshRate) + (ttl - diff)) * 1000);
	}

	/* (non-Javadoc)
	 * @see PubSub#updateRequestTimerByNewFeed()
	 */
	@Override
	protected synchronized void updateRequestTimerByNewFeed() {
		// TODO Auto-generated method stub
		updateRequestTimer(calculateInterval());
	}

	/* (non-Javadoc)
	 * @see PubSub#updateRequestTimerByOldFeed()
	 */
	@Override
	protected synchronized void updateRequestTimerByOldFeed() {
		// TODO Auto-generated method stub
		updateRequestTimer(params.maxRefreshRate*1000);
	}

	
	

}
