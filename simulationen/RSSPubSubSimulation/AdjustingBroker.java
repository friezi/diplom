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

		public SubnetSettings() {
		}

		public SubnetSettings(long size) {
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

	}

	protected class PingTask extends TimerTask {

		BrokerNode broker;

		public void run() {

		}
	}

	// a dummy-feed to start with and prevent NullPointerException
	private RSSFeed feed = new RSSFeed(new RSSFeedGeneralContent());

	// subnets and its settings
	private HashMap<BrokerNode, SubnetSettings> subnets = new HashMap<BrokerNode, SubnetSettings>();

	private Timer pingTimer = new Timer();

	private int nmbSubscribers = 0;

	private long netsize = 0;

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
		// nmbSubscribers = subscribers.size();
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

			// only adjust if new size differs from old size
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

			handleNewBroker((BrokerNode) rbm.getSrc());

			informOtherBrokers(rbm.getSrc());

		} else if ( m instanceof RegisterSubscriberMessage ) {

			RegisterSubscriberMessage rsm = (RegisterSubscriberMessage) m;

			handleNewSubscriber((PubSubNode) rsm.getSrc());
		}
	}

	/**
	 * calculates the netsize by adding all subnetsizes and number of local
	 * subscribers
	 */
	synchronized protected void adjustNetsize() {

		setNetsize(0);

		// add size of subnets
		Set<BrokerNode> currbrokers = getBrokers();
		for ( BrokerNode broker : currbrokers )
			setNetsize(getNetsize() + subnets.get(broker).getSubParams().getSize());

		// add number of subscribers
		setNetsize(getNetsize() + getNmbSubscribers());

	}

	/**
	 * @param delta
	 *            the delta-value which should be added to netsize
	 */
	synchronized protected void adjustNetsize(long delta) {
		setNetsize(getNetsize() + delta);
	}

	protected long calcNetSizeWithout(BrokerNode broker) {

		long subnetsize = 0;

		Set<BrokerNode> currbrokers = getBrokers();
		for ( BrokerNode otherbroker : currbrokers )
			if ( otherbroker != broker )
				subnetsize += subnets.get(otherbroker).getSubParams().getSize();
		subnetsize += getSubscribersSize();

		return subnetsize;

	}

	protected void sendSubnetSize(BrokerNode broker) {

		new SubnetParamMessage(this, broker, new SubnetParameters(calcNetSizeWithout(broker)),
				params.subntSzMsgRT);

	}

	//
	// synchronized protected PingTask updatePingTimer(PingTask pingtask){
	//		 
	// pingtask.cancel();
	// pingtask=new PingTask();
	// // pingtaskTimer
	//		 
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see BrokerNode#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {

		if ( o instanceof Peers.AddNotifier ) {

			if ( arg instanceof BrokerNode )
				addToBrokers((BrokerNode) arg);
//			else if ( arg instanceof PubSubNode )
//				addToSubscribers((PubSubNode) arg);

		} else if ( o instanceof Peers.RemoveNotifier ) {

			if ( arg instanceof BrokerNode )
				removeFromBrokers((BrokerNode) arg);
			else if ( arg instanceof PubSubNode )
				removeFromSubscribers((PubSubNode) arg);
		}

		if ( o instanceof Peers.AddNotifier ) {

			if ( arg instanceof BrokerNode ) {

				// register new broker
				registerNewBroker((BrokerNode) arg);

			}

		}

	}

	protected void handleNewBroker(BrokerNode broker) {

		// a new broker joined the network, tell him about subnetworks

		getSubnets().put(broker, new SubnetSettings());
		//
		// // determine subscribers
		// nmbSubscribers = getSubscribersSize();

		adjustNetsize();

		sendSubnetSize(broker);

	}

	protected void registerNewBroker(BrokerNode broker) {

		// a new broker joined the network, tell him about subnetworks

		getSubnets().put(broker, new SubnetSettings());
		//
		// // determine subscribers
		// nmbSubscribers = getSubscribersSize();

		//
		// // PLEASE REMOVE WEHN REGISTERING UF SUBSCRIBERS IS IMPLEMENTED!!!
		// adjustNetsize();

		// send a RegisterBrokerMessage
		new RegisterBrokerMessage(this, broker, new SubnetParameters(calcNetSizeWithout(broker)),
				params.subntSzMsgRT);

	}

	protected void handleNewSubscriber(PubSubNode subscriber) {

		addToSubscribers(subscriber);

		// determine subscribers
		nmbSubscribers = getSubscribersSize();

		adjustNetsize(1);
//		adjustNetsize();

		informBrokers();
	}

	protected void informOtherBrokers(Node exclbroker) {

		Set<BrokerNode> currbrokers = getBrokers();
		for ( BrokerNode otherbroker : currbrokers ) {
			if ( otherbroker != exclbroker )
				sendSubnetSize(otherbroker);
		}
	}

	protected void informBrokers() {

		Set<BrokerNode> currbrokers = getBrokers();
		for ( BrokerNode otherbroker : currbrokers ) {
			sendSubnetSize(otherbroker);
		}
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
	 * @return Returns the nmbSubscribers.
	 */
	protected synchronized int getNmbSubscribers() {
		return nmbSubscribers;
	}

	/**
	 * @param nmbSubscribers
	 *            The nmbSubscribers to set.
	 */
	protected synchronized void setNmbSubscribers(int nmbSubscribers) {
		this.nmbSubscribers = nmbSubscribers;
	}

}
