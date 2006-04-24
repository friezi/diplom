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

	private long requestFeedTimeoutValue;

	private Date requestFeedMessageDate;

	private Date rssFeedMessageDate;

	private RequestFeedTimeoutValueNotifier requestFeedTimueoutValueNotifier = new RequestFeedTimeoutValueNotifier();

	/**
	 * @param xp
	 * @param yp
	 * @param rssEventFeedFactory
	 * @param params
	 */
	public CongestionControlEventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory,
			SimParameters params) {
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
		resetRequestFeedTimeoutValue();
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

		incRequestFeedTimeoutValue();
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
		// diff = 0;
		// return (new Random().nextInt((spreadFactor) * ttl + 1) + (ttl -
		// diff)) * 1000;
		long timeoutsecs = getRequestFeedTimeoutValue() / 1000;
		// if ( requestFeedTimeoutValue < getMaxRefreshRateMS() )
		// timeout = getMaxRefreshRate();
		// else
		// timeout = requestFeedTimeoutValue/1000;
		return (long) ((new Random().nextFloat() * timeoutsecs + (ttl - diffsecs)) * 1000);
	}

	protected void updateRequestTimer() {

		feedRequestTask.cancel();
		purgeFeedRequestTimer();
		feedRequestTask = new FeedRequestTask(this);
		long rftv = getRequestFeedTimeoutValue();
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
		feedRequestTimer.schedule(feedRequestTask, interval, getRequestFeedTimeoutValue());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimerByOldFeedFromBroker()
	 */
	@Override
	protected synchronized void updateRequestTimerByOldFeedFromBroker() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimerByNewFeedFromBroker()
	 */
	@Override
	protected synchronized void updateRequestTimerByNewFeedFromBroker() {

		// if the requestFeedTimeoutValue is very high, it can happen, that we
		// never get the possibility
		// to request the server: we get "knocked out" by feeds from brokers and
		// can never again achieve the
		// preferrefdRefreshRate. to prevent this, we have to check, if both
		// values differ and in positive case
		// we need to update the timer despite the fact that the feed came from
		// a broker.
		if ( getRequestFeedTimeoutValue() > getPreferredRefreshRateMilis() ) {

			// if one is running aready (must be!) take the shortest timeout
			long interval = calculateNextRequestTimeout();

			if ( interval < feedRequestTask.scheduledExecutionTime() - System.currentTimeMillis() )
				updateRequestTimer(interval);

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

	protected void recalculateRequestTimeoutinterval() {

		if ( requestFeedTimerCounter > 1 ) {
			// we had to request several times -> set the timeout to meanvalue

			long rftv = getRequestFeedTimeoutValue();

			setRequestFeedTimeoutValue((rftv * requestFeedTimerCounter + rftv) / 2);

		} else {

			long roundtriptime = (rssFeedMessageDate.getTime() - requestFeedMessageDate.getTime());

			if ( roundtriptime < getPreferredRefreshRateMilis() )
				setRequestFeedTimeoutValue(getPreferredRefreshRateMilis());
			else
				setRequestFeedTimeoutValue(roundtriptime);
		}

	}

	protected void incRequestFeedTimeoutValue() {
		setRequestFeedTimeoutValue(getRequestFeedTimeoutValue() + getPreferredRefreshRateMilis());
	}

	protected void resetRequestFeedTimeoutValue() {
		setRequestFeedTimeoutValue(0);
	}

	protected void incRequestFeedTimerCounter() {
		requestFeedTimerCounter++;
	}

	protected void resetRequestFeedTimerCounter() {
		requestFeedTimerCounter = 0;
	}

	/**
	 * returns the preferredRefreshRate in miliseconds
	 * 
	 * @return preferredRefreshRate in miliseconds
	 */
	protected long getPreferredRefreshRateMilis() {
		return getPreferredRefreshRate() * 1000;
	}

	/**
	 * @return Returns the requestFeedTimeoutValue.
	 */
	public synchronized long getRequestFeedTimeoutValue() {
		return requestFeedTimeoutValue;
	}

	/**
	 * @param requestFeedTimeoutValue
	 *            The requestFeedTimeoutValue to set.
	 */
	public synchronized void setRequestFeedTimeoutValue(long requestFeedTimeoutValue) {
		this.requestFeedTimeoutValue = requestFeedTimeoutValue;
		requestFeedTimueoutValueNotifier.notifyObservers(new Long(requestFeedTimeoutValue));
	}

	protected synchronized void addRequestFeedTimeoutValueObserver(Observer observer) {
		requestFeedTimueoutValueNotifier.addObserver(observer);
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
				deletePreferredRefreshRateNotifier(preferredRefreshRateObserver);
				super.windowClosed(arg0);
			}

		}

		protected class PreferredRefreshRateObserver implements Observer {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Observer#update(java.util.Observable,
			 *      java.lang.Object)
			 */
			public void update(Observable o, Object arg) {

				Integer value = (Integer) arg;

				sliderlabel.setText("preferred refresh-rate: " + value);
			}

		}

		int maxValue = 60;

		InfoWindow baseWindow;;

		JLabel sliderlabel;

		protected PreferredRefreshRateObserver preferredRefreshRateObserver = new PreferredRefreshRateObserver();

		public InfoWindowExtension(InfoWindow baseWindow) {

			this.baseWindow = baseWindow;

			JPanel sliderpanel = new JPanel();

			sliderpanel.setLayout(new BoxLayout(sliderpanel, BoxLayout.Y_AXIS));

			JSlider slider;

			Hashtable<Integer, JLabel> labeltable = new Hashtable<Integer, JLabel>();

			labeltable.put(1, new JLabel("1"));

			for ( int i = 10; i <= maxValue; i += 10 )
				labeltable.put(i, new JLabel(new Integer(i).toString()));

			slider = new JSlider(1, maxValue, (int) (((PubSubNode) baseWindow.getNode())
					.getPreferredRefreshRate()));
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

			addPreferredRefreshrateObserver(preferredRefreshRateObserver);
			preferredRefreshRateObserver.update(getPreferredRefreshRateNotifier(), getPreferredRefreshRate());

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
				setPreferredRefreshRate(slider.getValue());
				setRequestFeedTimeoutValue(getPreferredRefreshRateMilis());
				updateRequestTimer(getPreferredRefreshRateMilis());
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
				requestFeedTimueoutValueNotifier.deleteObserver(reqFdTmOValObserver);
				super.windowClosed(arg0);
			}

		}

		private class RequestFeedTimeoutValueObserver implements Observer {

			public void update(Observable observable, Object arg1) {

				Long rftv = (Long) arg1;

				reqFdTmOValLabel.setText("current refresh-rate: " + rftv / 1000 + "    ");

			}

		}

		JLabel reqFdTmOValLabel = new JLabel();

		RequestFeedTimeoutValueObserver reqFdTmOValObserver = new RequestFeedTimeoutValueObserver();

		InfoWindow moreinfowindow;

		public MoreInfoWindowExtension(InfoWindow moreinfowindow) {

			this.moreinfowindow = moreinfowindow;

			requestFeedTimueoutValueNotifier.addObserver(reqFdTmOValObserver);

			reqFdTmOValObserver.update(requestFeedTimueoutValueNotifier, new Long(
					getRequestFeedTimeoutValue()));

			moreinfowindow.addWindowListener(new CloseWindowAdapter());

			moreinfowindow.getContentPane().add(reqFdTmOValLabel);

			moreinfowindow.pack();

		}

	}

	protected class RequestFeedTimeoutValueNotifier extends Observable {

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
