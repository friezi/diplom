import rsspubsubframework.*;
import java.util.*;

/**
 * A Broker which can determine the size of the network
 */

/**
 * @author friezi
 * 
 */
public class AdjustingBroker extends BrokerNode {

	protected class SubnetSettings {

		private SubnetParameters subParams = new SubnetParameters();

		private PingTask pingtask = null;

		public SubnetSettings() {
		}

		public SubnetSettings(long size) throws InterruptedException {
			subParams.setSize(size);
		}

		/**
		 * @return Returns the subParams.
		 */
		public SubnetParameters getSubParams() {
			return subParams;
		}

		/**
		 * @param subParams
		 *            The subParams to set.
		 */
		public void setSubParams(SubnetParameters subParams) {
			this.subParams = subParams;
		}

		/**
		 * @return Returns the pingtask.
		 */
		protected PingTask getPingtask() {
			return pingtask;
		}

		/**
		 * @param pingtask
		 *            The pingtask to set.
		 */
		protected void setPingtask(PingTask pingtask) {
			this.pingtask = pingtask;
		}

	}

	protected class PingTask extends TimerTask {

		BrokerNode broker = null;

		public PingTask(BrokerNode broker) {
			this.broker = broker;
		}

		public void run() {

		}
	}

	protected class SubscribersChangedTask extends TimerTask {
		public void run() {
			processSubscribersChanged();
		}
	}

	// a dummy-feed to start with and prevent NullPointerException
	private RSSFeed feed = new RSSFeed(new RSSFeedGeneralContent());

	// subnets and its settings
	private HashMap<BrokerNode, SubnetSettings> subnets = new HashMap<BrokerNode, SubnetSettings>();

	private Timer pingTimer = new Timer();

	private Timer changeTimer = new Timer();

	private long netsize = 0;

	private int nmbOnlineSubscribers = 0;

	private boolean collectingSubscrInfo = false;

	public AdjustingBroker(int xp, int yp, SimParameters params) {
		super(xp, yp, params);
		// TODO Auto-generated constructor stub
	}

	public void init() {
		//
		// super.init();
		//
		// Set<Node> peers = getPeers();
		//
		// // determine neighbourbrokers
		// for ( BrokerNode broker : brokers )
		// getSubnets().put((BrokerNode) broker, new SubnetSettings());
		//
		// // determine subscribers
		// nmbOnlineSubscribers = subscribers.size();
		//
		// adjustNetsize();
		//
		// // tell all other neighbourbrokers about subnetsizes
		// for ( BrokerNode broker : brokers )
		// sendSubnetSize(broker);

	}

	public void receiveMessage(Message m) {

		if ( m instanceof RSSFeedMessage ) {

			RSSFeedMessage fm = (RSSFeedMessage) m;

			// if message-feed is newer than our one, set it and send it to all
			// other peers
			if ( fm.getFeed().isNewerThan(getFeed()) ) {

				setFeed(fm.getFeed());

				// send a new RSSFeedMessage to all other Brokers and
				// Subscribers
				Set<Node> peers = getPeers();
				for ( Node peer : peers ) {
					if ( peer != fm.getSrc() )
						new RSSFeedMessage(this, peer, getFeed(), fm.getRssFeedRepresentation().copyWith(
								null, getFeed()), params);
				}

			}

		} else if ( m instanceof SubnetParamMessage ) {

			SubnetParamMessage ssm = (SubnetParamMessage) m;
			SubnetSettings subnetsettings = getSubnets().get(ssm.getSrc());

			if ( ssm.getSubParams().getSize() != subnetsettings.getSubParams().getSize() ) {
				// add difference to netsize
				adjustNetsize(ssm.getSubParams().getSize() - subnetsettings.getSubParams().getSize());
				// set new size
				subnetsettings.getSubParams().setSize(ssm.getSubParams().getSize());
				// send new size to all other neighbourbrokers
				informOtherBrokers(ssm.getSrc());
			}

		} else if ( m instanceof RegisterBrokerMessage ) {

			RegisterBrokerMessage rbm = (RegisterBrokerMessage) m;

			// check if Broker is already registered (due to an undetected
			// disconnection). If broker is registered we have still the old
			// information about
			// the networksize, thus we must only add the difference of new and
			// old subnetsize to
			// networksize. if broker is not yet registered we add him and set
			// its size to
			// zero for later adapting it.
			if ( getSubnets().containsKey(rbm.getSrc()) == false )
				getSubnets().put((BrokerNode) rbm.getSrc(), new SubnetSettings());

			SubnetSettings subnetsettings = getSubnets().get(rbm.getSrc());

			long oldsubnetsize = subnetsettings.getSubParams().getSize();

			// set size of subnet
			subnetsettings.getSubParams().setSize(rbm.getSubParams().getSize());

			adjustNetsize(rbm.getSubParams().getSize() - oldsubnetsize);

			// only on change tell the others
			if ( rbm.getSubParams().getSize() != oldsubnetsize )
				informOtherBrokers(rbm.getSrc());

		} else if ( m instanceof RegisterSubscriberMessage ) {

			RegisterSubscriberMessage rsm = (RegisterSubscriberMessage) m;

			handleNewSubscriber((PubSubNode) rsm.getSrc());
		} else if ( m instanceof UnregisterSubscriberMessage ) {

			UnregisterSubscriberMessage usm = (UnregisterSubscriberMessage) m;

			handleUnregisteredSubscriber((PubSubNode) usm.getSrc());
		}
	}

	/**
	 * calculates the netsize by adding all subnetsizes and number of local
	 * subscribers
	 */
	synchronized protected void adjustNetsize() {
		setNetsize(calcNetSize());
	}

	/**
	 * @param delta
	 *            the delta-value which should be added to netsize
	 */
	synchronized protected void adjustNetsize(long delta) {
		setNetsize(getNetsize() + delta);
	}

	protected long calcNetSize() {
		return calcNetSizeWithout(null);
	}

	protected long calcNetSizeWithout(BrokerNode broker) {

		long subnetsize = 0;

		Set<BrokerNode> currbrokers = getBrokers();
		for ( BrokerNode otherbroker : currbrokers )
			if ( otherbroker != broker )
				subnetsize += getSubnets().get(otherbroker).getSubParams().getSize();
		// IMPORTANT DETAIL!!!
		// we only add the numbers of online-subscribers!
		// this value is set only, when timeout for collecting the subscribers
		// occurs!!!
		subnetsize += getNmbOnlineSubscribers();

		return subnetsize;

	}

	protected void sendSubnetSize(BrokerNode broker) {

		new SubnetParamMessage(this, broker, new SubnetParameters(calcNetSizeWithout(broker), new Date()),
				params);

	}

	synchronized protected PingTask updatePingTimer(PingTask pingtask, BrokerNode broker) {

		if ( pingtask != null )
			pingtask.cancel();
		pingtask = new PingTask(broker);
		pingTimer.schedule(pingtask, params.pingTimeout);
		return pingtask;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BrokerNode#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {

		if ( o instanceof Peers.AddNotifier ) {

			if ( arg instanceof BrokerNode ) {
				addToBrokers((BrokerNode) arg);
				// we must add an entry for this broker if it's not yet
				// contained:
				// on a one-side connection-built-up, we won't get a
				// RegisterBrokerMessage, where an entry will be added as well.
				// in the other case we MUST NOT add an entry, because the old
				// value, which we need later, would be overridden!
				if ( getSubnets().containsKey((BrokerNode) arg) == false )
					getSubnets().put((BrokerNode) arg, new SubnetSettings());
				// register broker at network
				registerNewBroker((BrokerNode) arg);
			}
			//
			// else if ( arg instanceof PubSubNode ) {
			// addToSubscribers((PubSubNode) arg);
			// // adjustNetsize(1);
			// }

		} else if ( o instanceof Peers.RemoveNotifier ) {

			if ( arg instanceof BrokerNode )
				removeFromBrokers((BrokerNode) arg);
			// else if ( arg instanceof PubSubNode )
			// removeFromSubscribers((PubSubNode) arg);
		}

	}

	protected void registerNewBroker(BrokerNode broker) {

		// send a RegisterBrokerMessage
		new RegisterBrokerMessage(this, broker, new SubnetParameters(calcNetSizeWithout(broker), new Date()),
				params);

	}

	protected void handleNewSubscriber(PubSubNode subscriber) {

		if ( getSubscribers().contains(subscriber) == false ) {
			addToSubscribers(subscriber);
			adjustNetsize(1);

			// wait an amount of time before informing the other brokers
			if ( isCollectingSubscrInfo() == false ) {
				changeTimer.schedule(new SubscribersChangedTask(), params.subscrChgTimeout);
			}
		}

	}

	protected void handleUnregisteredSubscriber(PubSubNode subscriber) {

		if ( getSubscribers().contains(subscriber) == true ) {
			removeFromSubscribers(subscriber);
			adjustNetsize(-1);

			// wait an amount of time before informing the other brokers
			if ( isCollectingSubscrInfo() == false ) {
				changeTimer.schedule(new SubscribersChangedTask(), params.subscrChgTimeout);
			}
		}

	}

	protected void informOtherBrokers(Node exclbroker) {

		Set<BrokerNode> currbrokers = getBrokers();
		for ( BrokerNode otherbroker : currbrokers ) {
			if ( otherbroker != exclbroker )
				sendSubnetSize(otherbroker);
		}
	}

	protected void informBrokers() {
		informOtherBrokers(null);
	}

	/**
	 * When timeout occurs, we update number of online subscribers and inform
	 * all other brokers
	 */
	synchronized protected void processSubscribersChanged() {

		// neighbours should only be informed on change
		// if number of online-subscribers stays same, no update necessary

		int oldnmbonlinesubscribers = getNmbOnlineSubscribers();
		setNmbOnlineSubscribers(getSubscribers().size());
		if ( oldnmbonlinesubscribers != getNmbOnlineSubscribers() )
			informBrokers();
		setCollectingSubscrInfo(false);

	}

	protected String text() {
		return String.valueOf(getNetsize());
	}

	/**
	 * @return Returns the feed.
	 */
	public RSSFeed getFeed() {
		return feed;
	}

	/**
	 * @param feed
	 *            The feed to set.
	 */
	public void setFeed(RSSFeed feed) {
		this.feed = feed;
	}

	/**
	 * @return Returns the subnets.
	 */
	public HashMap<BrokerNode, SubnetSettings> getSubnets() {
		return subnets;
	}

	/**
	 * @return Returns the netsize.
	 */
	protected synchronized long getNetsize() {
		return netsize;
	}

	/**
	 * @param netsize
	 *            The netsize to set.
	 */
	protected synchronized void setNetsize(long netsize) {
		this.netsize = netsize;
	}

	/**
	 * @return Returns the collectingSubscrInfo.
	 */
	synchronized protected boolean isCollectingSubscrInfo() {
		return collectingSubscrInfo;
	}

	/**
	 * @param collectingSubscrInfo
	 *            The collectingSubscrInfo to set.
	 */
	synchronized protected void setCollectingSubscrInfo(boolean collectingSubscrInfo) {
		this.collectingSubscrInfo = collectingSubscrInfo;
	}

	/**
	 * @return Returns the nmbOnlineSubscribers.
	 */
	protected synchronized int getNmbOnlineSubscribers() {
		return nmbOnlineSubscribers;
	}

	/**
	 * @param nmbOnlineSubscribers
	 *            The nmbOnlineSubscribers to set.
	 */
	protected synchronized void setNmbOnlineSubscribers(int nmbSubscribers) {
		this.nmbOnlineSubscribers = nmbSubscribers;
	}

}
