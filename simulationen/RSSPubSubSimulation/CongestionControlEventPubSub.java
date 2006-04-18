import java.util.Date;
import java.util.Random;

/**
 * 
 */

/**
 * This PubSub does congestion-control. It checks the server-overload and
 * adjusts its polling-behaviour.
 * 
 * @author Friedemann Zintel
 * 
 */
public class CongestionControlEventPubSub extends EventPubSub {

	private long requestFeedTimerCounter;

	private long requestFeedTimeoutValue;

	private Date requestFeedMessageDate;

	private Date rssFeedMessageDate;

	/**
	 * @param xp
	 * @param yp
	 * @param rssEventFeedFactory
	 * @param params
	 */
	public CongestionControlEventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory,
			SimParameters params) {
		super(xp, yp, rssEventFeedFactory, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#init()
	 */
	@Override
	public void init() {
		resetRequestFeedTimeoutValue();
		resetRequestFeedTimerCounter();
		super.init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#handleNetworkSizeUpdateMessage(NetworkSizeUpdateMessage)
	 */
	@Override
	protected void handleNetworkSizeUpdateMessage(NetworkSizeUpdateMessage nsum) {
		// don't handle this anymore
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#handleRequestFeedMessage(PubSub.RequestFeedMessage)
	 */
	@Override
	protected void handleRequestFeedMessage(RequestFeedMessage rfm) {

		incRequestFeedTimeoutValue();
		incRequestFeedTimerCounter();
		updateRequestTimer();
		requestFeedMessageDate = new Date();
		super.handleRequestFeedMessage(rfm);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see EventPubSub#handleRSSFeedMessage(RSSFeedMessage)
	 */
	@Override
	protected void handleRSSFeedMessage(RSSFeedMessage fm) {

		rssFeedMessageDate = new Date();
		super.handleRSSFeedMessage(fm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#calculateInterval()
	 */
	@Override
	protected long calculateNextRequestTimeout() {

		Date now = new Date();
		Date feedDate = getFeed().getGeneralContent().getLastBuiltDate();
		int ttl = getFeed().getGeneralContent().getTtl();
		int diffsecs = (int) ((now.getTime() - feedDate.getTime()) / 1000);
		if ( diffsecs > ttl )
			diffsecs = ttl;
		// diff = 0;
		// return (new Random().nextInt((spreadFactor) * ttl + 1) + (ttl -
		// diff)) * 1000;
		long timeoutsecs = requestFeedTimeoutValue / 1000;
		// if ( requestFeedTimeoutValue < getMaxRefreshRateMS() )
		// timeout = params.maxRefreshRate;
		// else
		// timeout = requestFeedTimeoutValue/1000;
		return (long) ((new Random().nextFloat() * timeoutsecs + (ttl - diffsecs)) * 1000);
	}

	protected void updateRequestTimer() {

		feedRequestTask.cancel();
		purgeFeedRequestTimer();
		feedRequestTask = new FeedRequestTask(this);
		feedRequestTimer.schedule(feedRequestTask, requestFeedTimeoutValue, requestFeedTimeoutValue);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimer(long)
	 */
	@Override
	protected synchronized void updateRequestTimer(long interval) {

		feedRequestTask.cancel();
		purgeFeedRequestTimer();
		feedRequestTask = new FeedRequestTask(this);
		feedRequestTimer.schedule(feedRequestTask, interval, requestFeedTimeoutValue);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimerByOldFeedFromBroker()
	 */
	@Override
	protected synchronized void updateRequestTimerByOldFeedFromBroker() {
		updateRequestTimer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimerByNewFeedFromBroker()
	 */
	@Override
	protected synchronized void updateRequestTimerByNewFeedFromBroker() {
		updateRequestTimer(calculateNextRequestTimeout());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimerByNewFeedFromServer()
	 */
	@Override
	protected synchronized void updateRequestTimerByNewFeedFromServer() {

		recalculateRequestTimeoutinterval();
		resetRequestFeedTimerCounter();
		updateRequestTimer(calculateNextRequestTimeout());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimerByOldFeedFromServer()
	 */
	@Override
	protected synchronized void updateRequestTimerByOldFeedFromServer() {

		recalculateRequestTimeoutinterval();
		resetRequestFeedTimerCounter();
		updateRequestTimer();

	}

	protected void recalculateRequestTimeoutinterval() {

		if ( requestFeedTimerCounter > 1 ) {
			// we had to request several times -> set the timeout to meanvalue

			requestFeedTimeoutValue = (requestFeedTimeoutValue * requestFeedTimerCounter + requestFeedTimeoutValue) / 2;

		} else {

			long roundtriptime = (rssFeedMessageDate.getTime() - requestFeedMessageDate.getTime());

			if ( roundtriptime < getMaxRefreshRateMS() )
				requestFeedTimeoutValue = getMaxRefreshRateMS();
			else
				requestFeedTimeoutValue = roundtriptime;
		}

	}

	protected void incRequestFeedTimeoutValue() {
		requestFeedTimeoutValue += getMaxRefreshRateMS();
	}

	protected void resetRequestFeedTimeoutValue() {
		requestFeedTimeoutValue = 0;
	}

	protected void incRequestFeedTimerCounter() {
		requestFeedTimerCounter++;
	}

	protected void resetRequestFeedTimerCounter() {
		requestFeedTimerCounter = 0;
	}

	/**
	 * returns the maxRefreshRate in miliseconds
	 * 
	 * @return
	 */
	protected long getMaxRefreshRateMS() {
		return params.maxRefreshRate * 1000;
	}

}
