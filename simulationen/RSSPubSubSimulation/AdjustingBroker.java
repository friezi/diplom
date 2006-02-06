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

		private PingTimeoutTask pingtimeouttask = null;

		public SubnetSettings() {
		}

		public SubnetSettings(long size, PingTimeoutTask pingtimeouttask) throws InterruptedException {
			subParams.setSize(size);
			this.pingtimeouttask = pingtimeouttask;
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
		 * @return Returns the pingtimeouttask.
		 */
		protected PingTimeoutTask getPingtimeouttask() {
			return pingtimeouttask;
		}

		/**
		 * @param pingtimeouttask
		 *            The pingtimeouttask to set.
		 */
		protected void setPingtimeouttask(PingTimeoutTask pingtimeouttask) {
			this.pingtimeouttask = pingtimeouttask;
		}

	}

	/**
	 * This message is sent by a timer and indicates that the brokers have to be
	 * informed about number of online subscribers. It's not displayed on
	 * screen.
	 */

	/**
	 * @author friezi
	 * 
	 */
	public class InformBrokersMessage extends Message {

		public InformBrokersMessage(Node src, Node dst) {
			super(src, dst, 1);
		}

	}

	/**
	 * This message is sent by a timer and indicates that the subscribers have
	 * to informed about change of network-size. It's not displayed on screen.
	 */

	/**
	 * @author friezi
	 * 
	 */
	public class InformSubscribersMessage extends Message {

		public InformSubscribersMessage(Node src, Node dst) {
			super(src, dst, 1);
		}

	}

	/**
	 * This message is sent by a timer and indicates that its time to ping all
	 * neighbourbrokers
	 * 
	 */

	/**
	 * @author friezi
	 * 
	 */
	public class TimeForPingMessage extends Message {

		private AdjustingBroker.PingTask task;

		public TimeForPingMessage(Node src, Node dst, AdjustingBroker.PingTask task) {
			super(src, dst, 1);
			this.task = task;
		}

		/**
		 * @return Returns the task.
		 */
		protected AdjustingBroker.PingTask getTask() {
			return task;
		}

	}

	/**
	 * This message will be receivec from a timer, if an expected ping timed
	 * out.
	 */

	/**
	 * @author friezi
	 * 
	 */
	public class PingTimeoutMessage extends Message {

		private AdjustingBroker.PingTimeoutTask task;

		private BrokerNode broker;

		public PingTimeoutMessage(Node src, Node dst, BrokerNode broker, AdjustingBroker.PingTimeoutTask task) {
			super(src, dst, 1);
			this.broker = broker;
			this.task = task;
		}

		/**
		 * @return Returns the task.
		 */
		protected AdjustingBroker.PingTimeoutTask getTask() {
			return task;
		}

		/**
		 * @return Returns the broker.
		 */
		protected BrokerNode getBroker() {
			return broker;
		}

	}

	class PingTask extends TimerTask {

		// the broker we belong to
		BrokerNode timerbroker = null;

		public PingTask(BrokerNode timerbroker) {
			this.timerbroker = timerbroker;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			new TimeForPingMessage(timerbroker, timerbroker, this);
		}

	}

	class PingTimeoutTask extends TimerTask {

		// the broker we're expecting a ping from
		BrokerNode broker = null;

		// the broker we belong to
		BrokerNode timerbroker = null;

		public PingTimeoutTask(BrokerNode timerbroker, BrokerNode broker) {
			this.timerbroker = timerbroker;
			this.broker = broker;
		}

		public void run() {
			// processBrokerVanished(broker);
			new PingTimeoutMessage(timerbroker, timerbroker, broker, this);
		}
	}

	protected class InformBrokersTask extends TimerTask {

		// the broker we belong to
		BrokerNode timerbroker = null;

		public InformBrokersTask(BrokerNode timerbroker) {
			this.timerbroker = timerbroker;
		}

		public void run() {
			// processSubscribersChanged();
			new InformBrokersMessage(timerbroker, timerbroker);
		}
	}

	protected class InformSubscribersTask extends TimerTask {

		// the broker we belong to
		BrokerNode timerbroker = null;

		public InformSubscribersTask(BrokerNode timerbroker) {
			this.timerbroker = timerbroker;
		}

		public void run() {
			new InformSubscribersMessage(timerbroker, timerbroker);
		}
	}

	// a dummy-feed to start with and prevent NullPointerException
	private RSSFeed feed = new RSSFeed(new RSSFeedGeneralContent());

	// subnets and its settings
	private HashMap<BrokerNode, SubnetSettings> subnets = new HashMap<BrokerNode, SubnetSettings>();

	private Timer pingtimer = new Timer();

	private PingTask pingtask = null;

	private InformBrokersTask informbrokerstask = null;

	private Timer pingTimeoutTimer = new Timer();

	private Timer changetimer = new Timer();

	private long netsize = 0;

	private long oldnetsize = 0;

	private int nmbOnlineSubscribers = 0;

	private HashSet<BrokerNode> connectedBrokers = new HashSet<BrokerNode>();

	private boolean collectingSubscrInfo = false;

	private boolean collectingNetworkInfo = false;

	public AdjustingBroker(int xp, int yp, SimParameters params) {
		super(xp, yp, params);
		// TODO Auto-generated constructor stub
	}

	public void init() {

		newPingTimer();

	}

	public void receiveMessage(Message m) {

		// process only if not blocked
		if ( isBlocked() == true )
			return;

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

			if ( getSubnets().containsKey((BrokerNode) ssm.getSrc()) ) {
				SubnetSettings subnetsettings = getSubnets().get(ssm.getSrc());
				//
				// /**
				// ***********************************************************
				// */
				//				
				// PingTimeoutTask oldtask =
				// subnetsettings.getPingtimeouttask();
				//				
				// // set timer
				// subnetsettings.setPingtimeouttask(updatePingTimeoutTimer(oldtask,
				// (BrokerNode) ssm.getSrc()));
				// /**
				// ***********************************************************
				// */

				// only on change do something more
				if ( ssm.getSubParams().getSize() != subnetsettings.getSubParams().getSize() ) {
					// add difference to netsize
					adjustNetsize(ssm.getSubParams().getSize() - subnetsettings.getSubParams().getSize());
					// set new size
					subnetsettings.getSubParams().setSize(ssm.getSubParams().getSize());
					// send new size to all other neighbourbrokers
					informAllBut(ssm.getSrc());
				}

			}

		} else if ( m instanceof RegisterBrokerMessage ) {

			RegisterBrokerMessage rbm = (RegisterBrokerMessage) m;

			addToBrokers((BrokerNode) rbm.getSrc());

			// check if Broker is already/still registered (due to an undetected
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
			PingTimeoutTask oldtask = subnetsettings.getPingtimeouttask();

			// set size of subnet
			subnetsettings.getSubParams().setSize(rbm.getSubParams().getSize());

			// adjust the total network-size
			adjustNetsize(rbm.getSubParams().getSize() - oldsubnetsize);

			// send acknowledgement ( a bit faster)
			new RegisterAckMessage(this, rbm.getSrc(), params.subnetParamMsgRT / 2);

			// send subnetsize to broker
			sendSubnetSize((BrokerNode) rbm.getSrc());

			// set timer
			subnetsettings.setPingtimeouttask(updatePingTimeoutTimer(oldtask, (BrokerNode) rbm.getSrc()));

			// only on change tell the others
			if ( rbm.getSubParams().getSize() != oldsubnetsize )
				informAllBut(rbm.getSrc());

		} else if ( m instanceof RegisterAckMessage ) {

			BrokerNode broker = (BrokerNode) m.getSrc();

			addToBrokers(broker);
			// we must add an entry for this broker if it's not yet
			// contained:
			// on a one-side connection-built-up, we won't get a
			// RegisterBrokerMessage where an entry will be added as well.
			// in the other case we MUST NOT add an entry, because the old
			// value, which we need later, would be overridden!
			if ( getSubnets().containsKey(broker) == false )
				getSubnets().put(broker, new SubnetSettings());

			// set a timer
			PingTimeoutTask oldtask = getSubnets().get(broker).getPingtimeouttask();
			getSubnets().get(broker).setPingtimeouttask(updatePingTimeoutTimer(oldtask, broker));

		} else if ( m instanceof UnregisterBrokerMessage ) {

			// only if we haven't disconnected as well
			if ( getBrokers().contains((BrokerNode) m.getSrc()) )
				processBrokerDisconnected((BrokerNode) m.getSrc());

		} else if ( m instanceof RegisterSubscriberMessage ) {

			RegisterSubscriberMessage rsm = (RegisterSubscriberMessage) m;

			handleNewSubscriber((PubSubNode) rsm.getSrc());

		} else if ( m instanceof UnregisterSubscriberMessage ) {

			UnregisterSubscriberMessage usm = (UnregisterSubscriberMessage) m;

			handleUnregisteredSubscriber((PubSubNode) usm.getSrc());

		} else if ( m instanceof InformBrokersMessage ) {

			processInformBrokers();

		} else if ( m instanceof InformSubscribersMessage ) {

			processInformSubscribers();

		} else if ( m instanceof TimeForPingMessage ) {

			TimeForPingMessage tpm = (TimeForPingMessage) m;

			// if for any reason the amount of the stored network-size
			// is not corresponding to the sum of the subnet-size (...)
			// we do a recalculation:

			adjustNetsize();

			// in case of a change to the former value, we have to inform
			// the subscribers

			doInformSubscriberTimer();

			// imagine: just before the timeforping-message is put in the
			// message-queue an update of network-size is sent to all
			// neighbour-brokers. In this case a new pingtimer is already
			// set up and we can forget the message of the old timer
			// THIS WON'T HAPPEN IN THE CURRENT SCENARIO
			if ( tpm.getTask() == pingtask ) {

				ping();

				// newPingTimer();

			}

		} else if ( m instanceof PingMessage ) {

			PingMessage pm = (PingMessage) m;

			if ( getSubnets().containsKey(pm.getSrc()) ) {
				// just forsafety

				SubnetSettings subnetsettings = getSubnets().get(pm.getSrc());

				PingTimeoutTask oldtask = subnetsettings.getPingtimeouttask();

				// set timer
				subnetsettings.setPingtimeouttask(updatePingTimeoutTimer(oldtask, (BrokerNode) pm.getSrc()));

				// same processing as for SubnetParamMessage:

				// only on change do something more
				if ( pm.getSubParams().getSize() != subnetsettings.getSubParams().getSize() ) {

					// add difference to netsize
					adjustNetsize(pm.getSubParams().getSize() - subnetsettings.getSubParams().getSize());
					// set new size
					subnetsettings.getSubParams().setSize(pm.getSubParams().getSize());
					// send new size to all other neighbourbrokers
					informAllBut(pm.getSrc());

				}

			}

		} else if ( m instanceof PingTimeoutMessage ) {

			PingTimeoutMessage ptm = (PingTimeoutMessage) m;

			// imagine: just before the pingtimeout-message is put in the
			// message-queue the expected ping arrives.
			// The broker will be removed although
			// we got his ping. To prevent this, we check if the task which sent
			// the pingtimeout-message is the "uptodate"-task.

			// if we have an entry for the broker and the included task is not
			// the task
			// which send the message stop processing
			if ( getSubnets().containsKey((BrokerNode) ptm.getSrc()) ) {
				if ( getSubnets().get(ptm.getSrc()).pingtimeouttask == ptm.getTask() )
					return;
			}

			// everything normal -> do the processing
			// processBrokerDisconnected(ptm.getBroker());
			processBrokerVanished(ptm.getBroker());
		}
	}

	/**
	 * calculates the netsize by adding all subnetsizes and number of local
	 * subscribers
	 */
	synchronized protected void adjustNetsize() {

		// store old netsize
		if ( isCollectingNetworkInfo() == false )
			oldnetsize = getNetsize();

		setNetsize(calcNetSize());

	}

	/**
	 * @param delta
	 *            the delta-value which should be added to netsize
	 */
	synchronized protected void adjustNetsize(long delta) {

		// store old netsize
		if ( isCollectingNetworkInfo() == false )
			oldnetsize = getNetsize();

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

		// in case that (due to node-blocking) the number of online
		// subscribers could not be set, we set it here. Interference
		// with general functionality is only avoided if we do the following
		// check. Only in that case both values must be same.
		if ( isCollectingSubscrInfo() == false )
			setNmbOnlineSubscribers(getSubscribersSize());

		// IMPORTANT DETAIL!!!
		// we only add the numbers of online-subscribers!
		// this value is set only, when timeout for collecting the subscribers
		// occurs (in the case or above)!!!
		subnetsize += getNmbOnlineSubscribers();

		return subnetsize;

	}

	/**
	 * sends the size of a subnetwork to a broker
	 * 
	 * @param broker
	 *            the broker
	 */
	protected void sendSubnetSize(BrokerNode broker) {

		new SubnetParamMessage(this, broker, new SubnetParameters(calcNetSizeWithout(broker), new Date()),
				params);

	}

	synchronized protected PingTimeoutTask updatePingTimeoutTimer(PingTimeoutTask pingtimeouttask,
			BrokerNode broker) {

		if ( pingtimeouttask != null )
			pingtimeouttask.cancel();
		pingtimeouttask = new PingTimeoutTask(this, broker);
		pingTimeoutTimer.schedule(pingtimeouttask, params.pingTimeoutFactor * params.pingTimer);
		return pingtimeouttask;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BrokerNode#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {

		super.update(o, arg);

		if ( o instanceof Peers.AddNotifier ) {

			if ( arg instanceof BrokerNode ) {

				register((BrokerNode) arg);

			}

		} else if ( o instanceof Peers.RemoveNotifier ) {

			if ( arg instanceof BrokerNode )
				unregister((BrokerNode) arg);
			// removeFromBrokers((BrokerNode) arg);
		}

	}

	protected void registerAtBroker(BrokerNode broker) {

		// send a RegisterBrokerMessage
		new RegisterBrokerMessage(this, broker, new SubnetParameters(calcNetSizeWithout(broker), new Date()),
				params);

	}

	protected void unregisterFromBroker(BrokerNode broker) {

		processBrokerDisconnected(broker);

		// send a UnegisterBrokerMessage
		new UnregisterBrokerMessage(this, broker, params.subnetParamMsgRT);

	}

	protected void handleNewSubscriber(PubSubNode subscriber) {

		if ( getSubscribers().contains(subscriber) == false ) {
			addToSubscribers(subscriber);

			// send acknowledgement
			new RegisterAckMessage(this, subscriber, params.subnetParamMsgRT);

			adjustNetsize(1);

			// wait an amount of time before informing the other brokers
			// must be a repeated task: if the node gets blocked, this operation
			// must be again triggered
			if ( isCollectingSubscrInfo() == false ) {
				informbrokerstask = new InformBrokersTask(this);
				changetimer.schedule(informbrokerstask, params.informBrokersTimeout,
						params.informBrokersTimeout);
				setCollectingSubscrInfo(true);
			}
		}

	}

	protected void handleUnregisteredSubscriber(PubSubNode subscriber) {

		if ( getSubscribers().contains(subscriber) == true ) {
			removeFromSubscribers(subscriber);

			adjustNetsize(-1);

			// wait an amount of time before informing the other brokers
			// must be a repeated task: if the node gets blocked, this operation
			// must be again triggered
			if ( isCollectingSubscrInfo() == false ) {
				informbrokerstask = new InformBrokersTask(this);
				changetimer.schedule(informbrokerstask, params.informBrokersTimeout,
						params.informBrokersTimeout);
				setCollectingSubscrInfo(true);
			}
		}

	}

	//	
	// protected void informBrokers(){
	//
	// Set<BrokerNode> currbrokers = getBrokers();
	// for ( BrokerNode broker : currbrokers ) {
	// sendSubnetSize(broker);
	// }
	//		
	// }

	protected void informSubscribers() {

		// inform also the subscribers
		Set<PubSubNode> currsubscribers = getSubscribers();
		for ( PubSubNode subscriber : currsubscribers )
			new NetworkSizeUpdateMessage(this, subscriber, getNetsize(), params);

	}

	protected void informAllBut(Node exclbroker) {

		Set<BrokerNode> currbrokers = getBrokers();
		for ( BrokerNode otherbroker : currbrokers ) {
			if ( otherbroker != exclbroker )
				sendSubnetSize(otherbroker);
		}

		doInformSubscriberTimer();

	}

	/**
	 * a timertask for informing the subscribers will be set up if not already
	 * running.
	 */
	protected void doInformSubscriberTimer() {

		// wait an amount of time before informing the other brokers
		if ( isCollectingNetworkInfo() == false ) {
			changetimer.schedule(new InformSubscribersTask(this), params.informSubscribersTimeout);
			setCollectingNetworkInfo(true);
		}

	}

	protected void informAll() {
		informAllBut(null);
	}

	/**
	 * When timeout occurs, we update number of online subscribers and inform
	 * all other brokers
	 */
	protected void processInformBrokers() {

		// neighbours should only be informed on change
		// if number of online-subscribers stays same,
		// no update is necessary

		int oldnmbonlinesubscribers = getNmbOnlineSubscribers();
		setNmbOnlineSubscribers(getSubscribersSize());
		if ( oldnmbonlinesubscribers != getNmbOnlineSubscribers() )
			informAll();

		// stop the triggering task
		// ALL THIS IS DUE TO A VERY SUBTLE BUG WHICH ANNOYED ME QUITE MUCH
		informbrokerstask.cancel();
		setCollectingSubscrInfo(false);

	}

	protected void processInformSubscribers() {

		if ( oldnetsize != getNetsize() )
			informSubscribers();
		setCollectingNetworkInfo(false);

	}

	/**
	 * It's executed if the broker unregistered
	 * 
	 * @param broker
	 *            the broker
	 */
	protected void processBrokerDisconnected(BrokerNode broker) {

		long subnetsize = 0;

		// adjust network.size
		if ( getSubnets().containsKey(broker) )
			subnetsize = getSubnets().get(broker).getSubParams().getSize();
		adjustNetsize((-1) * subnetsize);

		// remove settings for broker
		getSubnets().remove(broker);

		// remove broker from brokerlist
		removeFromBrokers(broker);

		// inform others on change
		if ( subnetsize != 0 )
			informAllBut(broker);

	}

	/**
	 * It's executed if we get no ping from broker
	 * 
	 * @param broker
	 *            the broker
	 */
	protected void processBrokerVanished(BrokerNode broker) {

		long subnetsize = 0;

		// maybe he just vanished for a period of time,
		// so keep on maintaining him, but set subnetsize to zero

		// adjust network.size
		if ( getSubnets().containsKey(broker) ) {
			subnetsize = getSubnets().get(broker).getSubParams().getSize();
			getSubnets().get(broker).getSubParams().setSize(0);
		}
		adjustNetsize((-1) * subnetsize);

		// inform others on change
		if ( subnetsize != 0 )
			informAllBut(broker);

	}

	protected void ping() {

		// informBrokers();

		Set<BrokerNode> currbrokers = getBrokers();
		for ( BrokerNode broker : currbrokers ) {
			new PingMessage(this, broker, new SubnetParameters(calcNetSizeWithout(broker), new Date()),
					params);
		}
	}

	protected void newPingTimer() {

		pingtask = new PingTask(this);
		pingtimer.schedule(pingtask, params.pingTimer, params.pingTimer);

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
	 * @return Returns the collectingNetworkInfo.
	 */
	protected synchronized boolean isCollectingNetworkInfo() {
		return collectingNetworkInfo;
	}

	/**
	 * @param collectingNetworkInfo
	 *            The collectingNetworkInfo to set.
	 */
	protected synchronized void setCollectingNetworkInfo(boolean collectingNetworkInfo) {
		this.collectingNetworkInfo = collectingNetworkInfo;
	}

	/**
	 * @return Returns the nmbOnlineSubscribers.
	 */
	protected synchronized int getNmbOnlineSubscribers() {
		return nmbOnlineSubscribers;
	}

	/**
	 * @param nmbSubscribers
	 *            The nmbSubscribers to set.
	 */
	protected synchronized void setNmbOnlineSubscribers(int nmbSubscribers) {
		this.nmbOnlineSubscribers = nmbSubscribers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BrokerNode#register(rsspubsubframework.BrokerType)
	 */
	@Override
	public void register(BrokerType broker) {
		// TODO Auto-generated method stub
		super.register(broker);
		registerAtBroker((BrokerNode) broker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BrokerNode#unregister(rsspubsubframework.BrokerType)
	 */
	@Override
	public void unregister(BrokerType broker) {
		// TODO Auto-generated method stub
		super.unregister(broker);
		unregisterFromBroker((BrokerNode) broker);
	}

}
