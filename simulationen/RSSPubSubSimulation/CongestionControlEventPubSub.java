import java.util.Date;
import java.util.Random;

/**
 * 
 */

/**
 * @author Friedemann Zintel
 *
 */
public class CongestionControlEventPubSub extends EventPubSub {

	/**
	 * @param xp
	 * @param yp
	 * @param rssEventFeedFactory
	 * @param params
	 */
	public CongestionControlEventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {
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
	protected long calculateRequestTimeout() {

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


}
