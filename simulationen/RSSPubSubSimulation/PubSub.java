import rsspubsubframework.*;

import java.awt.*;
import java.util.*;

public class PubSub extends PubSubNode {

	protected class FeedRequestTask extends TimerTask {

		private PubSubNode timerpubsub = null;

		FeedRequestTask(PubSubNode timerpubsub) {
			this.timerpubsub = timerpubsub;
		}

		public void run() {
			new RequestFeedMessage(timerpubsub, timerpubsub);
			// requestFeed();
			// feedRequestTimer.schedule(new
			// FeedRequestTask(),calculateInterval());
		}

	}

	/**
	 * This message will be sent by the timer to the PubSubNode when it' time to
	 * request the next feed.
	 * 
	 * @author Friedemann Zintel
	 * 
	 */
	protected class RequestFeedMessage extends InternalMessage {

		RequestFeedMessage(Node src, Node dst) {
			super(src, dst);
		}

	}

	protected class UnregisterFromBrokerTaskMessage extends InternalMessage {

		private BrokerNode broker;

		UnregisterFromBrokerTaskMessage(Node src, Node dst, BrokerNode broker) {
			super(src, dst);
			this.broker = broker;
			// TODO Auto-generated constructor stub
		}

		/**
		 * @return Returns the broker.
		 */
		public BrokerNode getBroker() {
			return broker;
		}

	}

	protected class AckTimerMessage extends InternalMessage {

		private BrokerNode broker;

		public AckTimerMessage(Node src, Node dst, BrokerNode broker) {
			super(src, dst);
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
		private PubSubNode timernode;

		private BrokerNode broker;

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

	protected HashMap<BrokerNode, AckTimerTask> acktaskmap = new HashMap<BrokerNode, AckTimerTask>();

	protected FeedRequestTask feedRequestTask = new FeedRequestTask(this);

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

			handleRSSFeedMessage((RSSFeedMessage) m);

		} else if ( m instanceof RequestFeedMessage ) {

			handleRequestFeedMessage((RequestFeedMessage) m);

		} else if ( m instanceof NetworkSizeUpdateMessage ) {

			handleNetworkSizeUpdateMessage((NetworkSizeUpdateMessage) m);

		} else if ( m instanceof AckTimerMessage ) {

			handleAckTimerMessage((AckTimerMessage) m);

		} else if ( m instanceof RegisterAckMessage ) {

			handleRegisterAckMessage((RegisterAckMessage) m);

		} else if ( m instanceof UnregisterFromBrokerTaskMessage ) {

			handleUnregisterFromBrokerTaskMessage((UnregisterFromBrokerTaskMessage) m);

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
		// return (new Random().nextInt((spreadFactor) * ttl + 1) + (ttl -
		// diff)) * 1000;
		return (long) ((new Random().nextFloat() * (spreadFactor * ttl + 1) + (ttl - diff)) * 1000);

	}

	// for extensibility
	synchronized protected void updateRequestTimerByNewFeed() {
		updateRequestTimer(calculateInterval());
	}

	// for extensibility
	synchronized protected void updateRequestTimerByOldFeed() {
		updateRequestTimerByNewFeed();
	}

	synchronized protected void updateRequestTimer(long interval) {

		feedRequestTask.cancel();
		feedRequestTask = new FeedRequestTask(this);
		feedRequestTimer.schedule(feedRequestTask, interval);

	}

	protected void requestFeed() {
		new RSSFeedRequestMessage(this, getRssServer(), params);
	}

	protected void handleRSSFeedMessage(RSSFeedMessage fm) {

		// do only with new feeds
		if ( fm.getFeed().isNewerThan(getFeed()) ) {

			// show the feed
			setFeed(fm.getFeed());
			setRssFeedRepresentation(getRssFeedRepresentationFactory().newRSSFeedRepresentation(this, getFeed()));
			getRssFeedRepresentation().represent();

			updateRequestTimerByNewFeed();

			// send the feed to Broker, if we didn't get the message from
			// him
			// if (fm.getSrc() != getBroker()) {
			if ( brokerlist.contains(fm.getSrc()) == false ) {

				this.getStatistics().addServerFeed(this);

				for (BrokerNode broker : brokerlist)
					new RSSFeedMessage(this, broker, getFeed(), fm.getRssFeedRepresentation().copyWith(null, getFeed()), params);

			} else {

				// just statistics
				this.getStatistics().addBrokerFeed(this);
				// if we got the message from a broker, a request for RSSServer
				// will be omitted
				// -> statistics
				this.getStatistics().addOmittedRSSFeedRequest(this);

				// send it to all other brokers
				for (BrokerNode broker : brokerlist)
					if ( broker != fm.getSrc() )
						new RSSFeedMessage(this, broker, getFeed(), fm.getRssFeedRepresentation().copyWith(null, getFeed()), params);

			}

		} else {

			// got an old feed; update timer only if sender is RSSServer
			if ( fm.getSrc() == getRssServer() ) {
				updateRequestTimerByOldFeed();
			}
		}

	}

	protected void handleRequestFeedMessage(RequestFeedMessage rfm) {
		requestFeed();
	}

	protected void handleNetworkSizeUpdateMessage(NetworkSizeUpdateMessage nsum) {

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

	}

	protected void handleAckTimerMessage(AckTimerMessage atm) {

		// ERROR: this happens mysteriously
		if ( atm.getBroker() == null )
			System.err.println("PubSub.handleAckTimerMessage(): atm.getBroker() == null");

		new RegisterSubscriberMessage(this, atm.getBroker(), params.subnetParamMsgRT);

	}

	protected void handleRegisterAckMessage(RegisterAckMessage ram) {

		/*
		 * if (acktaskmap.containsKey((BrokerNode) ram.getSrc())) {
		 * acktaskmap.get((BrokerNode) ram.getSrc()).cancel();
		 * acktaskmap.remove((BrokerNode) ram.getSrc()); }
		 */
		brokerlist.add((BrokerNode) ram.getSrc());
	}

	protected void handleUnregisterFromBrokerTaskMessage(UnregisterFromBrokerTaskMessage ubm) {

		AckTimerTask ttask = acktaskmap.get(ubm.getBroker());
		if ( ttask != null ) {
			ttask.cancel();
			ackTimer.purge();
			acktaskmap.remove(ttask);
		}
		if ( brokerlist.contains(ubm.getBroker()) ) {
			new UnregisterSubscriberMessage(this, ubm.getBroker(), params.subnetParamMsgRT);
			brokerlist.remove(ubm.getBroker());
		}

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
				callbackRegisterAtBroker((BrokerNode) arg);
			}
		} else if ( o instanceof Peers.RemoveNotifier ) {
			if ( arg instanceof BrokerNode ) {
				callbackUnregisterFromBroker((BrokerNode) arg);
			}
		}
	}

	protected void removeConnection(PubSubNode node1, BrokerNode node2) {
		Engine.getSingleton().removeEdgeFromNodes(node1, node2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.PubSubType#register(rsspubsubframework.BrokerType)
	 */
	public void callbackRegisterAtBroker(BrokerType broker) {

		if ( broker == null )
			System.err.println("PubSub.callbackRegisterAtBroker(): broker == null");

		new RegisterSubscriberMessage(this, (BrokerNode) broker, params.subnetParamMsgRT);
		AckTimerTask task = new AckTimerTask(this, (BrokerNode) broker);
		ackTimer.schedule(task, params.pingTimeoutFactor * params.pingTimer, params.pingTimeoutFactor * params.pingTimer);
		acktaskmap.put((BrokerNode) broker, task);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.PubSubType#unregister(rsspubsubframework.BrokerType)
	 */
	public void callbackUnregisterFromBroker(BrokerType broker) {
		new UnregisterFromBrokerTaskMessage(this, this, (BrokerNode) broker);
		try {
			removeConnection(this, (BrokerNode) broker);
		} catch (ConcurrentModificationException e) {
			System.err.println("PubSub.callbackUnregisterFromBroker(): " + e);
		}
	}
}
