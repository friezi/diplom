package de.zintel.diplom.statistics;
/**
 * 
 */

import java.util.*;
import java.io.*;

import de.zintel.diplom.gnuplot.AbstractGnuplotDataProvider;
import de.zintel.diplom.gnuplot.GnuplotDataRecorder;
import de.zintel.diplom.gnuplot.GnuplotFloatProvider;
import de.zintel.diplom.gnuplot.GnuplotIntegerProvider;
import de.zintel.diplom.gnuplot.GnuplotLongProvider;
import de.zintel.diplom.rps.pubsub.PubSubType;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.Message;
import de.zintel.diplom.simulation.SimParameters;

/**
 * @author Friedemann Zintel
 * 
 */
public class RPSStatistics {

	RPSStatistics statistics = this;

	SimParameters params;

	private AbstractGnuplotDataProvider gnuplotproviderTotTempReq = null;

	private AbstractGnuplotDataProvider gnuplotproviderMeanValueCPP = null;

	private AbstractGnuplotDataProvider gnuplotproviderMeanDvtCPP = null;

	private AbstractGnuplotDataProvider gnuplotproviderMeanDvtCoeffCPP = null;

	private AbstractGnuplotDataProvider gnuplotproviderAverageMessageDelayRatio = null;

	private AbstractGnuplotDataProvider gnuplotproviderAverageUptodateRatio = null;

	private AbstractGnuplotDataProvider gnuplotproviderRelReOmRatio = null;

	private GnuplotDataRecorder gnuplotrecorderTotTempReq = null;

	private GnuplotDataRecorder gnuplotrecorderMeanValueCPP = null;

	private GnuplotDataRecorder gnuplotrecorderMeanDvtCPP = null;

	private GnuplotDataRecorder gnuplotrecorderMeanDvtCoeffCPP = null;

	private GnuplotDataRecorder gnuplotrecorderAverageMessageDelayRatio = null;

	private GnuplotDataRecorder gnuplotrecorderAverageUptodateRatio = null;

	private GnuplotDataRecorder gnuplotrecorderRelReOmRatio = null;

	private static int messageFrameFragmentSize = 1;

	private static int fragments = 360;

	private Long receivedRSSRequests = new Long(0);

	private Long omittedRSSRequests = new Long(0);

	private Long serverFeeds = new Long(0);

	private Long brokerFeeds = new Long(0);

	private Integer reOmRatio = new Integer(0);

	private Integer relReOmRatio = new Integer(0);

	private Integer srvBrkRatio = new Integer(0);

	private Integer relSrvBrkRatio = new Integer(0);

	private Integer averageUptodateRatio = new Integer(0);

	private Integer averageMessageDelayRatio = new Integer(0);

	private Integer delayedMessagesRatio = new Integer(0);

	private Long requestsInQueue = new Long(0);

	private Long unrepliedRequests = new Long(0);

	private Long totalTemporaryRequests = new Long(0);

	private Vector<Long> currPollPeriods = new Vector<Long>();

	private Long meanValueCurrPollPeriods = new Long(0);

	private Long stdDevCurrPollPeriods = new Long(0);

	private Float coeffVarCurrPollPeriods = new Float(0);

	public class ReceivedRSSFeedRequestObserver extends Observable implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable observable, Object object) {
			synchronized ( statistics ) {
				incReceivedRSSRequests();
			}
			// System.out.println("received count ist: " + count);
			notifyObservers(getReceivedRSSRequests());
		}

		public synchronized void notifyObservers(Long count) {
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
			synchronized ( statistics ) {
				incOmittedRSSRequests();
			}
			// System.out.println("omitted count ist: " + count);
			notifyObservers(getOmittedRSSRequests());
		}

		public synchronized void notifyObservers(Long count) {
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
			synchronized ( statistics ) {
				incServerFeeds();
			}
			notifyObservers(getServerFeeds());
		}

		public synchronized void notifyObservers(Long count) {
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

		public synchronized void notifyObservers(Long count) {
			setChanged();
			super.notifyObservers(count);
		}

	}

	private BrokerFeedObserver brokerFeedObserver = new BrokerFeedObserver();

	public class ReOmRatioUpdater extends Observable implements Observer {

		public void addToObservables() {
			addNotifierObserver(getReceivedRSSFeedRequestObserver(), this);
			addNotifierObserver(getOmittedRSSFeedRequestObserver(), this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observable#notifyObservers()
		 */
		public synchronized void notifyObservers(Integer count) {
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
			addNotifierObserver(getServerFeedObserver(), this);
			addNotifierObserver(getBrokerFeedObserver(), this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1) {

			synchronized ( statistics ) {
				long divisor = (getServerFeeds() + getBrokerFeeds());

				if ( divisor != 0 )
					setSrvBrkRatio((int) (((100 * getBrokerFeeds())) / divisor));
				else
					setSrvBrkRatio(0);
			}

			notifyObservers(getSrvBrkRatio());

		}

		public synchronized void notifyObservers(Integer count) {
			setChanged();
			super.notifyObservers(count);
		}

	}

	SrvBrkRatioUpdater srvBrkRatioUpdater = new SrvBrkRatioUpdater();

	public class RelReOmRatioUpdater extends Observable implements Observer {

		int messageCount = messageFrameFragmentSize;

		long[] messageReFrame = new long[fragments];

		long[] messageOmFrame = new long[fragments];

		int fragment = fragments - 1;

		public RelReOmRatioUpdater() {
			for ( int i = 0; i < fragments; i++ ) {
				messageReFrame[i] = 0;
				messageOmFrame[i] = 0;
			}
		}

		public void addToObservables() {
			addNotifierObserver(getReceivedRSSFeedRequestObserver(), this);
			addNotifierObserver(getOmittedRSSFeedRequestObserver(), this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public synchronized void update(Observable observable, Object arg1) {

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

		public synchronized void notifyObservers(Integer count) {
			setChanged();
			super.notifyObservers(count);
		}

	}

	RelReOmRatioUpdater relReOmRatioUpdater = new RelReOmRatioUpdater();

	public class RelSrvBrkRatioUpdater extends Observable implements Observer {

		int messageCount = messageFrameFragmentSize;

		long[] messageSrvFrame = new long[fragments];

		long[] messageBrkFrame = new long[fragments];

		int fragment = fragments - 1;

		public RelSrvBrkRatioUpdater() {
			for ( int i = 0; i < fragments; i++ ) {
				messageSrvFrame[i] = 0;
				messageBrkFrame[i] = 0;
			}
		}

		public void addToObservables() {
			addNotifierObserver(getServerFeedObserver(), this);
			addNotifierObserver(getBrokerFeedObserver(), this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public synchronized void update(Observable observable, Object arg1) {

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

		public synchronized void notifyObservers(Integer count) {
			setChanged();
			super.notifyObservers(count);
		}

	}

	RelSrvBrkRatioUpdater relSrvBrkRatioUpdater = new RelSrvBrkRatioUpdater();

	protected class DelayedMessagesRatioNotifier extends Observable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observable#notifyObservers(java.lang.Object)
		 */
		public synchronized void notifyObservers(Integer delayedMessagesRatio) {
			setChanged();
			super.notifyObservers(delayedMessagesRatio);
		}

	}

	protected DelayedMessagesRatioNotifier delayedMessagesRatioNotifier = new DelayedMessagesRatioNotifier();

	protected class AverageUptodateRatioUpdater extends Observable implements Observer {

		int messages = messageFrameFragmentSize * fragments; // each ratio

		// must be
		// placed at a
		// seperate
		// index

		int messageCount = messages - 1;

		long[] messageUptodateFrame = new long[messages];

		public AverageUptodateRatioUpdater() {
			for ( int i = 0; i < messages; i++ ) {
				messageUptodateFrame[i] = 0;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public synchronized void update(Observable observable, Object uptodateRatio) {

			messageCount++;

			// upto limit of message-counter
			if ( messageCount == messages )
				messageCount = 0;

			// add current to appropriate array

			messageUptodateFrame[messageCount] = (Integer) uptodateRatio;

			int ratioSum = 0;
			int delayedMessages = 0;

			// calculate average uptodateRatio
			for ( int i = 0; i < messages; i++ ) {
				ratioSum += messageUptodateFrame[i];
				if ( messageUptodateFrame[i] < 100 )
					delayedMessages++;
			}

			synchronized ( statistics ) {

				setDelayedMessagesRatio(delayedMessages * 100 / messages);

				if ( messages != 0 )
					setAverageUptodateRatio(ratioSum / messages);
				else
					setAverageUptodateRatio(0);

				notifyObservers(getAverageUptodateRatio());

			}

		}

		public synchronized void notifyObservers(Integer count) {
			setChanged();
			super.notifyObservers(count);
		}

	}

	private AverageUptodateRatioUpdater averageUptodateRatioUpdater = new AverageUptodateRatioUpdater();

	protected class AverageMessageDelayRatioUpdater extends Observable implements Observer {

		int messages = messageFrameFragmentSize * fragments; // each ratio

		// must be
		// placed at a
		// seperate
		// index

		int messageCount = messages - 1;

		int[] messageDelayFrame = new int[messages];

		public AverageMessageDelayRatioUpdater() {
			for ( int i = 0; i < messages; i++ ) {
				messageDelayFrame[i] = 0;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public synchronized void update(Observable observable, Object messageDelayRatio) {

			messageCount++;

			// upto limit of message-counter
			if ( messageCount == messages )
				messageCount = 0;

			// add current to appropriate array

			messageDelayFrame[messageCount] = (Integer) messageDelayRatio;

			int ratioSum = 0;
			int delayedMessages = 0;

			// calculate average uptodateRatio
			for ( int i = 0; i < messages; i++ ) {
				if ( messageDelayFrame[i] > 0 ) {
					ratioSum += messageDelayFrame[i];
					delayedMessages++;
				}
			}

			synchronized ( statistics ) {

				if ( delayedMessages != 0 )
					setAverageMessageDelayRatio(ratioSum / delayedMessages);
				else
					setAverageMessageDelayRatio(0);

				notifyObservers(getAverageMessageDelayRatio());

			}

		}

		public synchronized void notifyObservers(Integer count) {
			setChanged();
			super.notifyObservers(count);
		}

	}

	private AverageMessageDelayRatioUpdater averageMessageDelayRatioUpdater = new AverageMessageDelayRatioUpdater();

	protected class RequestsInQueueObserver extends Observable implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1) {

			setRequestsInQueue((Long) arg1);
			notifyObservers(getRequestsInQueue());

		}

		public synchronized void notifyObservers(Long value) {
			setChanged();
			super.notifyObservers(value);
		}

	}

	private RequestsInQueueObserver requestsInQueueObserver = new RequestsInQueueObserver();

	protected class UnrepliedRequestsObserver extends Observable implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1) {

			setUnrepliedRequests((Long) arg1);
			notifyObservers(getUnrepliedRequests());

		}

		public synchronized void notifyObservers(Long value) {
			setChanged();
			super.notifyObservers(value);
		}

	}

	private UnrepliedRequestsObserver unrepliedRequestsObserver = new UnrepliedRequestsObserver();

	protected class TotalTemporaryRequestsObserver extends Observable implements Observer {

		public void update(Observable arg0, Object value) {

			setTotalTemporaryRequests((Long) value);
			notifyObservers(getTotalTemporaryRequests());

		}

		public synchronized void notifyObservers(Long value) {
			setChanged();
			super.notifyObservers(value);
		}

	}

	private TotalTemporaryRequestsObserver totalTemporaryRequestsObserver = new TotalTemporaryRequestsObserver();

	protected class CurrPollPeriodsObserver extends Observable implements Observer {

		public void update(Observable observable, Object arg1) {

			Long value = (Long) arg1;
			PubSubType pubsub = ((PubSubNodeStatistics.CurrPollPeriodNotifier) observable).getPubSubNodeStatistics().getNode();
			currPollPeriods.set(Engine.getPubsubindizes().get(pubsub), value);
			notifyObservers(value);

		}

		public synchronized void notifyObservers(Long value) {
			setChanged();
			super.notifyObservers(value);
		}

	}

	private CurrPollPeriodsObserver currPollPeriodsObserver = new CurrPollPeriodsObserver();

	protected class StdDevCPPUpdater extends Observable implements Observer {

		public void update(Observable arg0, Object arg1) {

			Long meanValue = calcMeanValue();

			setStdDevCurrPollPeriods(calcStandardDeviation(meanValue) / 1000);

			Long meanValueSecs = meanValue / 1000;

			setMeanValueCurrPollPeriods(meanValueSecs);

			if ( meanValueSecs > 0 )
				setCoeffVarCurrPollPeriods(getStdDevCurrPollPeriods() / (meanValue / 1000F));
			else
				setCoeffVarCurrPollPeriods(0F);

		}

		private Long calcMeanValue() {

			long sum = 0;

			for ( Long value : currPollPeriods ) {
				sum += value;
			}

			return (sum / currPollPeriods.size());

		}

		private Long calcStandardDeviation(Long meanValue) {

			long dvtsum = 0;

			for ( Long value : currPollPeriods )
				dvtsum += Math.abs(value - meanValue);

			return (dvtsum / currPollPeriods.size());

		}

		public synchronized void notifyObservers(Long value) {
			setChanged();
			super.notifyObservers(value);
		}

	}

	private StdDevCPPUpdater stdDevCPPUpdater = new StdDevCPPUpdater();

	private class CoeffOfVarCPPUpdater extends Observable {

		@Override
		public synchronized void notifyObservers(Object value) {
			setChanged();
			super.notifyObservers(value);
		}

	}

	private CoeffOfVarCPPUpdater coeffOfVarCPPUpdater = new CoeffOfVarCPPUpdater();

	private class MeanValueCPPUpdater extends Observable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observable#notifyObservers(java.lang.Object)
		 */
		@Override
		public synchronized void notifyObservers(Object arg0) {
			setChanged();
			super.notifyObservers(arg0);
		}

	}

	private MeanValueCPPUpdater meanValueCPPUpdater = new MeanValueCPPUpdater();

	/**
	 * @author Friedemann Zintel
	 * 
	 * Just a wrapper for easy but synchronized handling of the
	 * GnuplotProviders. This class should NOT be taken for data-calculation or
	 * for notifying observers d’rectly. It's really just a wrapper for other
	 * GnuplotDataProviders, meant for observers only to register and
	 * deregister. And: don't send EVER a message to an object of this class.
	 * 
	 */
	public static class GnuplotProviderNotifier extends AbstractGnuplotDataProvider {

		protected Observable notifier;

		protected AbstractGnuplotDataProvider provider;

		public GnuplotProviderNotifier(Observable notifier, AbstractGnuplotDataProvider provider) throws Exception {

			super(Engine.getSingleton().getParams());

			this.notifier = notifier;
			this.provider = provider;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observable#addObserver(java.util.Observer)
		 */
		@Override
		public synchronized void addObserver(Observer observer) {
			addGnuplotDataProviderObserver(notifier, provider, observer);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observable#deleteObserver(java.util.Observer)
		 */
		@Override
		public synchronized void deleteObserver(Observer observer) {
			deleteGnuplotDataProviderObserver(notifier, provider, observer);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see AbstractGnuplotDataProvider#countObservers()
		 */
		@Override
		public int countObservers() {
			return provider.countObservers();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see AbstractGnuplotDataProvider#receiveMessage(Message)
		 */
		@Override
		protected void receiveMessage(Message m) {
			System.err.println("WARNING: calling GnuplotProviderNotifier.receiveMessage() not allowed!");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see AbstractGnuplotDataProvider#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		@Override
		public synchronized void update(Observable arg0, Object value) {
			System.err.println("WARNING: calling GnuplotProviderNotifier.update() not allowed!");
		}

		@Override
		protected void incData(Object value) {
			System.err.println("WARNING: calling GnuplotProviderNotifier.incData() not allowed!");
		}

		@Override
		protected void notifyObservers() {
			System.err.println("WARNING: calling GnuplotProviderNotifier.notifyObservers() not allowed!");
		}

		@Override
		protected void resetData() {
			System.err.println("WARNING: calling GnuplotProviderNotifier.resetData() not allowed!");
		}

	}

	public RPSStatistics(SimParameters params) throws Exception {

		this.params = params;

		getReOmRatioUpdater().addToObservables();
		getSrvBrkRatioUpdater().addToObservables();
		getRelReOmRatioUpdater().addToObservables();
		getRelSrvBrkRatioUpdater().addToObservables();
		getCurrPollPeriodsObserver().addObserver(getStdDevCPPUpdater());

		gnuplotproviderTotTempReq = new GnuplotLongProvider(params);
		gnuplotproviderMeanValueCPP = new GnuplotLongProvider(params);
		gnuplotproviderMeanDvtCPP = new GnuplotLongProvider(params);
		gnuplotproviderMeanDvtCoeffCPP = new GnuplotFloatProvider(params);
		gnuplotproviderAverageMessageDelayRatio = new GnuplotIntegerProvider(params);
		gnuplotproviderRelReOmRatio = new GnuplotIntegerProvider(params);
		gnuplotproviderAverageUptodateRatio = new GnuplotIntegerProvider(params);

	}

	private static void addGnuplotDataProviderObserver(Observable notifier, AbstractGnuplotDataProvider provider, Observer observer) {

		synchronized ( provider ) {

			provider.addObserver(observer);

			if ( provider.countObservers() == 1 )
				addNotifierObserver(notifier, provider);

		}
	}

	private static void deleteGnuplotDataProviderObserver(Observable notifier, AbstractGnuplotDataProvider provider, Observer observer) {

		synchronized ( provider ) {

			provider.deleteObserver(observer);

			if ( provider.countObservers() == 0 )
				deleteNotifierObserver(notifier, provider);

		}

	}

	public static synchronized void addNotifierObserver(Observable notifier, Observer observer) {

		synchronized ( notifier ) {
			notifier.addObserver(observer);
		}
	}

	public static void deleteNotifierObserver(Observable notifier, Observer observer) {

		synchronized ( notifier ) {
			notifier.deleteObserver(observer);
		}

	}

	public void addTotTempReqGnuplotDataProviderObserver(Observer observer) {
		addGnuplotDataProviderObserver(getTotalTemporaryRequestsObserver(), gnuplotproviderTotTempReq, observer);
	}

	public void addMeanValueCPPGnuplotDataProviderObserver(Observer observer) {
		addGnuplotDataProviderObserver(getMeanValueCPPUpdater(), gnuplotproviderMeanValueCPP, observer);
	}

	public void addMeanDvtCPPGnuplotDataProviderObserver(Observer observer) {
		addGnuplotDataProviderObserver(getStdDevCPPUpdater(), gnuplotproviderMeanDvtCPP, observer);
	}

	public void addMeanDvtCoeffCPPGnuplotDataProviderObserver(Observer observer) {
		addGnuplotDataProviderObserver(getCoeffOfVarCPPUpdater(), gnuplotproviderMeanDvtCoeffCPP, observer);
	}

	public void addAverageMessageDelayRatioGnuplotDataProviderObserver(Observer observer) {
		addGnuplotDataProviderObserver(getAverageMessageDelayRatioUpdater(), gnuplotproviderAverageMessageDelayRatio, observer);
	}

	public void addRelReOmRatioGnuplotDataProviderObserver(Observer observer) {
		addGnuplotDataProviderObserver(getRelReOmRatioUpdater(), gnuplotproviderRelReOmRatio, observer);
	}

	public void addAverageUptodateRatioGnuplotDataProviderObserver(Observer observer) {
		addGnuplotDataProviderObserver(getAverageUptodateRatioUpdater(), gnuplotproviderAverageUptodateRatio, observer);
	}

	public void deleteTotTempReqGnuplotDataProviderObserver(Observer observer) {
		deleteGnuplotDataProviderObserver(getTotalTemporaryRequestsObserver(), gnuplotproviderTotTempReq, observer);
	}

	public void deleteMeanValueCPPGnuplotDataProviderObserver(Observer observer) {
		deleteGnuplotDataProviderObserver(getMeanValueCPPUpdater(), gnuplotproviderMeanValueCPP, observer);
	}

	public void deleteMeanDvtCPPGnuplotDataProviderObserver(Observer observer) {
		deleteGnuplotDataProviderObserver(getStdDevCPPUpdater(), gnuplotproviderMeanDvtCPP, observer);
	}

	public void deleteMeanDvtCoeffCPPGnuplotDataProviderObserver(Observer observer) {
		deleteGnuplotDataProviderObserver(getCoeffOfVarCPPUpdater(), gnuplotproviderMeanDvtCoeffCPP, observer);
	}

	public void deleteAverageMessageDelayRatioGnuplotDataProviderObserver(Observer observer) {
		deleteGnuplotDataProviderObserver(getAverageMessageDelayRatioUpdater(), gnuplotproviderAverageMessageDelayRatio, observer);
	}

	public void deleteRelReOmRatioGnuplotDataProviderObserver(Observer observer) {
		deleteGnuplotDataProviderObserver(getRelReOmRatioUpdater(), gnuplotproviderRelReOmRatio, observer);
	}

	public void deleteAverageUptodateRatioGnuplotDataProviderObserver(Observer observer) {
		deleteGnuplotDataProviderObserver(getAverageUptodateRatioUpdater(), gnuplotproviderAverageUptodateRatio, observer);
	}

	public void startGnuplotRecording() {

		try {

			// write gnuplot-datas for total temporary requests
			gnuplotrecorderTotTempReq = new GnuplotDataRecorder(params.getGnuplotFileTotalTemporaryRequests(), params);
			addTotTempReqGnuplotDataProviderObserver(gnuplotrecorderTotTempReq);

			// write gnuplot-datas for meanValue of current polling rates
			gnuplotrecorderMeanValueCPP = new GnuplotDataRecorder(params.getGnuplotFileMeanValueCPP(), params);
			addMeanValueCPPGnuplotDataProviderObserver(gnuplotrecorderMeanValueCPP);

			// write gnuplot-datas for mean-deviation of current polling rates
			gnuplotrecorderMeanDvtCPP = new GnuplotDataRecorder(params.getGnuplotFileStdDevCPP(), params);
			addMeanDvtCPPGnuplotDataProviderObserver(gnuplotrecorderMeanDvtCPP);

			// write gnuplot-datas for mean-deviation-coefficient of current
			// polling rates
			gnuplotrecorderMeanDvtCoeffCPP = new GnuplotDataRecorder(params.getGnuplotFileCoeffVarCPP(), params);
			addMeanDvtCoeffCPPGnuplotDataProviderObserver(gnuplotrecorderMeanDvtCoeffCPP);

			// write gnuplot-datas for average-message-delay-ratio
			gnuplotrecorderAverageMessageDelayRatio = new GnuplotDataRecorder(params.getGnuplotFileAvgMsgDelayRatio(), params);
			addAverageMessageDelayRatioGnuplotDataProviderObserver(gnuplotrecorderAverageMessageDelayRatio);

			// write gnuplot-datas for relative omitted-requests-ratio
			gnuplotrecorderRelReOmRatio = new GnuplotDataRecorder(params.getGnuplotFileRelReOmRatio(), params);
			addRelReOmRatioGnuplotDataProviderObserver(gnuplotrecorderRelReOmRatio);

			// write gnuplot-datas for average uptodate-ratio
			gnuplotrecorderAverageUptodateRatio = new GnuplotDataRecorder(params.getGnuplotFileAvgUptodateRatio(), params);
			addAverageUptodateRatioGnuplotDataProviderObserver(gnuplotrecorderAverageUptodateRatio);

			// write queuesize
			FileWriter queuewriter = new FileWriter(params.getGnuplotFileQueueSize());
			queuewriter.write("queue=" + params.getServerQueueSize() + "\n");
			queuewriter.close();

		} catch ( Exception e ) {

			System.err.println("RPSStatistics.startGnuplotRecording(): Exception:");
			e.printStackTrace();

		}

	}

	public void stopGnuplotRecording() {

		try {

			if ( gnuplotrecorderTotTempReq != null ) {

				deleteTotTempReqGnuplotDataProviderObserver(gnuplotrecorderTotTempReq);
				gnuplotrecorderTotTempReq.close();
				gnuplotrecorderTotTempReq = null;

			}

			if ( gnuplotrecorderMeanValueCPP != null ) {

				deleteMeanValueCPPGnuplotDataProviderObserver(gnuplotrecorderMeanValueCPP);
				gnuplotrecorderMeanValueCPP.close();
				gnuplotrecorderMeanValueCPP = null;

			}

			if ( gnuplotrecorderMeanDvtCPP != null ) {

				deleteMeanDvtCPPGnuplotDataProviderObserver(gnuplotrecorderMeanDvtCPP);
				gnuplotrecorderMeanDvtCPP.close();
				gnuplotrecorderMeanDvtCPP = null;

			}

			if ( gnuplotrecorderMeanDvtCoeffCPP != null ) {

				deleteMeanDvtCoeffCPPGnuplotDataProviderObserver(gnuplotrecorderMeanDvtCoeffCPP);
				gnuplotrecorderMeanDvtCoeffCPP.close();
				gnuplotrecorderMeanDvtCoeffCPP = null;

			}

			if ( gnuplotrecorderAverageMessageDelayRatio != null ) {

				deleteAverageMessageDelayRatioGnuplotDataProviderObserver(gnuplotrecorderAverageMessageDelayRatio);
				gnuplotrecorderAverageMessageDelayRatio.close();
				gnuplotrecorderAverageMessageDelayRatio = null;

			}

			if ( gnuplotrecorderRelReOmRatio != null ) {

				deleteRelReOmRatioGnuplotDataProviderObserver(gnuplotrecorderRelReOmRatio);
				gnuplotrecorderRelReOmRatio.close();
				gnuplotrecorderRelReOmRatio = null;

			}
			if ( gnuplotrecorderAverageUptodateRatio != null ) {

				deleteAverageUptodateRatioGnuplotDataProviderObserver(gnuplotrecorderAverageUptodateRatio);
				gnuplotrecorderAverageUptodateRatio.close();
				gnuplotrecorderAverageUptodateRatio = null;

			}

		} catch ( Exception e ) {

			System.err.println("RPSStatistics.startGnuplotRecording(): Exception:");
			e.printStackTrace();

		}

	}

	public void addTotalTemporaryRequestsObserver(Observer observer) {
		addNotifierObserver(getTotalTemporaryRequestsObserver(), observer);
	}

	public void deleteTotalTemporaryRequestsObserver(Observer observer) {
		deleteNotifierObserver(getTotalTemporaryRequestsObserver(), observer);
	}

	public void addStdDevCPPObserver(Observer observer) {
		addNotifierObserver(getStdDevCPPUpdater(), observer);
	}

	public void deleteStdDevCPPObserver(Observer observer) {
		deleteNotifierObserver(getStdDevCPPUpdater(), observer);
	}

	public void addCoeffOfVarCPPObserver(Observer observer) {
		addNotifierObserver(getCoeffOfVarCPPUpdater(), observer);
	}

	public void deleteCoeffOfVarCPPObserver(Observer observer) {
		deleteNotifierObserver(getCoeffOfVarCPPUpdater(), observer);
	}

	public void addMeanValueCPPObserver(Observer observer) {
		addNotifierObserver(getMeanValueCPPUpdater(), observer);
	}

	public void deleteMeanValueCPPObserver(Observer observer) {
		deleteNotifierObserver(getMeanValueCPPUpdater(), observer);
	}

	public void addAverageMessageDelayRatioObserver(Observer observer) {
		addNotifierObserver(getAverageMessageDelayRatioUpdater(), observer);
	}

	public void deleteAverageMessageDelayRatioObserver(Observer observer) {
		deleteNotifierObserver(getAverageMessageDelayRatioUpdater(), observer);
	}

	public void addRelReOmRatioObserver(Observer observer) {
		addNotifierObserver(getRelReOmRatioUpdater(), observer);
	}

	public void deleteRelReOmRatioObserver(Observer observer) {
		deleteNotifierObserver(getRelReOmRatioUpdater(), observer);
	}

	public void addAverageUptodateRatioObserver(Observer observer) {
		addNotifierObserver(getAverageUptodateRatioUpdater(), observer);
	}

	public void deleteAverageUptodateRatioObserver(Observer observer) {
		deleteNotifierObserver(getAverageUptodateRatioUpdater(), observer);
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

	/**
	 * @return Returns the requestsInQueueObserver.
	 */
	public RequestsInQueueObserver getRequestsInQueueObserver() {
		return requestsInQueueObserver;
	}

	/**
	 * @return Returns the unrepliedRequestsObserver.
	 */
	public UnrepliedRequestsObserver getUnrepliedRequestsObserver() {
		return unrepliedRequestsObserver;
	}

	/**
	 * @return Returns the delayedMessagesRatioNotifier.
	 */
	public DelayedMessagesRatioNotifier getDelayedMessagesRatioNotifier() {
		return delayedMessagesRatioNotifier;
	}

	/**
	 * @return Returns the averageMessageDelayRatioUpdater.
	 */
	public AverageMessageDelayRatioUpdater getAverageMessageDelayRatioUpdater() {
		return averageMessageDelayRatioUpdater;
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

	/**
	 * @return Returns the averageMessageDelayRatio.
	 */
	public synchronized Integer getAverageMessageDelayRatio() {
		return averageMessageDelayRatio;
	}

	/**
	 * @param averageMessageDelayRatio
	 *            The averageMessageDelayRatio to set.
	 */
	public synchronized void setAverageMessageDelayRatio(Integer averageMessageDelayRatio) {
		this.averageMessageDelayRatio = averageMessageDelayRatio;
	}

	/**
	 * @return Returns the averageUptodateRatio.
	 */
	public synchronized Integer getAverageUptodateRatio() {
		return averageUptodateRatio;
	}

	/**
	 * @param averageUptodateRatio
	 *            The averageUptodateRatio to set.
	 */
	public synchronized void setAverageUptodateRatio(Integer averageUptodateRatio) {
		this.averageUptodateRatio = averageUptodateRatio;
	}

	/**
	 * @return Returns the averageUptodateRatioUpdater.
	 */
	public synchronized AverageUptodateRatioUpdater getAverageUptodateRatioUpdater() {
		return averageUptodateRatioUpdater;
	}

	/**
	 * @return Returns the delayedMessagesRatio.
	 */
	public synchronized Integer getDelayedMessagesRatio() {
		return delayedMessagesRatio;
	}

	/**
	 * @param delayedMessagesRatio
	 *            The delayedMessagesRatio to set.
	 */
	public synchronized void setDelayedMessagesRatio(Integer delayedMessagesRatio) {
		this.delayedMessagesRatio = delayedMessagesRatio;
		getDelayedMessagesRatioNotifier().notifyObservers(delayedMessagesRatio);
	}

	/**
	 * @return Returns the requestsInQueue.
	 */
	public Long getRequestsInQueue() {
		return requestsInQueue;
	}

	/**
	 * @param requestsInQueue
	 *            The requestsInQueue to set.
	 */
	public void setRequestsInQueue(Long requestsInQueue) {
		this.requestsInQueue = requestsInQueue;
	}

	/**
	 * @return Returns the unrepliedRequests.
	 */
	public long getUnrepliedRequests() {
		return unrepliedRequests;
	}

	/**
	 * @param unrepliedRequests
	 *            The unrepliedRequests to set.
	 */
	public void setUnrepliedRequests(long unrepliedRequests) {
		this.unrepliedRequests = unrepliedRequests;
	}

	/**
	 * @return Returns the totalTemporaryRequests.
	 */
	public synchronized Long getTotalTemporaryRequests() {
		return totalTemporaryRequests;
	}

	/**
	 * @param totalTemporaryRequests
	 *            The totalTemporaryRequests to set.
	 */
	public synchronized void setTotalTemporaryRequests(Long totalTemporaryRequests) {
		this.totalTemporaryRequests = totalTemporaryRequests;
	}

	/**
	 * @return Returns the totalTemporaryRequestsObserver.
	 */
	public synchronized TotalTemporaryRequestsObserver getTotalTemporaryRequestsObserver() {
		return totalTemporaryRequestsObserver;
	}

	/**
	 * @return Returns the currPollPeriods.
	 */
	public Vector<Long> getCurrPollPeriods() {
		return currPollPeriods;
	}

	/**
	 * @return Returns the currPollPeriodsObserver.
	 */
	public synchronized CurrPollPeriodsObserver getCurrPollPeriodsObserver() {
		return currPollPeriodsObserver;
	}

	/**
	 * @return Returns the stdDevCPPUpdater.
	 */
	public synchronized StdDevCPPUpdater getStdDevCPPUpdater() {
		return stdDevCPPUpdater;
	}

	/**
	 * @return Returns the stdDevCurrPollPeriods.
	 */
	public synchronized Long getStdDevCurrPollPeriods() {
		return stdDevCurrPollPeriods;
	}

	/**
	 * @param stdDevCurrPollPeriods
	 *            The stdDevCurrPollPeriods to set.
	 */
	protected synchronized void setStdDevCurrPollPeriods(Long stdDevCurrPollPeriods) {
		this.stdDevCurrPollPeriods = stdDevCurrPollPeriods;
		getStdDevCPPUpdater().notifyObservers(getStdDevCurrPollPeriods());
	}

	/**
	 * @return Returns the coeffVarCurrPollPeriods.
	 */
	public synchronized Float getCoeffVarCurrPollPeriods() {
		return coeffVarCurrPollPeriods;
	}

	/**
	 * @param coeffVarCurrPollPeriods
	 *            The coeffVarCurrPollPeriods to set.
	 */
	public synchronized void setCoeffVarCurrPollPeriods(Float coeffVarCurrPollPeriods) {
		this.coeffVarCurrPollPeriods = coeffVarCurrPollPeriods;
		getCoeffOfVarCPPUpdater().notifyObservers(getCoeffVarCurrPollPeriods());
	}

	/**
	 * @return Returns the coeffOfVarCPPUpdater.
	 */
	public synchronized CoeffOfVarCPPUpdater getCoeffOfVarCPPUpdater() {
		return coeffOfVarCPPUpdater;
	}

	/**
	 * @return Returns the meanValueCurrPollPeriods.
	 */
	public synchronized Long getMeanValueCurrPollPeriods() {
		return meanValueCurrPollPeriods;
	}

	/**
	 * @param meanValueCurrPollPeriods
	 *            The meanValueCurrPollPeriods to set.
	 */
	public synchronized void setMeanValueCurrPollPeriods(Long meanValueCurrPollPeriods) {
		this.meanValueCurrPollPeriods = meanValueCurrPollPeriods;
		getMeanValueCPPUpdater().notifyObservers(getMeanValueCurrPollPeriods());
	}

	/**
	 * @return Returns the meanValueCPPUpdater.
	 */
	public synchronized MeanValueCPPUpdater getMeanValueCPPUpdater() {
		return meanValueCPPUpdater;
	}

	/**
	 * @return Returns the gnuplotproviderAverageMessageDelayRatio.
	 */
	public AbstractGnuplotDataProvider getGnuplotproviderAverageMessageDelayRatio() {
		return gnuplotproviderAverageMessageDelayRatio;
	}

	/**
	 * @return Returns the gnuplotproviderAverageUptodateRatio.
	 */
	public AbstractGnuplotDataProvider getGnuplotproviderAverageUptodateRatio() {
		return gnuplotproviderAverageUptodateRatio;
	}

	/**
	 * @return Returns the gnuplotproviderMeanDvtCoeffCPP.
	 */
	public AbstractGnuplotDataProvider getGnuplotproviderMeanDvtCoeffCPP() {
		return gnuplotproviderMeanDvtCoeffCPP;
	}

	/**
	 * @return Returns the gnuplotproviderMeanDvtCPP.
	 */
	public AbstractGnuplotDataProvider getGnuplotproviderMeanDvtCPP() {
		return gnuplotproviderMeanDvtCPP;
	}

	/**
	 * @return Returns the gnuplotproviderMeanValueCPP.
	 */
	public AbstractGnuplotDataProvider getGnuplotproviderMeanValueCPP() {
		return gnuplotproviderMeanValueCPP;
	}

	/**
	 * @return Returns the gnuplotproviderRelReOmRatio.
	 */
	public AbstractGnuplotDataProvider getGnuplotproviderRelReOmRatio() {
		return gnuplotproviderRelReOmRatio;
	}

	/**
	 * @return Returns the gnuplotproviderTotTempReq.
	 */
	public AbstractGnuplotDataProvider getGnuplotproviderTotTempReq() {
		return gnuplotproviderTotTempReq;
	}

}
