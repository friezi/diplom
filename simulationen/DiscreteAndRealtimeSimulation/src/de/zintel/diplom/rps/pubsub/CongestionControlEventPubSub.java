package de.zintel.diplom.rps.pubsub;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.zintel.diplom.feed.RSSEventFeedFactory;
import de.zintel.diplom.gui.InfoWindow;
import de.zintel.diplom.messages.InitialBrokerRSSFeedMessage;
import de.zintel.diplom.messages.NetworkSizeUpdateMessage;
import de.zintel.diplom.messages.RSSFeedMessage;
import de.zintel.diplom.messages.RSSFeedRichMessage;
import de.zintel.diplom.messages.SeqAckRSSFeedMessage;
import de.zintel.diplom.messages.SequencedRSSFeedRequestMessage;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.SimParameters;

/**
 * 
 */

/**
 * This PubSub does congestion-control. It checks the server-overload and
 * adjusts its polling-behaviour.
 * 
 * @author Friedemann Zintel
 * 
 */
public class CongestionControlEventPubSub extends EventPubSub {

	protected static class LongComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {

			if ( ((Long) arg0) < ((Long) arg1) )
				return -1;

			else if ( ((Long) arg0) > ((Long) arg1) )
				return 1;

			else
				return 0;

		}

	}

	protected long requestFeedTimerCounter = 0;

	/**
	 * The roundtrip-time between Subscriber and RSSServer
	 */
	private long rtt = 0;

	/**
	 * theb adjusted roundtrip-time
	 */
	private long artt = 0;

	protected Date requestFeedMessageDate;

	protected Date rssFeedMessageDate;

	/**
	 * roundtrip-timeout interval
	 */
	protected long rto = 0;

	/**
	 * the initial-cpp will be used by the Retransmission-timer fro calculating
	 * the rtt. It will not conflict with a possible new setting of the cpp
	 */
	protected long icpp = 0;

	private boolean updateRTimer = false;

	protected long sequenceNumber = 0;

	protected long retransmissionSequenceNumber = 0;

	protected long feedSequenceNumber = 0;

	protected long feedRetransmissionSequenceNumber = 0;

	protected static final long checkId = /* 982 */Long.MAX_VALUE;

	protected TreeMap<Long, Date> retransmissionSequence = new TreeMap<Long, Date>(new LongComparator());

	/**
	 * @param xp
	 * @param yp
	 * @param rssEventFeedFactory
	 * @param params
	 */
	public CongestionControlEventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {
		super(xp, yp, rssEventFeedFactory, params);
		setMoreinfo(true);
	}

	@Override
	public void init() {

		super.init();

	}

	@Override
	public void reset() {

		super.reset();
		retransmissionSequence.clear();
		resetFeedRetransmissionSequenceNumber();
		resetFeedSequenceNumber();
		resetRetransmissionSequenceNumber();
		resetSequenceNumber();
		resetRequestFeedTimerCounter();
		updateRTimer = false;
		resetIcpp();
		resetRto();
		resetRtt();
		resetArtt();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#init()
	 */
	@Override
	public void start() {

		requestFeedMessageDate = new Date(getEngine().getTime());

		resetRtt();
		setArtt(getRtt());
		setCpp(getArtt());
		setIcpp(getCpp());
		resetRto();
		resetRequestFeedTimerCounter();
		incSequenceNumber();
		resetRetransmissionSequenceNumber();
		resetFeedSequenceNumber();
		resetFeedRetransmissionSequenceNumber();
		super.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#handleNetworkSizeUpdateMessage(NetworkSizeUpdateMessage)
	 */
	@Override
	protected void handleNetworkSizeUpdateMessage(NetworkSizeUpdateMessage nsum) {
		// don't handle this anymore
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#handleRequestFeedMessage(PubSub.RequestFeedMessage)
	 */
	@Override
	protected void handleRequestFeedMessage(RequestFeedMessage rfm) {

		// incRtt();
		setRtt(getRto());
		setArtt(getRtt());
		incRequestFeedTimerCounter();
		incRto();
		updateFeedRequestTimer(getRto());
		requestFeedMessageDate = new Date(getEngine().getTime());

		// if ( getId() == checkId )
		// System.out.println("rto: " + getRto() + " Icpp:" + getIcpp() + " R" +
		// requestFeedTimerCounter + ":" + requestFeedMessageDate.getTime());

		incRetransmissionSequenceNumber();
		retransmissionSequence.put(retransmissionSequenceNumber, requestFeedMessageDate);

		super.handleRequestFeedMessage(rfm);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see EventPubSub#handleRSSFeedMessage(RSSFeedMessage)
	 */
	@Override
	protected void handleRSSFeedMessage(RSSFeedMessage fm) {

		rssFeedMessageDate = new Date(getEngine().getTime());

		if ( fm instanceof SeqAckRSSFeedMessage ) {

			SeqAckRSSFeedMessage sefm = (SeqAckRSSFeedMessage) fm;

			feedSequenceNumber = sefm.getSequenceNumber();
			feedRetransmissionSequenceNumber = sefm.getRetransmissionSequenceNumber();

		}
		super.handleRSSFeedMessage(fm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#requestFeed()
	 */
	@Override
	protected void requestFeed() {

		if ( getRssServer() != null )
			new SequencedRSSFeedRequestMessage(this, getRssServer(), sequenceNumber, retransmissionSequenceNumber, params).send();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#calculateInterval()
	 */
	@Override
	protected long calculateTimeToRefreshInterval() {

		Date now = new Date(getEngine().getTime());
		Date feedDate = getFeed().getGeneralContent().getLastBuiltDate();
		int ttl = getFeed().getGeneralContent().getTtl();
		int diffsecs = (int) ((now.getTime() - feedDate.getTime()) / 1000);
		if ( diffsecs > ttl )
			diffsecs = ttl;

		long cppSecs = getCpp() / 1000;

		return (long) ((Engine.getSingleton().getRandom().nextFloat() * cppSecs + (ttl - diffsecs)) * 1000);
	}

	protected void updateFeedRequestTimer() {

		feedRequestTask.cancel();
		purgeFeedRequestTimer();
		feedRequestTask = new FeedRequestTask(this, params);
		if ( getCpp() < 0 )
			System.err.println("WARNING: CongestionControlEventPubSub.updateRequestTimer(): getRtt() is negative: " + getCpp());
		feedRequestTimer.schedule(feedRequestTask, getCpp());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimer(long)
	 */
	@Override
	protected synchronized void updateFeedRequestTimer(long interval) {

		feedRequestTask.cancel();
		purgeFeedRequestTimer();
		feedRequestTask = new FeedRequestTask(this, params);
		if ( interval < 0 )
			System.err.println("WARNING: CongestionControlEventPubSub.updateRequestTimer(long): interval is negative: " + interval);
		feedRequestTimer.schedule(feedRequestTask, interval);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#getFeedRequestTimerPeriod()
	 */
	@Override
	protected long getFeedRequestTimerPeriod() {
		return getArtt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimerByOldFeedFromBroker()
	 */
	@Override
	protected synchronized void updateFeedRequestTimerByOldFeedFromBroker() {

		if ( updateRTimer == true ) {

			// if a measurement of the roundtrip-time is in progress, don't stop
			// it
			if ( requestFeedTimerCounter <= 1 ) {

				// if one is running aready (must be!) take the shortest timeout
				long interval = calculateTimeToRefreshInterval();

				if ( interval < feedRequestTask.getNextExecutionTime() - getEngine().getTime() )
					updateFeedRequestTimer(interval);

			}

			updateRTimer = false;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimerByNewFeedFromBroker()
	 */
	@Override
	protected synchronized void updateFeedRequestTimerByNewFeedFromBroker() {

		if ( updateRTimer == true ) {

			// THE FOLLOWING COMMMENTED LINES ARE A NICE IDEA BUT END UP IN
			// FATAL RESULTS!!!
			// // on a new feed, we stop an on-going measurement of
			// roundtrip-time
			// // because we got an rtt within the feed
			// resetRequestFeedTimerCounter();
			// resetRto();
			// setInitialCpp(getCpp());

			// if one is running aready (must be!) take the shortest timeout
			long interval = calculateTimeToRefreshInterval();

			if ( interval < 0 )
				System.err.println("WARNING!!!: CongestionControlEventPubSub.updateRequestTimerByNewFeedFromBroker(): interval is negative: " + interval);

			if ( interval < feedRequestTask.getNextExecutionTime() - getEngine().getTime() )
				updateFeedRequestTimer(interval);

			updateRTimer = false;

		}

	}

	protected synchronized void updateFeedRequestTimerByInitialBrokerFeed() {

		if ( updateRTimer == true ) {

			// if one is running aready (must be!) take the shortest timeout
			long interval = calculateTimeToRefreshInterval();

			if ( interval < 0 )
				System.err.println("WARNING!!!: CongestionControlEventPubSub.updateRequestTimerByNewFeedFromBroker(): interval is negative: " + interval);

			// update should be made in any case
			updateFeedRequestTimer(interval);

			updateRTimer = false;

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimerByNewFeedFromServer()
	 */
	@Override
	protected synchronized void updateFeedRequestTimerByNewFeedFromServer() {

		if ( feedSequenceNumber == sequenceNumber ) {

			recalculateRtt();
			recalculateArtt();
			setCpp(getArtt());
			setIcpp(getCpp());
			// if ( getId() == checkId )
			// System.out.println("icpp:" + getIcpp());
			resetRequestFeedTimerCounter();
			resetRto();
			resetRetransmissionSequenceNumber();
			resetRetransmissionSequence();
			incSequenceNumber();

		}
		updateFeedRequestTimer(calculateTimeToRefreshInterval());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimerByOldFeedFromServer()
	 */
	@Override
	protected synchronized void updateFeedRequestTimerByOldFeedFromServer() {

		if ( feedSequenceNumber == sequenceNumber ) {

			recalculateRtt();
			recalculateArtt();
			setCpp(getArtt());
			setIcpp(getCpp());
			// if ( getId() == checkId )
			// System.out.println("icpp:" + getIcpp());
			resetRequestFeedTimerCounter();
			resetRto();
			resetRetransmissionSequenceNumber();
			resetRetransmissionSequence();
			incSequenceNumber();

		}

		updateFeedRequestTimer(getCpp());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see EventPubSub#addRichInformation(RSSFeedRichMessage)
	 */
	@Override
	protected void addRichInformation(RSSFeedRichMessage rfrm) {

		super.addRichInformation(rfrm);
		rfrm.setArtt(getArtt());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see EventPubSub#storeRichInformation(RSSFeedRichMessage)
	 */
	@Override
	protected void storeRichInformation(RSSFeedRichMessage rfrm) {

		super.storeRichInformation(rfrm);

		long sum;

		if ( Long.MAX_VALUE - rfrm.getArtt() < getArtt() )
			sum = Long.MAX_VALUE;
		else
			sum = rfrm.getArtt() + getArtt();

		setArtt(sum / 2);

		if ( getArtt() < 0 ) {
			System.err.println("WARNING!!!: CongestionControlEventPubSub.storeRichInformation: getArtt() is negative: " + getArtt());
			System.err.println("rfrm.getArtt(): " + rfrm.getArtt() + " sum: " + sum);
		}

		setCpp(getArtt());

		if ( getCpp() < getIcpp() )
			setIcpp(getCpp());

		updateRTimer = true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see EventPubSub#storeInitialBrokerInformation(InitialBrokerRSSFeedMessage)
	 */
	@Override
	protected void storeInitialBrokerInformation(InitialBrokerRSSFeedMessage ibrm) {

		super.storeInitialBrokerInformation(ibrm);

		// Churn-compensation:

		// if we are not the first subscriber in the local area, we will take
		// over the already measured rtt,
		// otherwise, we will calculate it by ourself
		if ( ibrm.getNumberOfSubscribers() < 2 )
			return;

		if ( ibrm.getArtt() < 1 )
			return;

		setArtt(ibrm.getArtt());
		if ( getArtt() < 0 ) {
			System.err.println("WARNING!!!: CongestionControlEventPubSub.storeInitialBrokerInformation: getRtt() is negative: " + getArtt());
			System.err.println("rfrm.getRtt(): " + ibrm.getArtt());
		}

		setCpp(getArtt());
		setIcpp(getCpp());

		updateRTimer = true;

		updateFeedRequestTimerByInitialBrokerFeed();

	}

	protected void recalculateRtt() {

		// if the counter is 0, the Feed is from an old sequence, calculation
		// would be faulty
		if ( requestFeedTimerCounter == 0 )
			return;

		if ( feedSequenceNumber != sequenceNumber )
			return;

		try {

			// long delta_t = rssFeedMessageDate.getTime() -
			// retransmissionSequence.get(feedRetransmissionSequenceNumber).getTime();

			// long delta_t = (rssFeedMessageDate.getTime() -
			// retransmissionSequence.get(1L).getTime())/feedRetransmissionSequenceNumber;
			long delta_t = (rssFeedMessageDate.getTime() - retransmissionSequence.get(1L).getTime());
			//
			// if ( getId() == checkId ) {
			// System.out
			// .print("oldValue: "
			// + ((float) ((getIcpp() * sum2pot(1, requestFeedTimerCounter - 1)
			// / requestFeedTimerCounter + (rssFeedMessageDate.getTime() -
			// requestFeedMessageDate
			// .getTime())))));
			// System.out.print(" newValue: " + (((float) delta_t)));
			// System.out.print(" Icpp: " + (getIcpp()) + " rtc: " +
			// requestFeedTimerCounter + " rsn: " +
			// retransmissionSequence.size());
			// for ( long i = 1; i <= retransmissionSequence.size(); i++ )
			// System.out.print(" T" + i + ":" + (rssFeedMessageDate.getTime() -
			// retransmissionSequence.get(i).getTime()));
			// System.out.println();
			// }

			setRtt(delta_t);

		} catch ( NullPointerException e ) {
			// to prevent double retransmission-acks
			System.err.println("NullPointerException in recalculateRtt()");
			System.err.println("Elements: ");
			for ( Long key : retransmissionSequence.keySet() )
				System.err.println("key: " + key + "  value: " + retransmissionSequence.get(key));
		}

	}

	protected void recalculateArtt() {
		// if (getId()==checkId)
		// System.out.println("rtc: "+requestFeedTimerCounter+" ld: " +
		// ld(requestFeedTimerCounter + 1));
		setArtt((long) (Math.pow(2, requestFeedTimerCounter - 1) * getRtt()));
	}

	protected double ld(double x) {
		return Math.log(x) / Math.log(2);
	}

	//
	// protected void incRtt() {
	// // setRtt(getRtt() + getPreferredPollingRateMillis());
	// setRtt(2 * getRtt());
	// }

	protected void incRto() {
		setRto((long) (Math.pow(2, requestFeedTimerCounter) * getIcpp()));
	}

	protected void resetRtt() {
		// setRtt(0);
		setRtt(getPpp());
	}
	
	protected void resetArtt(){
		setArtt(getPpp());
	}

	protected void resetRetransmissionSequenceNumber() {
		retransmissionSequenceNumber = 0;
	}

	protected void resetSequenceNumber() {
		sequenceNumber = 0;
	}

	protected void resetRetransmissionSequence() {
		retransmissionSequence.clear();
	}

	protected void resetFeedSequenceNumber() {
		feedSequenceNumber = 0;
	}

	protected void resetFeedRetransmissionSequenceNumber() {
		feedRetransmissionSequenceNumber = 0;
	}

	protected void incRetransmissionSequenceNumber() {

		if ( retransmissionSequenceNumber == Long.MAX_VALUE )
			retransmissionSequenceNumber = 0;
		else
			retransmissionSequenceNumber++;

	}

	protected void incSequenceNumber() {

		if ( sequenceNumber == Long.MAX_VALUE )
			sequenceNumber = 0;
		else
			sequenceNumber++;

	}
	
	protected void resetIcpp(){
		setIcpp(getCpp());
	}

	protected void resetRto() {
		setRto(getIcpp());
	}

	protected long sum2pot(long start, long n) {

		long sum = 0;

		for ( long k = start; k <= n; k++ )
			sum += k * Math.pow(2, k);

		return sum;
	}

	protected void incRequestFeedTimerCounter() {
		requestFeedTimerCounter++;
	}

	protected void resetRequestFeedTimerCounter() {
		requestFeedTimerCounter = 0;
	}

	/**
	 * @return Returns the rtt.
	 */
	public synchronized long getRtt() {
		return rtt;
	}

	/**
	 * @param rtt
	 *            The rtt to set.
	 */
	public synchronized void setRtt(long rtt) {

		// the rtt-value must not get negative. If it is negative then because
		// it exceeded the
		// Long.MAX_VALUE
		if ( rtt <= 0 )
			this.rtt = Long.MAX_VALUE;
		else
			this.rtt = rtt;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSubNode#setCpp(long)
	 */
	@Override
	public synchronized void setCpp(long currentPollingPeriod) {

		super.setCpp(Math.min(Math.max(currentPollingPeriod, getPpp()), getMpp()));
		getStatistics().updateCurrPollPeriod(new Long(getCpp()));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSubNode#changeCpp(long)
	 */
	@Override
	public void callbackResetCpp() {

		resetRtt();
		setArtt(getRtt());
		setCpp(getArtt());
		resetRequestFeedTimerCounter();
		updateFeedRequestTimer();

		super.callbackResetCpp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RSSServerNode#showInfo()
	 */
	@Override
	public void showInfo() {
		super.showInfo();
		new InfoWindowExtension(infoWindow);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#showMoreInfo(InfoWindow)
	 */
	@Override
	public void showMoreInfo(InfoWindow moreinfowindow) {
		
		super.showMoreInfo(moreinfowindow);
		new MoreInfoWindowExtension(moreinfowindow);
		
	}

	protected class InfoWindowExtension implements ChangeListener {

		private class CloseWindowAdapter extends WindowAdapter {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosed(WindowEvent arg0) {
				deletePreferredPollingPeriodObserver(preferredPollingPeriodObserver);
				super.windowClosed(arg0);
			}

		}

		protected class PreferredPollingPeriodObserver implements Observer {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Observer#update(java.util.Observable,
			 *      java.lang.Object)
			 */
			public void update(Observable o, Object arg) {

				Integer value = (Integer) arg;

				sliderlabel.setText("preferred polling-period: " + value);
			}

		}

		int maxValue = 60;

		InfoWindow baseWindow;;

		JLabel sliderlabel;

		protected PreferredPollingPeriodObserver preferredPollingPeriodObserver = new PreferredPollingPeriodObserver();

		public InfoWindowExtension(InfoWindow baseWindow) {

			this.baseWindow = baseWindow;

			JPanel sliderpanel = new JPanel();

			sliderpanel.setLayout(new BoxLayout(sliderpanel, BoxLayout.Y_AXIS));

			JSlider slider;

			Hashtable<Integer, JLabel> labeltable = new Hashtable<Integer, JLabel>();

			labeltable.put(1, new JLabel("1"));

			for ( int i = 10; i <= maxValue; i += 10 )
				labeltable.put(i, new JLabel(new Integer(i).toString()));

			slider = new JSlider(1, maxValue, (int) (((PubSubNode) baseWindow.getNode()).getPppSecs()));
			slider.setLabelTable(labeltable);
			slider.addChangeListener(this);
			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);

			sliderlabel = new JLabel();
			sliderlabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			sliderlabel.setLabelFor(slider);
			sliderpanel.add(sliderlabel);
			sliderpanel.add(slider);
			sliderpanel.setBorder(BorderFactory.createEtchedBorder());

			baseWindow.addWindowListener(new CloseWindowAdapter());

			addPreferredPollingPeriodObserver(preferredPollingPeriodObserver);
			preferredPollingPeriodObserver.update(getPreferredPollingPeriodNotifier(), getPppSecs());

			JPanel basepanel = (JPanel) baseWindow.getContentPane();

			basepanel.setLayout(new BoxLayout(basepanel, BoxLayout.Y_AXIS));
			basepanel.add(sliderpanel);

			baseWindow.pack();

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {
			JSlider slider = (JSlider) e.getSource();

			if ( slider.getValueIsAdjusting() == false ) {
				setPppSecs(slider.getValue());
				setRtt(getPpp());
				setArtt(getRtt());
				setCpp(getArtt());
				updateFeedRequestTimer(getPpp());
			}

		}

	}

	protected class MoreInfoWindowExtension {

		private class CloseWindowAdapter extends WindowAdapter {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosed(WindowEvent arg0) {
				node.getStatistics().deleteCurrPollPeriodObserver(currPollPeriodObserver);
				super.windowClosed(arg0);
			}

		}

		private class CurrPollPeriodObserver implements Observer {

			public void update(Observable observable, Object arg1) {

				Long rftv = (Long) arg1;

				currPollPeriodLabel.setText("current polling-period: " + rftv / 1000 + "    ");
			}

		}

		private class UptodateRatioObserver implements Observer {

			public void update(Observable observable, Object arg1) {

				Integer udr = (Integer) arg1;

				uptodateRatioLabel.setText("uptodate ratio: " + udr + " %");
			}

		}

		private class MessageDelayRatioObserver implements Observer {

			public void update(Observable observable, Object arg1) {

				Integer mdr = (Integer) arg1;

				messageDelayRatioLabel.setText("message-delay ratio: " + mdr + " %");
			}

		}

		JLabel currPollPeriodLabel = new JLabel();

		JLabel uptodateRatioLabel = new JLabel();

		JLabel messageDelayRatioLabel = new JLabel();

		CurrPollPeriodObserver currPollPeriodObserver = new CurrPollPeriodObserver();

		UptodateRatioObserver uptodateRatioObserver = new UptodateRatioObserver();

		MessageDelayRatioObserver messageDelayRatioObserver = new MessageDelayRatioObserver();

		InfoWindow moreinfowindow;

		CongestionControlEventPubSub node = null;

		public MoreInfoWindowExtension(InfoWindow moreinfowindow) {

			this.moreinfowindow = moreinfowindow;
			this.node = (CongestionControlEventPubSub) moreinfowindow.getNode();

			node.getStatistics().addCurrPollPeriodObserver(currPollPeriodObserver);
			node.getStatistics().addUptodateRatioObserver(uptodateRatioObserver);
			node.getStatistics().addMessageDelayRatioObserver(messageDelayRatioObserver);

			currPollPeriodObserver.update(node.getStatistics().getCurrPollPeriodNotifier(), new Long(getCpp()));
			uptodateRatioObserver.update(node.getStatistics().getUptodateRatioNotifier(), node.getStatistics().getUptodateRatio());
			messageDelayRatioObserver.update(node.getStatistics().getMessageDelayRatioNotifier(), node.getStatistics().getMessageDelayRatio());

			moreinfowindow.addWindowListener(new CloseWindowAdapter());

			moreinfowindow.getContentPane().add(currPollPeriodLabel);
			moreinfowindow.getContentPane().add(uptodateRatioLabel);
			moreinfowindow.getContentPane().add(messageDelayRatioLabel);

			moreinfowindow.pack();

		}
	}

	/**
	 * @return Returns the rto.
	 */
	public synchronized long getRto() {
		return rto;
	}

	/**
	 * @param rto
	 *            The rto to set.
	 */
	public synchronized void setRto(long rto) {
		this.rto = rto;
	}

	/**
	 * @return Returns the initialCpp.
	 */
	public synchronized long getIcpp() {
		return icpp;
	}

	/**
	 * @param initialCpp
	 *            The initialCpp to set.
	 */
	public synchronized void setIcpp(long initialCpp) {
		this.icpp = initialCpp;
	}

	public synchronized long getArtt() {
		return artt;
	}

	public synchronized void setArtt(long artt) {

		if ( artt <= 0 )
			this.artt = Long.MAX_VALUE;
		else
			this.artt = artt;
	}

}
