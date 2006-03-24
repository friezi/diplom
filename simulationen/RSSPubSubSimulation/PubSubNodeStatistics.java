import java.util.*;

import rsspubsubframework.Node;

/**
 * 
 */

/**
 * stores statistical values for the PubSubNode
 * 
 * @author Friedemann Zintel
 * 
 */
public class PubSubNodeStatistics {

	private long omittedRSSFeedRequests = 0;

	private long serverFeeds = 0;

	private long brokerFeeds = 0;

	protected class OmittedRSSFeedRequestNotifier extends Observable {
		public void notifyObservers(Node node) {
			setChanged();
			super.notifyObservers(node);
		}
	}

	protected OmittedRSSFeedRequestNotifier omittedRSSFeedRequestNotifier = new OmittedRSSFeedRequestNotifier();

	/**
	 * increases the amount of omitted feed-requests and informs observers
	 * 
	 * @param node
	 *            the node which should be transitted to the observer
	 */
	public void addOmittedRSSFeedRequest(Node node) {
		omittedRSSFeedRequests++;
		getOmittedRSSFeedRequestNotifier().notifyObservers(node);
	}

	/**
	 * adds an observer for the parameter omittedRSSFeedRequests
	 * 
	 * @param observer
	 *            the observer
	 */
	public void addOmittedRSSFeedRequestObserver(Observer observer) {
		getOmittedRSSFeedRequestNotifier().addObserver(observer);
	}

	/**
	 * @return Returns the omittedRSSFeedRequestNotifier.
	 */
	public OmittedRSSFeedRequestNotifier getOmittedRSSFeedRequestNotifier() {
		return omittedRSSFeedRequestNotifier;
	}

	/**
	 * @return Returns the omittedRSSFeedRequests.
	 */
	public long getOmittedRSSFeedRequests() {
		return omittedRSSFeedRequests;
	}

	protected class ServerFeedNotifier extends Observable {
		public void notifyObservers(Node node) {
			setChanged();
			super.notifyObservers(node);
		}
	}

	protected ServerFeedNotifier serverFeedNotifier = new ServerFeedNotifier();

	public void addServerFeed(Node node) {
		serverFeeds++;
		getServerFeedNotifier().notifyObservers(node);
	}

	public void addServerFeedObserver(Observer observer) {
		getServerFeedNotifier().addObserver(observer);
	}

	/**
	 * @return Returns the serverFeedNotifier.
	 */
	public ServerFeedNotifier getServerFeedNotifier() {
		return serverFeedNotifier;
	}

	protected class BrokerFeedNotifier extends Observable {
		public void notifyObservers(Node node) {
			setChanged();
			super.notifyObservers(node);
		}
	}

	protected BrokerFeedNotifier brokerFeedNotifier = new BrokerFeedNotifier();

	public void addBrokerFeed(Node node) {
		brokerFeeds++;
		getBrokerFeedNotifier().notifyObservers(node);
	}

	public void addBrokerFeedObserver(Observer observer) {
		getBrokerFeedNotifier().addObserver(observer);
	}

	/**
	 * @return Returns the brokerFeedNotifier.
	 */
	public BrokerFeedNotifier getBrokerFeedNotifier() {
		return brokerFeedNotifier;
	}

}
