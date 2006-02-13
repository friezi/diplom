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

	protected class AckTimerMessage extends Message {

		private BrokerNode broker;

		public AckTimerMessage(Node src, Node dst, BrokerNode broker) {
			super(src, dst, 1);
			this.broker = broker;
		}

		/**
		 * @return Returns the broker.
		 */
		protected synchronized BrokerNode getBroker() {
			return broker;
		}

	}

	protected class AckTimerTask extends TimerTask {

		// Ourself
		PubSubNode timernode;

		BrokerNode broker;

		public AckTimerTask(PubSubNode timernode, BrokerNode broker) {
			this.timernode = timernode;
			this.broker = broker;
		}

		public void run() {
			new AckTimerMessage(timernode, timernode, broker);
		}
	}

	// dummy feed to prevent NullPointerException
	protected RSSFeed feed = new RSSFeed(new RSSFeedGeneralContent());

	protected Timer feedRequestTimer = new Timer();

	protected Timer ackTimer = new Timer();

	HashMap<BrokerNode, AckTimerTask> acktaskmap = new HashMap<BrokerNode, AckTimerTask>();

	protected FeedRequestTask feedRequestTask = new FeedRequestTask();

	protected int spreadFactor = 1;

	protected int spreadDivisor;

	protected long networksize = 1;

	public PubSub(int xp, int yp, SimParameters params) {
		super(xp, yp, params);
		this.spreadDivisor = params.spreadDivisor;
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
		} else if ( m instanceof NetworkSizeUpdateMessage ) {

			NetworkSizeUpdateMessage nsum = (NetworkSizeUpdateMessage) m;

			long size = nsum.getSize();

			// to prevent malicious values
			if ( size <= 0 )
				size = 1;

			spreadFactor = (int) (size / spreadDivisor);

			long interval = calculateInterval();

			// if we have a very long scheduled timer for the timer-task and due
			// to
			// the new network-size the new interval would be much shorter
			// we should set up a new task, otherwise we can live with a short
			// task
			if ( interval < feedRequestTask.scheduledExecutionTime() - System.currentTimeMillis() )
				updateRequestTimer(interval);

		} else if ( m instanceof AckTimerMessage ) {

			new RegisterSubscriberMessage(this, ((AckTimerMessage) m).getBroker(), params.subnetParamMsgRT);

		} else if ( m instanceof RegisterAckMessage ) {

			if ( acktaskmap.containsKey((BrokerNode) m.getSrc()) ) {
				acktaskmap.get((BrokerNode) m.getSrc()).cancel();
				acktaskmap.remove((BrokerNode) m.getSrc());
			}

		}
	}

	protected long calculateInterval() {

		Date now = new Date();
		Date feedDate = getFeed().getGeneralContent().getLastBuiltDate();
		int ttl = getFeed().getGeneralContent().getTtl();
		int diff = (int) ((now.getTime() - feedDate.getTime()) / 1000);
		if ( diff > ttl )
			// diff = ttl;
			diff = 0;
//		return (new Random().nextInt((spreadFactor) * ttl + 1) + (ttl - diff)) * 1000;
		return (long)((new Random().nextFloat()*(spreadFactor * ttl + 1) + (ttl - diff)) * 1000);

	}

	synchronized protected void updateRequestTimer() {

		feedRequestTask.cancel();
		feedRequestTask = new FeedRequestTask();
		feedRequestTimer.schedule(feedRequestTask, calculateInterval());

	}

	synchronized protected void updateRequestTimer(long interval) {

		feedRequestTask.cancel();
		feedRequestTask = new FeedRequestTask();
		feedRequestTimer.schedule(feedRequestTask, interval);

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

		new RegisterSubscriberMessage(this, (BrokerNode) broker, params.subnetParamMsgRT);
		AckTimerTask task = new AckTimerTask(this, (BrokerNode) broker);
		ackTimer.schedule(task, params.pingTimeoutFactor * params.pingTimer, params.pingTimeoutFactor
				* params.pingTimer);
		acktaskmap.put((BrokerNode) broker, task);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.PubSubType#unregister(rsspubsubframework.BrokerType)
	 */
	public void unregister(BrokerType broker) {
		// TODO Auto-generated method stub
		new UnregisterSubscriberMessage(this, (BrokerNode) broker, params.subnetParamMsgRT);
	}
}
