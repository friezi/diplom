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
	
	private long requestFeedTimerCounter;
	private long requestFeedTimeoutValue;

	/**
	 * @param xp
	 * @param yp
	 * @param rssEventFeedFactory
	 * @param params
	 */
	public CongestionControlEventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {
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
	protected long calculateRequestTimeout() {

		Date now = new Date();
		Date feedDate = getFeed().getGeneralContent().getLastBuiltDate();
		int ttl = getFeed().getGeneralContent().getTtl();
		int diff = (int) ((now.getTime() - feedDate.getTime()) / 1000);
		if (diff > ttl)
			diff = ttl;
//			diff = 0;
		//		return (new Random().nextInt((spreadFactor) * ttl + 1) + (ttl - diff)) * 1000;
		long timeout;
		if (requestFeedTimeoutValue<params.maxRefreshRate)
			timeout=params.maxRefreshRate;
		else
			timeout=requestFeedTimeoutValue;
		return (long) ((new Random().nextFloat() * (timeout) + (ttl - diff)) * 1000);
	}


	/* (non-Javadoc)
	 * @see PubSub#handleRequestFeedMessage(PubSub.RequestFeedMessage)
	 */
	@Override
	protected void handleRequestFeedMessage(RequestFeedMessage rfm) {
		// TODO Auto-generated method stub
		updateRequestTimer();
		incRequestFeedTimeoutValue();
		incRequestFeedTimerCounter();
		updateRequestTimer();
		super.handleRequestFeedMessage(rfm);
	}
	
	protected void updateRequestTimer(){
		feedRequestTask.cancel();
		feedRequestTimer.purge();
		feedRequestTask=new FeedRequestTask(this);
		feedRequestTimer.schedule(feedRequestTask,requestFeedTimeoutValue,requestFeedTimeoutValue);
	}
	
	/* (non-Javadoc)
	 * @see PubSub#updateRequestTimerByOldFeedFromBroker()
	 */
	@Override
	protected synchronized void updateRequestTimerByOldFeedFromBroker() {
		updateRequestTimer();
	}


	/* (non-Javadoc)
	 * @see PubSub#updateRequestTimerByNewFeedFromBroker()
	 */
	@Override
	protected synchronized void updateRequestTimerByNewFeedFromBroker() {
		updateRequestTimerByOldFeedFromBroker();
	}


	/* (non-Javadoc)
	 * @see PubSub#updateRequestTimerByNewFeedFromServer()
	 */
	@Override
	protected synchronized void updateRequestTimerByNewFeedFromServer() {
		// TODO Auto-generated method stub
	}


	/* (non-Javadoc)
	 * @see PubSub#updateRequestTimerByOldFeedFromServer()
	 */
	@Override
	protected synchronized void updateRequestTimerByOldFeedFromServer() {
	}


	protected void incRequestFeedTimeoutValue(){
		requestFeedTimeoutValue+=params.maxRefreshRate;
	}
	
	protected void resetRequestFeedTimeoutValue(){
		requestFeedTimeoutValue=params.maxRefreshRate;
	}
	
	protected void incRequestFeedTimerCounter(){
		requestFeedTimerCounter++;
	}
	
	protected void resetRequestFeedTimerCounter(){
		requestFeedTimerCounter=1;
	}

}
