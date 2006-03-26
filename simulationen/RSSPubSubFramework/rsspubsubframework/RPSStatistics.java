/**
 * 
 */
package rsspubsubframework;

import java.util.Observable;
import java.util.Observer;

/**
 * @author friezi
 * 
 */
public class RPSStatistics {

	RPSStatistics statistics = this;

	private static int messageFrameFragmentSize = 1;

	private static int fragments = 500;

	private Long receivedRSSRequests = new Long(0);

	private Long omittedRSSRequests = new Long(0);

	private Long serverFeeds = new Long(0);

	private Long brokerFeeds = new Long(0);

	private Integer reOmRatio = new Integer(0);

	private Integer relReOmRatio = new Integer(0);

	private Integer srvBrkRatio = new Integer(0);

	private Integer relSrvBrkRatio = new Integer(0);

	public class ReceivedRSSFeedRequestObserver extends Observable implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable observable, Object object) {
			// TODO Auto-generated method stub
			synchronized ( statistics ) {
				incReceivedRSSRequests();
			}
			// System.out.println("received count ist: " + count);
			notifyObservers(getReceivedRSSRequests());
		}

		public void notifyObservers(Long count) {
			setChanged();
			super.notifyObservers(count);
		}

	}

	private ReceivedRSSFeedRequestObserver receivedRSSFeedRequestObserver = new ReceivedRSSFeedRequestObserver();

	public class OmittedRSSFeedRequestObserver extends Observable implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable observable, Object object) {
			// TODO Auto-generated method stub
			synchronized ( statistics ) {
				incOmittedRSSRequests();
			}
			// System.out.println("omitted count ist: " + count);
			notifyObservers(getOmittedRSSRequests());
		}

		public void notifyObservers(Long count) {
			setChanged();
			super.notifyObservers(count);
		}

	}

	private OmittedRSSFeedRequestObserver omittedRSSFeedRequestObserver = new OmittedRSSFeedRequestObserver();

	public class ServerFeedObserver extends Observable implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable observable, Object object) {
			// TODO Auto-generated method stub
			synchronized ( statistics ) {
				incServerFeeds();
			}
			notifyObservers(getServerFeeds());
		}

		public void notifyObservers(Long count) {
			setChanged();
			super.notifyObservers(count);
		}
	}

	private ServerFeedObserver serverFeedObserver = new ServerFeedObserver();

	public class BrokerFeedObserver extends Observable implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable observable, Object object) {
			synchronized ( statistics ) {
				incBrokerFeeds();
			}
			notifyObservers(getBrokerFeeds());
		}

		public void notifyObservers(Long count) {
			setChanged();
			super.notifyObservers(count);
		}

	}

	private BrokerFeedObserver brokerFeedObserver = new BrokerFeedObserver();

	public class ReOmRatioUpdater extends Observable implements Observer {

		public void addToObservables() {
			getReceivedRSSFeedRequestObserver().addObserver(this);
			getOmittedRSSFeedRequestObserver().addObserver(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observable#notifyObservers()
		 */
		public void notifyObservers(Integer count) {
			setChanged();
			super.notifyObservers(count);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1) {
			// TODO Auto-generated method stub

			synchronized ( statistics ) {
				long divisor = (getReceivedRSSRequests() + getOmittedRSSRequests());

				if ( divisor != 0 )
					setReOmRatio((int) (((100 * getOmittedRSSRequests())) / divisor));
				else
					setReOmRatio(0);
			}

			notifyObservers(getReOmRatio());

		}

	}

	private ReOmRatioUpdater reOmRatioUpdater = new ReOmRatioUpdater();

	public class SrvBrkRatioUpdater extends Observable implements Observer {

		public void addToObservables() {
			getServerFeedObserver().addObserver(this);
			getBrokerFeedObserver().addObserver(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1) {
			// TODO Auto-generated method stub

			synchronized ( statistics ) {
				long divisor = (getServerFeeds() + getBrokerFeeds());

				if ( divisor != 0 )
					setSrvBrkRatio((int) (((100 * getBrokerFeeds())) / divisor));
				else
					setSrvBrkRatio(0);
			}

			notifyObservers(getSrvBrkRatio());

		}

		public void notifyObservers(Integer count) {
			setChanged();
			super.notifyObservers(count);
		}

	}

	SrvBrkRatioUpdater srvBrkRatioUpdater = new SrvBrkRatioUpdater();

	public class RelReOmRatioUpdater extends Observable implements Observer {

		int messageCount = 0;

		long[] messageReFrame = new long[fragments];

		long[] messageOmFrame = new long[fragments];

		int fragment = 0;

		public RelReOmRatioUpdater() {
			for ( int i = 0; i < fragments; i++ ) {
				messageReFrame[i] = 0;
				messageOmFrame[i] = 0;
			}
		}

		public void addToObservables() {
			getReceivedRSSFeedRequestObserver().addObserver(this);
			getOmittedRSSFeedRequestObserver().addObserver(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public synchronized void update(Observable observable, Object arg1) {
			// TODO Auto-generated method stub

			// upto limit of message-counter
			if ( messageCount == messageFrameFragmentSize ) {

				messageCount = 0;
				// cycle through fragments
				if ( fragment == fragments - 1 )
					fragment = 0;
				else
					fragment++;

			} else {
				messageCount++;
			}
			// delete content of old fragment
			if ( messageCount == 0 ) {
				messageReFrame[fragment] = 0;
				messageOmFrame[fragment] = 0;
			}

			// add current to appropriate array

			if ( observable instanceof ReceivedRSSFeedRequestObserver )
				messageReFrame[fragment]++;
			else if ( observable instanceof OmittedRSSFeedRequestObserver )
				messageOmFrame[fragment]++;

			long reMessages = 0;
			long omMessages = 0;

			for ( int i = 0; i < fragments; i++ ) {
				reMessages += messageReFrame[i];
				omMessages += messageOmFrame[i];
			}

			long divisor = (reMessages + omMessages);

			synchronized ( statistics ) {

				if ( divisor != 0 )
					setRelReOmRatio((int) ((100 * omMessages) / divisor));
				else
					setRelReOmRatio(0);

				notifyObservers(getRelReOmRatio());

			}

		}

		public void notifyObservers(Integer count) {
			setChanged();
			super.notifyObservers(count);
		}

	}

	RelReOmRatioUpdater relReOmRatioUpdater = new RelReOmRatioUpdater();

	public class RelSrvBrkRatioUpdater extends Observable implements Observer {

		int messageCount = 0;

		long[] messageSrvFrame = new long[fragments];

		long[] messageBrkFrame = new long[fragments];

		int fragment = 0;

		public RelSrvBrkRatioUpdater() {
			for ( int i = 0; i < fragments; i++ ) {
				messageSrvFrame[i] = 0;
				messageBrkFrame[i] = 0;
			}
		}

		public void addToObservables() {
			getServerFeedObserver().addObserver(this);
			getBrokerFeedObserver().addObserver(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public synchronized void update(Observable observable, Object arg1) {
			// TODO Auto-generated method stub

			// upto limit of message-counter
			if ( messageCount == messageFrameFragmentSize ) {

				messageCount = 0;
				// cycle through fragments
				if ( fragment == fragments - 1 )
					fragment = 0;
				else
					fragment++;

			} else {
				messageCount++;
			}
			// delete content of old fragment
			if ( messageCount == 0 ) {
				messageSrvFrame[fragment] = 0;
				messageBrkFrame[fragment] = 0;
			}

			// add current to appropriate array

			if ( observable instanceof ServerFeedObserver )
				messageSrvFrame[fragment]++;
			else if ( observable instanceof BrokerFeedObserver )
				messageBrkFrame[fragment]++;

			long srvMessages = 0;
			long brkMessages = 0;

			for ( int i = 0; i < fragments; i++ ) {
				srvMessages += messageSrvFrame[i];
				brkMessages += messageBrkFrame[i];
			}

			long divisor = (srvMessages + brkMessages);

			synchronized ( statistics ) {

				if ( divisor != 0 )
					setRelSrvBrkRatio((int) ((100 * brkMessages) / divisor));
				else
					setRelSrvBrkRatio(0);

				notifyObservers(getRelSrvBrkRatio());

			}

		}

		public void notifyObservers(Integer count) {
			setChanged();
			super.notifyObservers(count);
		}

	}

	RelSrvBrkRatioUpdater relSrvBrkRatioUpdater = new RelSrvBrkRatioUpdater();

	public RPSStatistics() {
		getReOmRatioUpdater().addToObservables();
		getSrvBrkRatioUpdater().addToObservables();
		getRelReOmRatioUpdater().addToObservables();
		getRelSrvBrkRatioUpdater().addToObservables();
	}

	/**
	 * @return Returns the receivedRSSFeedRequestObserver.
	 */
	public ReceivedRSSFeedRequestObserver getReceivedRSSFeedRequestObserver() {
		return receivedRSSFeedRequestObserver;
	}

	/**
	 * @return Returns the omittedRSSFeedRequestObserver.
	 */
	public OmittedRSSFeedRequestObserver getOmittedRSSFeedRequestObserver() {
		return omittedRSSFeedRequestObserver;
	}

	/**
	 * @return Returns the serverFeedObserver.
	 */
	public ServerFeedObserver getServerFeedObserver() {
		return serverFeedObserver;
	}

	/**
	 * @return Returns the brokerFeedObserver.
	 */
	public BrokerFeedObserver getBrokerFeedObserver() {
		return brokerFeedObserver;
	}

	/**
	 * @return Returns the reOmRatioUpdater.
	 */
	public ReOmRatioUpdater getReOmRatioUpdater() {
		return reOmRatioUpdater;
	}

	/**
	 * @return Returns the srvBrkRatioUpdater.
	 */
	public SrvBrkRatioUpdater getSrvBrkRatioUpdater() {
		return srvBrkRatioUpdater;
	}

	/**
	 * @return Returns the relReOmRatioUpdater.
	 */
	public RelReOmRatioUpdater getRelReOmRatioUpdater() {
		return relReOmRatioUpdater;
	}

	/**
	 * @return Returns the relSrvBrkRatioUpdater.
	 */
	public RelSrvBrkRatioUpdater getRelSrvBrkRatioUpdater() {
		return relSrvBrkRatioUpdater;
	}

	protected synchronized void incBrokerFeeds() {
		brokerFeeds++;
	}

	/**
	 * @return Returns the brokerFeeds.
	 */
	public synchronized Long getBrokerFeeds() {
		return brokerFeeds;
	}

	protected synchronized void incOmittedRSSRequests() {
		omittedRSSRequests++;
	}

	/**
	 * @return Returns the omittedRSSRequests.
	 */
	public synchronized Long getOmittedRSSRequests() {
		return omittedRSSRequests;
	}

	protected synchronized void incReceivedRSSRequests() {
		receivedRSSRequests++;
	}

	/**
	 * @return Returns the receivedRSSRequests.
	 */
	public synchronized Long getReceivedRSSRequests() {
		return receivedRSSRequests;
	}

	/**
	 * @return Returns the reOmRatio.
	 */
	public synchronized Integer getReOmRatio() {
		return reOmRatio;
	}

	protected synchronized void incServerFeeds() {
		serverFeeds++;
	}

	/**
	 * @return Returns the serverFeeds.
	 */
	public synchronized Long getServerFeeds() {
		return serverFeeds;
	}

	/**
	 * @return Returns the srvBrkRatio.
	 */
	public synchronized Integer getSrvBrkRatio() {
		return srvBrkRatio;
	}

	/**
	 * @param reOmRatio
	 *            The reOmRatio to set.
	 */
	public synchronized void setReOmRatio(int reOmRatio) {
		this.reOmRatio = reOmRatio;
	}

	/**
	 * @param srvBrkRatio
	 *            The srvBrkRatio to set.
	 */
	public synchronized void setSrvBrkRatio(int srvBrkRatio) {
		this.srvBrkRatio = srvBrkRatio;
	}

	/**
	 * @return Returns the relReOmRatio.
	 */
	public synchronized Integer getRelReOmRatio() {
		return relReOmRatio;
	}

	/**
	 * @param relReOmRatio
	 *            The relReOmRatio to set.
	 */
	public synchronized void setRelReOmRatio(int relReOmRatio) {
		this.relReOmRatio = relReOmRatio;
	}

	/**
	 * @return Returns the relSrvBrkRatio.
	 */
	public synchronized Integer getRelSrvBrkRatio() {
		return relSrvBrkRatio;
	}

	/**
	 * @param relSrvBrkRatio
	 *            The relSrvBrkRatio to set.
	 */
	public synchronized void setRelSrvBrkRatio(int relSrvBrkRatio) {
		this.relSrvBrkRatio = relSrvBrkRatio;
	}

}
