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
		private long size = 0;

		/**
		 * @return Returns the size.
		 */
		public long getSize() {
			return size;
		}

		/**
		 * @param size
		 *            The size to set.
		 */
		public void setSize(long size) {
			this.size = size;
		}
	}
	
	protected class PingTask extends TimerTask{
		
		BrokerNode broker;
		
		public void run(){
			
		}
	}

	// a dummy-feed to start with and prevent NullPointerException
	private RSSFeed feed = new RSSFeed(new RSSFeedGeneralContent());

	// subnets and its settings
	private HashMap<BrokerNode, SubnetSettings> subnets = new HashMap<BrokerNode, SubnetSettings>();
	
	private Timer pingTimer=new Timer();

	private int nmbSubscribers = 0;

	private long netsize = 0;

	public AdjustingBroker(int xp, int yp, SimParameters params) {
		super(xp, yp, params);
		// TODO Auto-generated constructor stub
	}

	public void init() {

		super.init();

		Set<Node> peers = getPeers();

		// determine neighbourbrokers
		for ( BrokerNode broker : brokers )
			getSubnets().put((BrokerNode) broker, new SubnetSettings());

		// determine subscribers
		nmbSubscribers = subscribers.size();

		adjustNetsize();

		// tell all other neighbourbrokers about subnetsizes
		for ( BrokerNode broker : brokers )
			sendSubnetSize(broker);

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

		} else if ( m instanceof SubnetSizeMessage ) {

			SubnetSizeMessage ssm = (SubnetSizeMessage) m;
			SubnetSettings subnetsettings = getSubnets().get(ssm.getSrc());

			// only adjust if new size differs from old size
			if ( ssm.getSubnetsize() != subnetsettings.getSize() ) {

				// add difference to netsize
				adjustNetsize(ssm.getSubnetsize() - subnetsettings.getSize());

				// set new size
				subnetsettings.setSize(ssm.getSubnetsize());

				// send new size to all other neighbourbrokers
				for ( BrokerNode otherbroker : brokers ) {
					if ( otherbroker != ssm.getSrc() )
						sendSubnetSize(otherbroker);
				}
			}

		}
	}

	/**
	 * calculates the netsize by adding all subnetsizes and number of local
	 * subscribers
	 */
	synchronized protected void adjustNetsize() {

		setNetsize(0);

		// add size of subnets
		for ( BrokerNode broker : brokers )
			setNetsize(getNetsize() + subnets.get(broker).getSize());

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

	protected void sendSubnetSize(BrokerNode broker) {

		long subnetsize = 0;

		for ( BrokerNode otherbroker : brokers )
			if ( otherbroker != broker )
				subnetsize += subnets.get(otherbroker).getSize();
		subnetsize += subscribers.size();

		new SubnetSizeMessage(this, broker, subnetsize, params.subntSzMsgRT);

	}
//
//	 synchronized protected PingTask updatePingTimer(PingTask pingtask){
//		 
//		 pingtask.cancel();
//		 pingtask=new PingTask();
////		 pingtaskTimer
//		 
//	 }
	
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
