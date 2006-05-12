import java.util.*;
import javax.swing.JLabel;

public class PubSub extends PubSubNode {

	protected class FeedRequestTask extends ExtendedTimerTask {

		private PubSubNode timerpubsub = null;

		FeedRequestTask(PubSubNode timerpubsub) {
			this.timerpubsub = timerpubsub;
		}

		public void run() {
			super.run();
			new RequestFeedMessage(timerpubsub, timerpubsub).send();
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

			// debugging
			// if ( broker == null )
			// System.err.println("PubSub.AckTimerMessage(): broker==null");

			this.broker = broker;
		}

		/**
		 * @return Returns the broker.
		 */
		protected synchronized BrokerNode getBroker() {
			return broker;
		}

	}

	protected class AckTimerTask extends ExtendedTimerTask {

		// Ourself
		private PubSubNode timernode;

		private BrokerNode broker;

		public AckTimerTask(PubSubNode timernode, BrokerNode broker) {

			// debugging
			// if ( broker == null )
			// System.err.println("PubSub.AckTimerTask(): broker==null");

			this.timernode = timernode;
			this.broker = broker;
		}

		public void run() {

			super.run();

			// debugging
			// if ( broker == null )
			// System.err.println("PubSub.AckTimerTask.run(): broker==null");

			new AckTimerMessage(timernode, timernode, broker).send();
		}
	}

	// dummy feed to prevent NullPointerException
	protected RSSFeed feed = new RSSFeed(new RSSFeedGeneralContent());

	protected ExtendedTimer feedRequestTimer = new ExtendedTimer();

	/**
	 * purge-counter for the feedRequestTimer
	 */
	protected int frtPurgeCounter = 0;

	/**
	 * the maximum value for the frtPurgeCounter
	 */
	protected static int MAXFRTPURGECOUNTER = 20;

	protected ExtendedTimer ackTimer = new ExtendedTimer();

	protected HashMap<BrokerNode, AckTimerTask> acktaskmap = new HashMap<BrokerNode, AckTimerTask>();

	protected FeedRequestTask feedRequestTask = new FeedRequestTask(this);

	protected int spreadFactor = 1;

	protected int spreadDivisor;

	protected long networksize = 1;

	protected InfoWindow infoWindow;

	protected boolean moreinfo = false;

	public PubSub(int xp, int yp, SimParameters params) {
		super(xp, yp, params);
		this.spreadDivisor = params.spreadDivisor;
	}

	public void init() {
		// wait a random time until broker has acknoledged our registration
		// requestFeed();
		// between 1 and 2 secs
		long starttime = (long) ((new Random().nextFloat() + 1) * 1000);
		feedRequestTask = new FeedRequestTask(this);
		feedRequestTimer.schedule(feedRequestTask, starttime, 1000);
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

	protected long calculateNextRequestTimeout() {

		Date now = new Date();
		Date feedDate = getFeed().getGeneralContent().getLastBuiltDate();
		int ttl = getFeed().getGeneralContent().getTtl();
		int diffsecs = (int) ((now.getTime() - feedDate.getTime()) / 1000);
		if ( diffsecs > ttl )
			// diff = ttl;
			diffsecs = 0;
		// return (new Random().nextInt((spreadFactor) * ttl + 1) + (ttl -
		// diff)) * 1000;
		return (long) ((new Random().nextFloat() * (spreadFactor * ttl + 1) + (ttl - diffsecs)) * 1000);

	}

	// for extensibility
	synchronized protected void updateRequestTimerByNewFeed() {
		updateRequestTimer(calculateNextRequestTimeout());
	}

	// for extensibility
	synchronized protected void updateRequestTimerByOldFeed() {
		updateRequestTimerByNewFeed();
	}

	// for extensibility
	synchronized protected void updateRequestTimerByNewFeedFromServer() {
		updateRequestTimerByNewFeed();
	}

	// for extensibility
	synchronized protected void updateRequestTimerByNewFeedFromBroker() {
		updateRequestTimerByNewFeed();
	}

	// for extensibility
	synchronized protected void updateRequestTimerByOldFeedFromServer() {
		updateRequestTimerByOldFeed();
	}

	// for extensibility
	synchronized protected void updateRequestTimerByOldFeedFromBroker() {
	}

	synchronized protected void updateRequestTimer(long interval) {

		feedRequestTask.cancel();
		purgeFeedRequestTimer();
		feedRequestTask = new FeedRequestTask(this);
		// if there's no response from server, we need to re-request
		feedRequestTimer.schedule(feedRequestTask, interval, getFeed().getGeneralContent().getTtl() * 1000);

	}

	protected void requestFeed() {
		new RSSFeedRequestMessage(this, getRssServer(), params).send();
	}

	protected void handleRSSFeedMessage(RSSFeedMessage fm) {

		// do only with new feeds
		if ( fm.getFeed().isNewerThan(getFeed()) ) {

			// show the feed
			setFeed(fm.getFeed());
			setRssFeedRepresentation(getRssFeedRepresentationFactory().newRSSFeedRepresentation(this, getFeed()));
			getRssFeedRepresentation().represent();

			// send the feed to Broker, if we didn't get the message from
			// him
			// if (fm.getSrc() != getBroker()) {
			if ( brokerlist.contains(fm.getSrc()) == false ) {

				updateRequestTimerByNewFeedFromServer();

				this.getStatistics().addServerFeed(this);

				for ( BrokerNode broker : brokerlist )
					new RSSFeedMessage(this, broker, getFeed(), fm.getRssFeedRepresentation().copyWith(null, getFeed()), params).send();

			} else {

				updateRequestTimerByNewFeedFromBroker();

				// just statistics
				this.getStatistics().addBrokerFeed(this);
				// if we got the message from a broker, a request for RSSServer
				// will be omitted
				// -> statistics
				this.getStatistics().addOmittedRSSFeedRequest(this);

				// send it to all other brokers
				for ( BrokerNode broker : brokerlist )
					if ( broker != fm.getSrc() )
						new RSSFeedMessage(this, broker, getFeed(), fm.getRssFeedRepresentation().copyWith(null, getFeed()), params).send();

			}

		} else {

			// got an old feed; update timer only if sender is RSSServer
			if ( fm.getSrc() == getRssServer() ) {
				updateRequestTimerByOldFeedFromServer();
			} else {
				updateRequestTimerByOldFeedFromBroker();
			}
		}

	}

	protected void handleRequestFeedMessage(RequestFeedMessage rfm) {
		requestFeed();
	}

	protected void handleNetworkSizeUpdateMessage(NetworkSizeUpdateMessage nsum) {

		long size = nsum.getSize();

		// to prevent malicious values
		if ( size < 1 )
			size = 1;

		spreadFactor = (int) (size / spreadDivisor);

		long interval = calculateNextRequestTimeout();

		// if we have a very long scheduled timer for the timer-task and due
		// to
		// the new network-size the new interval would be much shorter
		// we should set up a new task, otherwise we can live with a short
		// task
		if ( interval < feedRequestTask.getNextExecutionTime() - System.currentTimeMillis() )
			updateRequestTimer(interval);

	}

	protected void handleAckTimerMessage(AckTimerMessage atm) {

		// debugging: ERROR: this happens mysteriously
		// if ( atm.getBroker() == null )
		// System.err.println("PubSub.handleAckTimerMessage(): atm.getBroker()
		// == null");

		new RegisterSubscriberMessage(this, atm.getBroker(), params.subnetParamMsgRT).send();

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
			new UnregisterSubscriberMessage(this, ubm.getBroker(), params.subnetParamMsgRT).send();
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
		if ( getRssFeedRepresentation() != null )
			getRssFeedRepresentation().represent();
		else
			super.setDefaultColor();
	}

	/**
	 * @return Returns the moreinfo.
	 */
	public boolean isMoreinfo() {
		return moreinfo;
	}

	/**
	 * @param moreinfo
	 *            The moreinfo to set.
	 */
	public void setMoreinfo(boolean moreinfo) {
		this.moreinfo = moreinfo;
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

		// debugging
		// if ( broker == null )
		// System.err.println("PubSub.callbackRegisterAtBroker(): broker ==
		// null");

		new RegisterSubscriberMessage(this, (BrokerNode) broker, params.subnetParamMsgRT).send();
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
		new UnregisterFromBrokerTaskMessage(this, this, (BrokerNode) broker).send();
		try {
			removeConnection(this, (BrokerNode) broker);
		} catch ( ConcurrentModificationException e ) {
			System.err.println("PubSub.callbackUnregisterFromBroker(): " + e);
		}
	}

	protected void purgeFeedRequestTimer() {

		frtPurgeCounter++;

		if ( frtPurgeCounter >= MAXFRTPURGECOUNTER ) {

			feedRequestTimer.purge();
			frtPurgeCounter = 0;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSubNode#showInfo()
	 */
	@Override
	public void showInfo() {
		super.showInfo();
		(infoWindow = new InfoWindow("PubSub-Info", this, moreinfo)).setVisible(true);
	}

	/* (non-Javadoc)
	 * @see PubSubNode#showMoreInfo(MoreInfoWindow)
	 */
	@Override
	protected void showMoreInfo(InfoWindow moreinfowindow) {
		super.showMoreInfo(moreinfowindow);

	}
}
