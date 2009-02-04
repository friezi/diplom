package de.zintel.diplom.rps.pubsub;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;

import de.zintel.diplom.feed.RSSFeed;
import de.zintel.diplom.feed.RSSFeedGeneralContent;
import de.zintel.diplom.gui.InfoWindow;
import de.zintel.diplom.messages.BlockNodeMessage;
import de.zintel.diplom.messages.NetworkSizeUpdateMessage;
import de.zintel.diplom.messages.RSSFeedMessage;
import de.zintel.diplom.messages.RSSFeedRequestMessage;
import de.zintel.diplom.messages.RegisterAckMessage;
import de.zintel.diplom.messages.RegisterSubscriberMessage;
import de.zintel.diplom.messages.UnblockNodeMessage;
import de.zintel.diplom.messages.UnregisterSubscriberMessage;
import de.zintel.diplom.rps.broker.BrokerNode;
import de.zintel.diplom.rps.broker.BrokerType;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.InternalMessage;
import de.zintel.diplom.simulation.Message;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.synchronization.AbstractTimer;
import de.zintel.diplom.synchronization.ExtendedTimerTask;


public class PubSub extends PubSubNode {

	protected class FeedRequestTask extends ExtendedTimerTask {

		private PubSubNode timerpubsub = null;

		FeedRequestTask(PubSubNode timerpubsub, SimParameters params) {
			super(params);
			this.timerpubsub = timerpubsub;
		}

		@Override
		public void run() {
			super.run();
			setMessage(new RequestFeedMessage(timerpubsub, timerpubsub, getNextExecutionTime()).send());
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

		RequestFeedMessage(Node src, Node dst, long arrivalTime) {
			super(src, dst, arrivalTime);
		}

	}

	protected class UnregisterFromBrokerTaskMessage extends InternalMessage {

		private BrokerNode broker;

		UnregisterFromBrokerTaskMessage(Node src, Node dst, BrokerNode broker, long arrivalTime) {
			super(src, dst, arrivalTime);
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

		public AckTimerMessage(Node src, Node dst, BrokerNode broker, long arrivalTime) {
			super(src, dst, arrivalTime);

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

		public AckTimerTask(PubSubNode timernode, BrokerNode broker, SimParameters params) {
			super(params);

			// debugging
			// if ( broker == null )
			// System.err.println("PubSub.AckTimerTask(): broker==null");

			this.timernode = timernode;
			this.broker = broker;
		}

		@Override
		public void run() {

			super.run();

			// debugging
			// if ( broker == null )
			// System.err.println("PubSub.AckTimerTask.run(): broker==null");

			setMessage(new AckTimerMessage(timernode, timernode, broker, getNextExecutionTime()).send());
		}
	}

	// dummy feed to prevent NullPointerException
	protected RSSFeed feed = new RSSFeed(new RSSFeedGeneralContent());

	protected AbstractTimer feedRequestTimer;

	/**
	 * purge-counter for the feedRequestTimer
	 */
	protected int frtPurgeCounter = 0;

	/**
	 * the maximum value for the frtPurgeCounter
	 */
	protected static int MAXFRTPURGECOUNTER = 20;

	protected AbstractTimer ackTimer;

	protected HashMap<BrokerNode, AckTimerTask> acktaskmap = new HashMap<BrokerNode, AckTimerTask>();

	protected FeedRequestTask feedRequestTask;

	protected int spreadFactor = 1;

	protected int spreadDivisor;

	protected long networksize = 1;

	protected InfoWindow infoWindow;

	protected boolean moreinfo = false;

	public PubSub(int xp, int yp, SimParameters params) {
		super(xp, yp, params);
	}

	@Override
	public void init() {

		super.init();
		this.spreadDivisor = params.getSpreadDivisor();

		feedRequestTask = new FeedRequestTask(this, params);

		feedRequestTimer = getTimerfactory().newTimer();
		ackTimer = getTimerfactory().newTimer();

	}

	@Override
	public void reset() {

		networksize = 1;
		spreadFactor = 1;
		if ( feedRequestTask != null )
			feedRequestTask.cancel();
		purgeFeedRequestTimer();
		for ( AckTimerTask acktimertask : acktaskmap.values() )
			acktimertask.cancel();
		acktaskmap.clear();
		ackTimer.cancel();
		frtPurgeCounter = 0;
		feedRequestTimer.cancel();
		feed = new RSSFeed(new RSSFeedGeneralContent());
		super.reset();

	}

	@Override
	public void start() {

		startFeedRequestTimer();

	}

	/* (non-Javadoc)
	 * @see PubSubType#renew()
	 */
	public void renew() {

		LinkedList<BrokerType> pubsub_brokers = new LinkedList<BrokerType>(getBrokers());

		for ( BrokerType oldbroker : pubsub_brokers )
			unregisterFromBroker((BrokerNode) oldbroker);

		reset();
		init();

	}

	public void startFeedRequestTimer() {

		// wait a random time until broker has acknoledged our registration
		// requestFeed();
		// between 1 and 2 secs
		long starttime = (long) ((Engine.getSingleton().getRandom().nextFloat() + 1) * 1000);
		feedRequestTask = new FeedRequestTask(this, params);
		feedRequestTimer.schedule(feedRequestTask, starttime);

	}

	@Override
	protected void handleInternalMessage(InternalMessage m) {

		if ( m instanceof RequestFeedMessage ) {

			handleInternalMessageRequestFeedMessage((RequestFeedMessage) m);

		} else if ( m instanceof AckTimerMessage ) {

			handleInternalMessageAckTimerMessage((AckTimerMessage) m);

		} else if ( m instanceof BlockNodeMessage ) {

			handleInternalMessageBlockNodeMessage((BlockNodeMessage) m);

		} else if ( m instanceof UnblockNodeMessage ) {

			handleInternalMessageUnblockNodeMessage((UnblockNodeMessage) m);

		} else {

			handleStandardMessage(m);

		}

	}

	protected void handleInternalMessageRequestFeedMessage(RequestFeedMessage m) {

		handleStandardMessage(m);

		updateFeedRequestTimer(getFeedRequestTimerPeriod());

	}

	protected void handleInternalMessageAckTimerMessage(AckTimerMessage m) {

		handleStandardMessage(m);

		scheduleAckTimerTask(m.getBroker());

	}

	protected void handleInternalMessageBlockNodeMessage(BlockNodeMessage m) {
		this.block();
	}

	protected void handleInternalMessageUnblockNodeMessage(UnblockNodeMessage m) {
		this.unblock();
	}

	@Override
	protected void handleStandardMessage(Message m) {

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

	protected long calculateTimeToRefreshInterval() {

		Date now = new Date(getEngine().getTime());
		Date feedDate = getFeed().getGeneralContent().getLastBuiltDate();
		int ttl = getFeed().getGeneralContent().getTtl();
		int diffsecs = (int) ((now.getTime() - feedDate.getTime()) / 1000);
		if ( diffsecs > ttl )
			// diff = ttl;
			diffsecs = 0;
		// return (new Random().nextInt((spreadFactor) * ttl + 1) + (ttl -
		// diff)) * 1000;
		return (long) ((Engine.getSingleton().getRandom().nextFloat() * (spreadFactor * ttl + 1) + (ttl - diffsecs)) * 1000);

	}

	// for extensibility
	synchronized protected void updateFeedRequestTimerByNewFeed() {
		updateFeedRequestTimer(calculateTimeToRefreshInterval());
	}

	// for extensibility
	synchronized protected void updateFeedRequestTimerByOldFeed() {
		updateFeedRequestTimerByNewFeed();
	}

	// for extensibility
	synchronized protected void updateFeedRequestTimerByNewFeedFromServer() {
		updateFeedRequestTimerByNewFeed();
	}

	// for extensibility
	synchronized protected void updateFeedRequestTimerByNewFeedFromBroker() {
		updateFeedRequestTimerByNewFeed();
	}

	// for extensibility
	synchronized protected void updateFeedRequestTimerByOldFeedFromServer() {
		updateFeedRequestTimerByOldFeed();
	}

	// for extensibility
	synchronized protected void updateFeedRequestTimerByOldFeedFromBroker() {
	}

	synchronized protected void updateFeedRequestTimer(long interval) {

		if ( feedRequestTask != null ) {
			feedRequestTask.cancel();
		}
		purgeFeedRequestTimer();
		feedRequestTask = new FeedRequestTask(this, params);
		// if there's no response from server, we need to re-request
		feedRequestTimer.schedule(feedRequestTask, interval);

	}

	protected long getFeedRequestTimerPeriod() {
		return getFeed().getGeneralContent().getTtl() * 1000;
	}

	protected void requestFeed() {

		if ( getRssServer() != null )
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
			if ( brokerlist.contains(fm.getOrigin()) == false ) {

				updateFeedRequestTimerByNewFeedFromServer();

				this.getStatistics().addServerFeed(this);

				for ( BrokerNode broker : brokerlist )
					new RSSFeedMessage(this, broker, getFeed(), fm.getRssFeedRepresentation().copyWith(null, getFeed()), params).send();

			} else {

				updateFeedRequestTimerByNewFeedFromBroker();

				// just statistics
				this.getStatistics().addBrokerFeed(this);
				// if we got the message from a broker, a request for RSSServer
				// will be omitted
				// -> statistics
				this.getStatistics().addOmittedRSSFeedRequest(this);

				// send it to all other brokers
				for ( BrokerNode broker : brokerlist )
					if ( broker != fm.getOrigin() )
						new RSSFeedMessage(this, broker, getFeed(), fm.getRssFeedRepresentation().copyWith(null, getFeed()), params).send();

			}

		} else {

			// got an old feed; update timer only if sender is RSSServer
			if ( fm.getOrigin() == getRssServer() ) {
				updateFeedRequestTimerByOldFeedFromServer();
			} else {
				updateFeedRequestTimerByOldFeedFromBroker();
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

		long interval = calculateTimeToRefreshInterval();

		// if we have a very long scheduled timer for the timer-task and due
		// to
		// the new network-size the new interval would be much shorter
		// we should set up a new task, otherwise we can live with a short
		// task
		if ( interval < feedRequestTask.getNextExecutionTime() - getEngine().getTime() )
			updateFeedRequestTimer(interval);

	}

	protected void handleAckTimerMessage(AckTimerMessage atm) {

		// debugging: ERROR: this happens mysteriously
		// if ( atm.getBroker() == null )
		// System.err.println("PubSub.handleAckTimerMessage(): atm.getBroker()
		// == null");

		new RegisterSubscriberMessage(this, atm.getBroker(), params.getSubnetParamMsgRT()).send();

	}

	protected void handleRegisterAckMessage(RegisterAckMessage ram) {

		/*
		 * if (acktaskmap.containsKey((BrokerNode) ram.getSrc())) {
		 * acktaskmap.get((BrokerNode) ram.getSrc()).cancel();
		 * acktaskmap.remove((BrokerNode) ram.getSrc()); }
		 */
		brokerlist.add((BrokerNode) ram.getRegistrator());

		registering--;

		displayOverlayConnection(this, ram.getRegistrator());

	}

	protected void handleUnregisterFromBrokerTaskMessage(UnregisterFromBrokerTaskMessage ubm) {
		unregisterFromBroker(ubm.getBroker());
	}

	protected void unregisterFromBroker(BrokerNode broker) {

		AckTimerTask ttask = acktaskmap.get(broker);
		if ( ttask != null ) {

			ttask.cancel();
			ackTimer.purge();
			acktaskmap.remove(ttask);

		}
		if ( brokerlist.contains(broker) ) {

			new UnregisterSubscriberMessage(this, broker, params.getSubnetParamMsgRT()).send();
			brokerlist.remove(broker);
			undisplayOverlayConnection(this, broker);

		} else
			System.err.println("WARNING:  PubSub.unregisterFromBroker(): subscriber " + this + " has no connection to broker " + broker + "!");

	}

	public RSSFeed getFeed() {
		return feed;
	}

	public void setFeed(RSSFeed feed) {
		this.feed = feed;
	}

	@Override
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

	protected void scheduleAckTimerTask(BrokerNode broker) {

		AckTimerTask task = new AckTimerTask(this, (BrokerNode) broker, params);
		ackTimer.purge();
		ackTimer.schedule(task, params.getPingTimeoutFactor() * params.getPingTimer());
		acktaskmap.put(broker, task);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
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
		getEngine().removeEdgeFromNodes(node1, node2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSubType#callbackRegisterAtBroker(BrokerType)
	 */
	public void callbackRegisterAtBroker(BrokerType broker) {

		// debugging
		// if ( broker == null )
		// System.err.println("PubSub.callbackRegisterAtBroker(): broker ==
		// null");

		new RegisterSubscriberMessage(this, (BrokerNode) broker, params.getSubnetParamMsgRT()).send();

		registering++;

		scheduleAckTimerTask((BrokerNode) broker);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSubType#callbackUnregisterFromBroker(BrokerType)
	 */
	public void callbackUnregisterFromBroker(BrokerType broker) {

		new UnregisterFromBrokerTaskMessage(this, this, (BrokerNode) broker, getEngine().getTime()).send();

		// try {
		// removeConnection(this, (BrokerNode) broker);
		// } catch ( ConcurrentModificationException e ) {
		// System.err.println("PubSub.callbackUnregisterFromBroker(): " + e);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSubType#callbackBlock()
	 */
	public void callbackBlock(long delay) {
		new BlockNodeMessage(this, this, getEngine().getTime() + delay).send();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSubType#callbackUnblock()
	 */
	public void callbackUnblock(long delay) {
		new UnblockNodeMessage(this, this, getEngine().getTime() + delay).send();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSubType#callbackIsBlocked()
	 */
	public boolean callbackIsBlocked() {
		return this.isBlocked();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSubNode#showMoreInfo(MoreInfoWindow)
	 */
	@Override
	public void showMoreInfo(InfoWindow moreinfowindow) {
		super.showMoreInfo(moreinfowindow);

	}
}
