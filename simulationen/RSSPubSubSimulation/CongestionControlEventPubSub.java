import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.border.*;

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

	private long requestFeedTimerCounter;

	/**
	 * The RoundTripTime between Subscriber and RSSServer
	 */
	private long rtt;

	private Date requestFeedMessageDate;

	private Date rssFeedMessageDate;

	private boolean updateRTimer = false;

	private RttNotifier rttNotifier = new RttNotifier();

	/**
	 * @param xp
	 * @param yp
	 * @param rssEventFeedFactory
	 * @param params
	 */
	public CongestionControlEventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {
		super(xp, yp, rssEventFeedFactory, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#init()
	 */
	@Override
	public void init() {
		setMoreinfo(true);
		resetRtt();
		resetRequestFeedTimerCounter();
		super.init();
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

		incRtt();
		incRequestFeedTimerCounter();
		updateRequestTimer();
		requestFeedMessageDate = new Date();
		super.handleRequestFeedMessage(rfm);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see EventPubSub#handleRSSFeedMessage(RSSFeedMessage)
	 */
	@Override
	protected void handleRSSFeedMessage(RSSFeedMessage fm) {

		rssFeedMessageDate = new Date();
		super.handleRSSFeedMessage(fm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#calculateInterval()
	 */
	@Override
	protected long calculateNextRequestTimeout() {

		Date now = new Date();
		Date feedDate = getFeed().getGeneralContent().getLastBuiltDate();
		int ttl = getFeed().getGeneralContent().getTtl();
		int diffsecs = (int) ((now.getTime() - feedDate.getTime()) / 1000);
		if ( diffsecs > ttl )
			diffsecs = ttl;

		long rtosecs = Math.max(getPreferredPollingRate(), getRtt()) / 1000;

		return (long) ((new Random().nextFloat() * rtosecs + (ttl - diffsecs)) * 1000);
	}

	protected void updateRequestTimer() {

		feedRequestTask.cancel();
		purgeFeedRequestTimer();
		feedRequestTask = new FeedRequestTask(this);
		long rftv = getRtt();
		feedRequestTimer.schedule(feedRequestTask, rftv, rftv);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimer(long)
	 */
	@Override
	protected synchronized void updateRequestTimer(long interval) {

		feedRequestTask.cancel();
		purgeFeedRequestTimer();
		feedRequestTask = new FeedRequestTask(this);
		feedRequestTimer.schedule(feedRequestTask, interval, getRtt());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimerByOldFeedFromBroker()
	 */
	@Override
	protected synchronized void updateRequestTimerByOldFeedFromBroker() {

		if ( updateRTimer == true ) {

			// if one is running aready (must be!) take the shortest timeout
			long interval = calculateNextRequestTimeout();

			if ( interval < feedRequestTask.getNextExecutionTime() - System.currentTimeMillis() )
				updateRequestTimer(interval);

			updateRTimer = false;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimerByNewFeedFromBroker()
	 */
	@Override
	protected synchronized void updateRequestTimerByNewFeedFromBroker() {

		if ( updateRTimer == true ) {

			// if one is running aready (must be!) take the shortest timeout
			long interval = calculateNextRequestTimeout();

			if ( interval < feedRequestTask.getNextExecutionTime() - System.currentTimeMillis() )
				updateRequestTimer(interval);

			updateRTimer = false;

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimerByNewFeedFromServer()
	 */
	@Override
	protected synchronized void updateRequestTimerByNewFeedFromServer() {

		recalculateRequestTimeoutinterval();
		resetRequestFeedTimerCounter();
		updateRequestTimer(calculateNextRequestTimeout());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimerByOldFeedFromServer()
	 */
	@Override
	protected synchronized void updateRequestTimerByOldFeedFromServer() {

		recalculateRequestTimeoutinterval();
		resetRequestFeedTimerCounter();
		updateRequestTimer();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see EventPubSub#addRichInformation(RSSFeedRichMessage)
	 */
	@Override
	protected void addRichInformation(RSSFeedRichMessage rfrm) {
		super.addRichInformation(rfrm);
		rfrm.setRtt(getRtt());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see EventPubSub#storeRichInformation(RSSFeedRichMessage)
	 */
	@Override
	protected void storeRichInformation(RSSFeedRichMessage rfrm) {
		super.storeRichInformation(rfrm);

		long newRTT = (rfrm.getRtt() + getRtt()) / 2;
		setRtt(newRTT);
		updateRTimer = true;
	}

	protected void recalculateRequestTimeoutinterval() {

		long delta_t = (rssFeedMessageDate.getTime() - requestFeedMessageDate.getTime());

		if ( requestFeedTimerCounter > 1 ) {
			// we had to request several times -> set the timeout to meanvalue

			long rftv = getRtt();

			// setRtt((rftv * requestFeedTimerCounter + rftv) / 2);
			setRtt(getPreferredPollingRateMillis() * (((requestFeedTimerCounter * requestFeedTimerCounter) - 1) / 3) + delta_t);

		} else {

			if ( delta_t < getPreferredPollingRateMillis() )
				setRtt(getPreferredPollingRateMillis());
			else
				setRtt(delta_t);
		}

	}

	protected void incRtt() {
		setRtt(getRtt() + getPreferredPollingRateMillis());
	}

	protected void resetRtt() {
		setRtt(0);
	}

	protected void incRequestFeedTimerCounter() {
		requestFeedTimerCounter++;
	}

	protected void resetRequestFeedTimerCounter() {
		requestFeedTimerCounter = 0;
	}

	/**
	 * returns the preferredPollingRate in miliseconds
	 * 
	 * @return preferredPollingRate in miliseconds
	 */
	protected long getPreferredPollingRateMillis() {
		return getPreferredPollingRate() * 1000;
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
		this.rtt = rtt;
		rttNotifier.notifyObservers(new Long(rtt));
	}

	protected synchronized void addRttObserver(Observer observer) {
		rttNotifier.addObserver(observer);
	}

	protected synchronized void deleteRttObserver(Observer observer) {
		rttNotifier.deleteObserver(observer);
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
	protected void showMoreInfo(InfoWindow moreinfowindow) {
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
				deletePreferredPollingRateObserver(preferredPollingRateObserver);
				super.windowClosed(arg0);
			}

		}

		protected class PreferredPollingRateObserver implements Observer {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Observer#update(java.util.Observable,
			 *      java.lang.Object)
			 */
			public void update(Observable o, Object arg) {

				Integer value = (Integer) arg;

				sliderlabel.setText("preferred polling-rate: " + value);
			}

		}

		int maxValue = 60;

		InfoWindow baseWindow;;

		JLabel sliderlabel;

		protected PreferredPollingRateObserver preferredPollingRateObserver = new PreferredPollingRateObserver();

		public InfoWindowExtension(InfoWindow baseWindow) {

			this.baseWindow = baseWindow;

			JPanel sliderpanel = new JPanel();

			sliderpanel.setLayout(new BoxLayout(sliderpanel, BoxLayout.Y_AXIS));

			JSlider slider;

			Hashtable<Integer, JLabel> labeltable = new Hashtable<Integer, JLabel>();

			labeltable.put(1, new JLabel("1"));

			for ( int i = 10; i <= maxValue; i += 10 )
				labeltable.put(i, new JLabel(new Integer(i).toString()));

			slider = new JSlider(1, maxValue, (int) (((PubSubNode) baseWindow.getNode()).getPreferredPollingRate()));
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

			addPreferredPollingRateObserver(preferredPollingRateObserver);
			preferredPollingRateObserver.update(getPreferredPollingRateNotifier(), getPreferredPollingRate());

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
				setPreferredPollingRate(slider.getValue());
				setRtt(getPreferredPollingRateMillis());
				updateRequestTimer(getPreferredPollingRateMillis());
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
				deleteRttObserver(rttObserver);
				super.windowClosed(arg0);
			}

		}

		private class RttObserver implements Observer {

			public void update(Observable observable, Object arg1) {

				Long rftv = (Long) arg1;

				rttLabel.setText("current polling-rate: " + rftv / 1000 + "    ");
			}

		}

		JLabel rttLabel = new JLabel();

		RttObserver rttObserver = new RttObserver();

		InfoWindow moreinfowindow;

		public MoreInfoWindowExtension(InfoWindow moreinfowindow) {

			this.moreinfowindow = moreinfowindow;

			addRttObserver(rttObserver);

			rttObserver.update(rttNotifier, new Long(getRtt()));

			moreinfowindow.addWindowListener(new CloseWindowAdapter());

			moreinfowindow.getContentPane().add(rttLabel);

			moreinfowindow.pack();

		}

	}

	protected class RttNotifier extends Observable {

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

	}

}
