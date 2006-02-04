import rsspubsubframework.*;
import java.awt.*;
import java.util.*;

public class PubSub extends PubSubNode {

	protected class FeedRequestTask extends TimerTask {

		public void run() {
			requestFeed();
			// feedRequestTimer.schedule(new
			// FeedRequestTask(),calculateInterval());
		}

	}

	// dummy feed to prevent NullPointerException
	protected RSSFeed feed = new RSSFeed(new RSSFeedGeneralContent());

	protected Timer feedRequestTimer = new Timer();

	protected FeedRequestTask feedRequestTask = new FeedRequestTask();

	protected int spreadFactor;

	public PubSub(int xp, int yp, SimParameters params) {
		super(xp, yp, params);
		this.spreadFactor = params.spreadFactor;
	}

	public void init() {
		requestFeed();
	}

	public void receiveMessage(Message m) {

		// process only if not blocked
		if ( isBlocked() == true )
			return;

		if ( m instanceof RSSFeedMessage ) {

			RSSFeedMessage fm = (RSSFeedMessage) m;

			// do only with new feeds
			if ( fm.getFeed().isNewerThan(getFeed()) ) {

				// show the feed
				setFeed(fm.getFeed());
				setRssFeedRepresentation(getRssFeedRepresentationFactory().newRSSFeedRepresentation(this,
						getFeed()));
				getRssFeedRepresentation().represent();

				updateRequestTimer();

				// send the feed to Broker, if we didn't get the message from
				// him
				if ( fm.getSrc() != getBroker() )
					new RSSFeedMessage(this, getBroker(), getFeed(), fm.getRssFeedRepresentation().copyWith(
							null, getFeed()), params);

			} else {

				// got an old feed; update timer only if sender is RSSServer
				if ( fm.getSrc() == getRssServer() ) {
					updateRequestTimer();
				}
			}
		}
	}

	protected long calculateInterval() {

		Date now = new Date();
		Date feedDate = getFeed().getGeneralContent().getLastBuiltDate();
		int ttl = getFeed().getGeneralContent().getTtl();
		int diff = (int) ((now.getTime() - feedDate.getTime()) / 1000);
		if ( diff > ttl )
			diff = ttl;
		return (new Random().nextInt(spreadFactor * ttl + 1) + (ttl - diff)) * 1000;

	}

	synchronized protected void updateRequestTimer() {

		feedRequestTask.cancel();
		feedRequestTask = new FeedRequestTask();
		feedRequestTimer.schedule(feedRequestTask, calculateInterval());

	}

	synchronized protected void requestFeed() {
		new RSSFeedRequestMessage(this, getRssServer(), params);
	}

	public RSSFeed getFeed() {
		return feed;
	}

	public void setFeed(RSSFeed feed) {
		this.feed = feed;
	}

	public synchronized void setDefaultColor() {
		getRssFeedRepresentation().represent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {

		super.update(o, arg);

		if ( o instanceof Peers.AddNotifier ) {
			if ( arg instanceof BrokerNode ) {
				register((BrokerNode) arg);
			}
		} else if ( o instanceof Peers.RemoveNotifier ) {
			if ( arg instanceof BrokerNode ) {
				unregister((BrokerNode) arg);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.PubSubType#register(rsspubsubframework.BrokerType)
	 */
	public void register(BrokerType broker) {
		new RegisterSubscriberMessage(this, (BrokerNode) broker, params.subntSzMsgRT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.PubSubType#unregister(rsspubsubframework.BrokerType)
	 */
	public void unregister(BrokerType broker) {
		// TODO Auto-generated method stub
		new UnregisterSubscriberMessage(this, (BrokerNode) broker, params.subntSzMsgRT);
	}
}
