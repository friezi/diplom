import rsspubsubframework.*;

import java.util.*;

/**
 * A Broker which can determine the size of the network
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class AdjustingBroker extends BrokerNode {

	protected class SubnetSettings {

		protected SubnetParameters subParams = new SubnetParameters();

		protected PingTimeoutTask pingtimeouttask = null;

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
	 * @author Friedemann Zintel
	 * 
	 */
	public class InformBrokersMessage extends InternalMessage {

		public InformBrokersMessage(Node src, Node dst) {
			super(src, dst);
		}

	}

	/**
	 * This message is sent by a timer and indicates that the subscribers have
	 * to informed about change of network-size. It's not displayed on screen.
	 */

	/**
	 * @author Friedemann Zintel
	 * 
	 */
	public class InformSubscribersMessage extends InternalMessage {

		public InformSubscribersMessage(Node src, Node dst) {
			super(src, dst);
		}

	}

	/**
	 * This message is sent by a timer and indicates that its time to ping all
	 * neighbourbrokers
	 * 
	 */

	/**
	 * @author Friedemann Zintel
	 * 
	 */
	public class TimeForPingMessage extends InternalMessage {

		private AdjustingBroker.PingTask task;

		public TimeForPingMessage(Node src, Node dst, AdjustingBroker.PingTask task) {
			super(src, dst);
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
	 * This message will be received from a timer, if an expected ping timed
	 * out.
	 */

	/**
	 * @author Friedemann Zintel
	 * 
	 */
	public class PingTimeoutMessage extends InternalMessage {

		private AdjustingBroker.PingTimeoutTask task;

		private Node node;

		public PingTimeoutMessage(Node src, Node dst, Node node, AdjustingBroker.PingTimeoutTask task) {
			super(src, dst);
			this.node = node;
			this.task = task;
		}

		/**
		 * @return Returns the task.
		 */
		protected AdjustingBroker.PingTimeoutTask getTask() {
			return task;
		}

		/**
		 * @return Returns the node.
		 */
		protected Node getNode() {
			return node;
		}

	}

	class PingTask extends ExtendedTimerTask {

		// the node we belong to
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
			super.run();
			new TimeForPingMessage(timerbroker, timerbroker, this).send();
		}

	}

	class PingTimeoutTask extends ExtendedTimerTask {

		// the node we're expecting a ping from
		private Node node = null;

		// the node we belong to
		BrokerNode timerbroker = null;

		public PingTimeoutTask(BrokerNode timerbroker, Node node) {
			this.timerbroker = timerbroker;
			this.node = node;
		}

		public void run() {
			// processBrokerVanished(node);
			super.run();
			new PingTimeoutMessage(timerbroker, timerbroker, node, this).send();
		}
	}

	protected class InformBrokersTask extends ExtendedTimerTask {

		// the node we belong to
		BrokerNode timerbroker = null;

		public InformBrokersTask(BrokerNode timerbroker) {
			this.timerbroker = timerbroker;
		}

		public void run() {
			// processSubscribersChanged();
			super.run();
			new InformBrokersMessage(timerbroker, timerbroker).send();
		}
	}

	protected class InformSubscribersTask extends ExtendedTimerTask {

		// the node we belong to
		BrokerNode timerbroker = null;

		public InformSubscribersTask(BrokerNode timerbroker) {
			this.timerbroker = timerbroker;
		}

		public void run() {
			super.run();
			new InformSubscribersMessage(timerbroker, timerbroker).send();
		}
	}

	// a dummy-feed to start with and prevent NullPointerException
	protected RSSFeed feed = new RSSFeed(new RSSFeedGeneralContent());

	// subnets and its settings
	protected HashMap<BrokerNode, SubnetSettings> subnets = new HashMap<BrokerNode, SubnetSettings>();

	protected HashMap<PubSubNode, ExtendedTimer> subscriberTimer = new HashMap<PubSubNode, ExtendedTimer>();

	protected ExtendedTimer pingtimer = new ExtendedTimer();

	protected PingTask pingtask = null;

	protected InformBrokersTask informbrokerstask = null;

	protected InformSubscribersTask informsubscriberstask = null;

	protected ExtendedTimer pingTimeoutTimer = new ExtendedTimer();

	protected ExtendedTimer changetimer = new ExtendedTimer();

	protected long netsize = 0;

	protected long oldnetsize = 0;

	protected int nmbOnlineSubscribers = 0;

	protected HashSet<BrokerNode> connectedBrokers = new HashSet<BrokerNode>();

	protected boolean collectingSubscrInfo = false;

	protected boolean collectingNetworkInfo = false;

	public AdjustingBroker(int xp, int yp, SimParameters params) {
		super(xp, yp, params);
	}

	public void init() {

		newPingTimer();

	}

	public void receiveMessage(Message m) {

		// process only if not blocked
		if ( isBlocked() == true )
			return;

		if ( m instanceof RSSFeedMessage ) {

			handleRSSFeedMessage((RSSFeedMessage) m);

		} else if ( m instanceof SubnetParamMessage ) {

			handleSubnetParamMessage((SubnetParamMessage) m);

		} else if ( m instanceof RegisterBrokerMessage ) {

			handleRegisterBrokerMessage((RegisterBrokerMessage) m);

		} else if ( m instanceof RegisterAckMessage ) {

			handleRegisterAckMessage((RegisterAckMessage) m);

		} else if ( m instanceof UnregisterBrokerMessage ) {

			handleUnregisterBrokerMessage((UnregisterBrokerMessage) m);

		} else if ( m instanceof RegisterSubscriberMessage ) {

			handleRegisterSubscriberMessage((RegisterSubscriberMessage) m);

		} else if ( m instanceof UnregisterSubscriberMessage ) {

			handleUnregisterSubscriberMessage((UnregisterSubscriberMessage) m);

		} else if ( m instanceof InformBrokersMessage ) {

			processInformBrokers();

		} else if ( m instanceof InformSubscribersMessage ) {

			processInformSubscribers();

		} else if ( m instanceof TimeForPingMessage ) {

			handleTimeForPingMessage((TimeForPingMessage) m);

		} else if ( m instanceof PingMessage ) {

			handlePingMessage((PingMessage) m);

		} else if ( m instanceof PingTimeoutMessage ) {

			handlePingTimeoutMessage((PingTimeoutMessage) m);

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
	 * sends the size of a subnetwork to a node
	 * 
	 * @param broker
	 *            the destination broker
	 * @param messageInitiator
	 *            the broker which sent this message originally
	 * @param causeOfMessage
	 *            the node which caused this message
	 */
	protected void sendSubnetSize(BrokerNode broker, Node messageInitiator, Node causeOfMessage) {

		new SubnetParamMessage(this, broker, messageInitiator, causeOfMessage, new SubnetParameters(calcNetSizeWithout(broker), new Date()), params)
				.send();

	}

	synchronized protected PingTimeoutTask updatePingTimeoutTimer(PingTimeoutTask pingtimeouttask, BrokerNode broker) {

		if ( pingtimeouttask != null ) {
			pingtimeouttask.cancel();
			pingTimeoutTimer.purge();
		}
		pingtimeouttask = new PingTimeoutTask(this, broker);
		// if the broker gets bloked the task must continue triggering until the
		// broker does processing again
		pingTimeoutTimer.schedule(pingtimeouttask, params.pingTimeoutFactor * params.pingTimer, params.pingTimeoutFactor * params.pingTimer);
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

				callbackRegisterAtBroker((BrokerNode) arg);

			}

		} else if ( o instanceof Peers.RemoveNotifier ) {

			if ( arg instanceof BrokerNode )
				callbackUnregisterFromBroker((BrokerNode) arg);
			// removeFromBrokers((BrokerNode) arg);
		}

	}

	protected void registerAtBroker(BrokerNode broker) {

		// send a RegisterBrokerMessage
		new RegisterBrokerMessage(this, broker, new SubnetParameters(calcNetSizeWithout(broker), new Date()), params).send();

	}

	protected void unregisterFromBroker(BrokerNode broker) {

		processBrokerDisconnected(broker);

		// send a UnegisterBrokerMessage
		new UnregisterBrokerMessage(this, broker, params.subnetParamMsgRT).send();

	}

	protected void handleRSSFeedMessage(RSSFeedMessage fm) {

		// if message-feed is newer than our one, set it and send it to all
		// other peers
		if ( fm.getFeed().isNewerThan(getFeed()) ) {

			setFeed(fm.getFeed());

			// send a new RSSFeedMessage to all other Brokers and
			// Subscribers
			Set<Node> peers = getPeers();
			for ( Node peer : peers ) {
				if ( peer != fm.getSrc() )
					new RSSFeedMessage(this, peer, getFeed(), fm.getRssFeedRepresentation().copyWith(null, getFeed()), params).send();
			}

		}

	}

	protected void handleSubnetParamMessage(SubnetParamMessage spm) {

		// check if message came from an authorized node
		if ( getSubnets().containsKey((BrokerNode) spm.getSrc()) ) {
			SubnetSettings subnetsettings = getSubnets().get(spm.getSrc());

			// if we are the initiator of the message we discovered a
			// circle, so we brake down the connection to the
			// "causeOfMessage"-node which created the circle
			if ( spm.getMI() == this )
				if ( spm.getCOM() instanceof BrokerType )
					callbackUnregisterFromBroker((BrokerType) spm.getCOM());

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
			if ( spm.getSubParams().getSize() != subnetsettings.getSubParams().getSize() ) {
				// add difference to netsize
				adjustNetsize(spm.getSubParams().getSize() - subnetsettings.getSubParams().getSize());
				// set new size
				subnetsettings.getSubParams().setSize(spm.getSubParams().getSize());
				// send new size to all other neighbourbrokers
				// keep the original messageInitiator and causeOfMessage to
				// detect circles
				informAllBut(spm.getSrc(), spm.getMI(), spm.getCOM());
			}

		}

	}

	protected void handleRegisterBrokerMessage(RegisterBrokerMessage rbm) {

		addToBrokers((BrokerNode) rbm.getSrc());

		// check if Broker is already/still registered (due to an undetected
		// disconnection). If node is registered we have still the old
		// information about
		// the networksize, thus we must only add the difference of new and
		// old subnetsize to
		// networksize. if node is not yet registered we add him and set
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
		new RegisterAckMessage(this, rbm.getSrc(), params.subnetParamMsgRT / 2).send();

		// send subnetsize to node
		sendSubnetSize((BrokerNode) rbm.getSrc(), this, rbm.getSrc());

		// set timer
		subnetsettings.setPingtimeouttask(updatePingTimeoutTimer(oldtask, (BrokerNode) rbm.getSrc()));

		// only on change tell the others
		if ( rbm.getSubParams().getSize() != oldsubnetsize )
			informAllBut(rbm.getSrc(), this, rbm.getSrc());

	}

	protected void handleRegisterAckMessage(RegisterAckMessage ram) {

		BrokerNode broker = (BrokerNode) ram.getSrc();

		addToBrokers(broker);
		// we must add an entry for this node if it's not yet
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

	}

	protected void handleUnregisterBrokerMessage(UnregisterBrokerMessage ubm) {

		// only if we haven't disconnected as well
		if ( getBrokers().contains((BrokerNode) ubm.getSrc()) )
			processBrokerDisconnected((BrokerNode) ubm.getSrc());

	}

	protected void handleRegisterSubscriberMessage(RegisterSubscriberMessage rsm) {

		handleNewSubscriber((PubSubNode) rsm.getSrc());

	}

	protected void handleUnregisterSubscriberMessage(UnregisterSubscriberMessage usm) {

		handleUnregisteredSubscriber((PubSubNode) usm.getSrc());

	}

	protected void handleTimeForPingMessage(TimeForPingMessage tpm) {

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

	}

	protected void handlePingMessage(PingMessage pm) {

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
				// here we don't want to check for occurence of circles, so
				// we set MI and
				// COM always new
				informAllBut(pm.getSrc(), this, pm.getSrc());

			}

		}

	}

	protected void handlePingTimeoutMessage(PingTimeoutMessage ptm) {

		// imagine: just before the pingtimeout-message is put in the
		// message-queue the expected ping arrives.
		// The node will be removed although
		// we got his ping. To prevent this, we check if the task which sent
		// the pingtimeout-message is the "uptodate"-task.

		// if we have an entry for the node and the included task is not
		// the task
		// which send the message stop processing
		if ( getSubnets().containsKey((BrokerNode) ptm.getSrc()) ) {
			if ( getSubnets().get(ptm.getSrc()).pingtimeouttask != ptm.getTask() )
				return;
		}

		// everything normal -> do the processing
		// processBrokerDisconnected(ptm.getBroker());
		processNodeVanished(ptm.getNode());

	}

	protected void handleNewSubscriber(PubSubNode subscriber) {

		if ( getSubscribers().contains(subscriber) == false ) {

			addToSubscribers(subscriber);

			// send acknowledgement
			new RegisterAckMessage(this, subscriber, params.subnetParamMsgRT).send();

			// send current RSSFeed
			sendRssFeedMessage(this, subscriber);

			adjustNetsize(1);

			// wait an amount of time before informing the other brokers
			// must be a repeated task: if the node gets blocked, this operation
			// must be again triggered
			if ( isCollectingSubscrInfo() == false ) {
				informbrokerstask = new InformBrokersTask(this);
				changetimer.schedule(informbrokerstask, params.informBrokersTimeout, params.informBrokersTimeout);
				setCollectingSubscrInfo(true);
			}
		} else if ( subscriberTimer.containsKey(subscriber) ) {

			subscriberTimer.get(subscriber).cancel();

		}

		ExtendedTimer timer = new ExtendedTimer();
		timer.schedule(new PingTimeoutTask(this, subscriber), 2 * params.pingTimeoutFactor * params.pingTimer, 2 * params.pingTimeoutFactor
				* params.pingTimer);
		subscriberTimer.put(subscriber, timer);

	}

	protected void sendRssFeedMessage(Node src, Node dst) {
		new RSSFeedMessage(src, dst, getFeed(), new RSSFeedRepresentation(getFeed()), params).send();
	}

	protected void handleUnregisteredSubscriber(PubSubNode subscriber) {

		if ( getSubscribers().contains(subscriber) == true ) {

			subscriberTimer.get(subscriber).cancel();
			subscriberTimer.remove(subscriber);
			removeFromSubscribers(subscriber);

			adjustNetsize(-1);

			// wait an amount of time before informing the other brokers
			// must be a repeated task: if the node gets blocked, this operation
			// must be again triggered
			if ( isCollectingSubscrInfo() == false ) {
				informbrokerstask = new InformBrokersTask(this);
				changetimer.schedule(informbrokerstask, params.informBrokersTimeout, params.informBrokersTimeout);
				setCollectingSubscrInfo(true);
			}
		}

	}

	//	
	// protected void informBrokers(){
	//
	// Set<BrokerNode> currbrokers = getBrokers();
	// for ( BrokerNode node : currbrokers ) {
	// sendSubnetSize(node);
	// }
	//		
	// }

	protected void informSubscribers() {

		// inform also the subscribers
		Set<PubSubNode> currsubscribers = getSubscribers();
		for ( PubSubNode subscriber : currsubscribers )
			new NetworkSizeUpdateMessage(this, subscriber, getNetsize(), params).send();

	}

	protected void informAllBut(Node exclbroker, Node messageInitiator, Node causeOfMessage) {

		Set<BrokerNode> currbrokers = getBrokers();
		for ( BrokerNode otherbroker : currbrokers ) {
			if ( otherbroker != exclbroker )
				sendSubnetSize(otherbroker, messageInitiator, causeOfMessage);
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
			informsubscriberstask = new InformSubscribersTask(this);
			changetimer.schedule(informsubscriberstask, params.informSubscribersTimeout, params.informSubscribersTimeout);
			setCollectingNetworkInfo(true);
		}

	}

	protected void informAll(Node messageInitiator, Node causeOfMessage) {
		informAllBut(null, messageInitiator, causeOfMessage);
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
			informAll(this, this); // cause of message is not important

		// stop the triggering task
		// ALL THIS IS DUE TO A VERY SUBTLE BUG WHICH ANNOYED ME QUITE MUCH
		informbrokerstask.cancel();
		setCollectingSubscrInfo(false);

	}

	protected void processInformSubscribers() {

		if ( oldnetsize != getNetsize() )
			informSubscribers();
		informsubscriberstask.cancel();
		setCollectingNetworkInfo(false);

	}

	/**
	 * It's executed if the node unregistered
	 * 
	 * @param broker
	 *            the broker which disconnected from us
	 */
	protected void processBrokerDisconnected(BrokerNode broker) {

		long subnetsize = 0;

		// adjust network.size
		if ( getSubnets().containsKey(broker) )
			subnetsize = getSubnets().get(broker).getSubParams().getSize();
		adjustNetsize((-1) * subnetsize);

		// remove settings for node
		getSubnets().remove(broker);

		// remove node from brokerlist
		removeFromBrokers(broker);

		// inform others on change
		if ( subnetsize != 0 )
			informAllBut(broker, this, broker);

	}

	/**
	 * It's executed if we get no ping from node
	 * 
	 * @param node
	 *            the node
	 */
	protected void processNodeVanished(Node node) {

		if ( node instanceof BrokerNode ) {

			processBrokerVanished((BrokerNode) node);

		} else if ( node instanceof PubSubNode ) {

			processSubscriberVanished((PubSubNode) node);

		}

	}

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
			informAllBut(broker, this, broker);

	}

	protected void processSubscriberVanished(PubSubNode subscriber) {
		handleUnregisteredSubscriber(subscriber);
	}

	protected void ping() {

		// informBrokers();

		Set<BrokerNode> currbrokers = getBrokers();
		for ( BrokerNode broker : currbrokers ) {
			new PingMessage(this, broker, new SubnetParameters(calcNetSizeWithout(broker), new Date()), params).send();
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
	public void callbackRegisterAtBroker(BrokerType broker) {
		super.callbackRegisterAtBroker(broker);
		registerAtBroker((BrokerNode) broker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BrokerNode#unregister(rsspubsubframework.BrokerType)
	 */
	@Override
	public void callbackUnregisterFromBroker(BrokerType broker) {
		super.callbackUnregisterFromBroker(broker);
		unregisterFromBroker((BrokerNode) broker);
		try {
			removeConnection(this, (BrokerNode) broker);
		} catch ( ConcurrentModificationException e ) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BrokerNode#callbackUnregisterFromAllBrokers()
	 */
	@Override
	public void callbackUnregisterFromAllBrokers() {
		super.callbackUnregisterFromAllBrokers();
		Set<BrokerNode> brokers = getBrokers();
		for ( BrokerType broker : brokers )
			callbackUnregisterFromBroker(broker);
	}

}
