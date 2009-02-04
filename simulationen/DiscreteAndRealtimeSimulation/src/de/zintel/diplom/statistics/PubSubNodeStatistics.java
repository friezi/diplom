package de.zintel.diplom.statistics;
import java.util.*;

import de.zintel.diplom.rps.pubsub.PubSubNode;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;

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

	private SimParameters params;

	private long omittedRSSFeedRequests = 0;

	private long serverFeeds = 0;

	private long brokerFeeds = 0;

	private int uptodateRatio = 100;

	private int messageDelayRatio = 0;

	private PubSubNode node = null;

	public PubSubNodeStatistics(PubSubNode node, SimParameters params) {
		this.params = params;
		this.node = node;

	}

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

	protected class UptodateRatioNotifier extends Observable {
		public void notifyObservers(Integer uptodateRatio) {
			setChanged();
			super.notifyObservers(uptodateRatio);
		}
	}

	protected UptodateRatioNotifier uptodateRatioNotifier = new UptodateRatioNotifier();

	/**
	 * @return Returns the uptodateRatioNotifier.
	 */
	public UptodateRatioNotifier getUptodateRatioNotifier() {
		return uptodateRatioNotifier;
	}

	/**
	 * @return Returns the uptodateRatio.
	 */
	public int getUptodateRatio() {
		return uptodateRatio;
	}

	public void addUptodateRatioObserver(Observer observer) {
		getUptodateRatioNotifier().addObserver(observer);
	}

	/**
	 * @param uptodateRatio
	 *            The uptodateRatio to set.
	 */
	public void setUptodateRatio(int uptodateRatio) {
		this.uptodateRatio = uptodateRatio;
		getUptodateRatioNotifier().notifyObservers(uptodateRatio);
	}

	protected class MessageDelayRatioNotifier extends Observable {
		public void notifyObservers(Integer messageDelayRatio) {
			setChanged();
			super.notifyObservers(messageDelayRatio);
		}
	}

	protected MessageDelayRatioNotifier messageDelayRatioNotifier = new MessageDelayRatioNotifier();

	public void addMessageDelayRatioObserver(Observer observer) {
		getMessageDelayRatioNotifier().addObserver(observer);
	}

	/**
	 * @return Returns the messageDelayRatioNotifier.
	 */
	public MessageDelayRatioNotifier getMessageDelayRatioNotifier() {
		return messageDelayRatioNotifier;
	}

	/**
	 * @return Returns the messageDelayRatio.
	 */
	public int getMessageDelayRatio() {
		return messageDelayRatio;
	}

	/**
	 * @param messageDelayRatio
	 *            The messageDelayRatio to set.
	 */
	public void setMessageDelayRatio(int messageDelayRatio) {
		this.messageDelayRatio = messageDelayRatio;
		getMessageDelayRatioNotifier().notifyObservers(messageDelayRatio);
	}

	protected class CurrPollPeriodNotifier extends Observable {

		private PubSubNodeStatistics pubSubNodeStatistics = null;

		public CurrPollPeriodNotifier(PubSubNodeStatistics pubSubNodeStatistics) {
			this.pubSubNodeStatistics = pubSubNodeStatistics;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observable#notifyObservers(java.lang.Object)
		 */
		@Override
		public void notifyObservers(Object arg0) {
			setChanged();
			super.notifyObservers(arg0);
		}

		/**
		 * @return Returns the pubSubNodeStatistics.
		 */
		public PubSubNodeStatistics getPubSubNodeStatistics() {
			return pubSubNodeStatistics;
		}

	}

	private CurrPollPeriodNotifier currPollPeriodNotifier = new CurrPollPeriodNotifier(this);

	/**
	 * @return Returns the currPollPeriodNotifier.
	 */
	public CurrPollPeriodNotifier getCurrPollPeriodNotifier() {
		return currPollPeriodNotifier;
	}

	public synchronized void addCurrPollPeriodObserver(Observer observer) {
		currPollPeriodNotifier.addObserver(observer);
	}

	public synchronized void deleteCurrPollPeriodObserver(Observer observer) {
		currPollPeriodNotifier.deleteObserver(observer);
	}

	public void updateCurrPollPeriod(Long currPollPeriod) {
		currPollPeriodNotifier.notifyObservers(currPollPeriod);
	}

	/**
	 * @return Returns the node.
	 */
	public PubSubNode getNode() {
		return node;
	}

}
