package de.zintel.diplom.statistics;
import java.util.*;

import de.zintel.diplom.simulation.Node;

/**
 * 
 */

/**
 * stores statistical values fo the RSSServer
 * 
 * @author Friedemann Zintel
 * 
 */
public class RSSServerNodeStatistics {

	private long receivedRSSFeedRequests = 0;

	private long requestsInQueue = 0;

	private long unrepliedRequests = 0;

	private long totalTemporaryRequests = 0;

	protected class ReceivedRSSFeedRequestNotifier extends Observable {
		public void notifyObservers(Node node) {
			setChanged();
			super.notifyObservers(node);
		}
	}

	protected ReceivedRSSFeedRequestNotifier receivedRSSFeedRequestNotifier = new ReceivedRSSFeedRequestNotifier();

	/**
	 * increases the amount of received rss-requests
	 * 
	 * @param node
	 *            the node which should nbe transferred to the observer
	 */
	public void addReceivedRSSFeedRequest(Node node) {
		receivedRSSFeedRequests++;
		getReceivedRSSFeedRequestNotifier().notifyObservers(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.RSSServerType#addReceivedRSSFeedRequestObserver(java.util.Observer)
	 */
	/**
	 * adds an observer for the variable receivedRSSFeedRequests;
	 * 
	 * @param observer
	 *            the obbserver to be added
	 */
	public void addReceivedRSSFeedRequestObserver(Observer observer) {
		getReceivedRSSFeedRequestNotifier().addObserver(observer);
	}

	/**
	 * @return Returns the omittedRSSFeedRequestNotifier.
	 */
	public ReceivedRSSFeedRequestNotifier getReceivedRSSFeedRequestNotifier() {
		return receivedRSSFeedRequestNotifier;
	}

	/**
	 * @return Returns the receivedRSSFeedRequests.
	 */
	public long getReceivedRSSFeedRequests() {
		return receivedRSSFeedRequests;
	}

	protected class RequestsInQueueNotifier extends Observable {
		public void notifyObservers(long riq) {
			setChanged();
			super.notifyObservers((Long) riq);
		}
	}

	protected RequestsInQueueNotifier requestsInQueueNotifier = new RequestsInQueueNotifier();

	public void addRequestsInQueueObserver(Observer observer) {
		getRequestsInQueueNotifier().addObserver(observer);
	}

	/**
	 * @param requestsInQueue The requestsInQueue to set.
	 */
	public void setRequestsInQueue(long requestsInQueue) {
		this.requestsInQueue = requestsInQueue;
		getRequestsInQueueNotifier().notifyObservers(requestsInQueue);
	}

	/**
	 * @return Returns the requestsInQueue.
	 */
	public long getRequestsInQueue() {
		return requestsInQueue;
	}

	/**
	 * @return Returns the requestsInQueueNotifier.
	 */
	public RequestsInQueueNotifier getRequestsInQueueNotifier() {
		return requestsInQueueNotifier;
	}

	protected class UnrepliedRequestsNotifier extends Observable {
		public void notifyObservers(long ur) {
			setChanged();
			super.notifyObservers((Long) ur);
		}

	}

	protected UnrepliedRequestsNotifier unrepliedRequestsNotifier = new UnrepliedRequestsNotifier();

	public void addUnrepliedRequestsObserver(Observer observer) {
		getUnrepliedRequestsNotifier().addObserver(observer);
	}

	/**
	 * @return Returns the unrepliedRequestsNotifier.
	 */
	public UnrepliedRequestsNotifier getUnrepliedRequestsNotifier() {
		return unrepliedRequestsNotifier;
	}

	/**
	 * @return Returns the unrepliedRequests.
	 */
	public long getUnrepliedRequests() {
		return unrepliedRequests;
	}

	/**
	 * @param unrepliedRequests The unrepliedRequests to set.
	 */
	public void setUnrepliedRequests(long unrepliedRequests) {
		this.unrepliedRequests = unrepliedRequests;
		getUnrepliedRequestsNotifier().notifyObservers(unrepliedRequests);
	}

	public class TotalTemporaryRequestsNotifier extends Observable {

		public void notifyObservers(long ttr) {
			setChanged();
			super.notifyObservers((Long) ttr);
		}

	}

	protected TotalTemporaryRequestsNotifier totalTemporaryRequestsNotifier = new TotalTemporaryRequestsNotifier();

	/**
	 * @return Returns the totalRequests.
	 */
	public long getTotalTemporaryRequests() {
		return totalTemporaryRequests;
	}

	/**
	 * @param totalTemporaryRequests The totalRequests to set.
	 */
	public void setTotalTemporaryRequests(long totalTemporaryRequests) {
		this.totalTemporaryRequests = totalTemporaryRequests;
		getTotalTemporaryRequestsNotifier().notifyObservers(totalTemporaryRequests);
	}

	/**
	 * @return Returns the totalTemporaryRequestsNotifier.
	 */
	public TotalTemporaryRequestsNotifier getTotalTemporaryRequestsNotifier() {
		return totalTemporaryRequestsNotifier;
	}

	public void addTotalTemporaryRequestsObserver(Observer observer) {
		getTotalTemporaryRequestsNotifier().addObserver(observer);
	}

}
