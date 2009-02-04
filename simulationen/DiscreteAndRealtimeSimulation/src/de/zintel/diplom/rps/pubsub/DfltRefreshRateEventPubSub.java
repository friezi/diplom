package de.zintel.diplom.rps.pubsub;
import java.awt.GridLayout;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.zintel.diplom.feed.RSSEventFeedFactory;
import de.zintel.diplom.gui.InfoWindow;
import de.zintel.diplom.messages.NetworkSizeUpdateMessage;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.SimParameters;

/**
 * 
 */

/**
 * 
 * This PubSub refreshes the feeds according to a default refreshrate, given by
 * the user It doesn't care about the networksize.
 * 
 * @author Friedemann Zintel
 * 
 */
public class DfltRefreshRateEventPubSub extends EventPubSub {

	/**
	 * @param xp
	 * @param yp
	 * @param rssEventFeedFactory
	 * @param params
	 */
	public DfltRefreshRateEventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {
		super(xp, yp, rssEventFeedFactory, params);
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
		// diff = 0;
		// return (new Random().nextInt((spreadFactor) * ttl + 1) + (ttl -
		// diff)) * 1000;
		return (long) ((Engine.getSingleton().getRandom().nextFloat() * (getPppSecs()) + (ttl - diffsecs)) * 1000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimerByNewFeed()
	 */
	@Override
	protected synchronized void updateFeedRequestTimerByNewFeed() {
		updateFeedRequestTimer(calculateTimeToRefreshInterval());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimerByOldFeed()
	 */
	@Override
	protected synchronized void updateFeedRequestTimerByOldFeed() {
		updateFeedRequestTimer(getPppSecs() * 1000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#updateRequestTimer(long)
	 */
	@Override
	synchronized protected void updateFeedRequestTimer(long interval) {

		feedRequestTask.cancel();
		purgeFeedRequestTimer();
		feedRequestTask = new FeedRequestTask(this, params);
		// if there's no response from server, we need to re-request
		feedRequestTimer.schedule(feedRequestTask, interval);

	}

	/* (non-Javadoc)
	 * @see PubSub#getFeedRequestTimerPeriod()
	 */
	@Override
	protected long getFeedRequestTimerPeriod() {
		return getPppSecs() * 1000;
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

	protected class InfoWindowExtension implements ChangeListener {

		int maxValue = 60;

		InfoWindow baseWindow;

		public InfoWindowExtension(InfoWindow baseWindow) {

			this.baseWindow = baseWindow;

			JPanel sliderpanel = new JPanel(new GridLayout(2, 1));

			JSlider slider;

			Hashtable<Integer, JLabel> labeltable = new Hashtable<Integer, JLabel>();

			labeltable.put(1, new JLabel("1"));

			for (int i = 10; i <= maxValue; i += 10)
				labeltable.put(i, new JLabel(new Integer(i).toString()));

			slider = new JSlider(1, maxValue, (int) (((PubSubNode) baseWindow.getNode()).getPppSecs()));
			slider.setLabelTable(labeltable);
			slider.addChangeListener(this);
			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);

			sliderpanel.add(new JLabel("max. refresh-rate", JLabel.CENTER));
			sliderpanel.add(slider);

			baseWindow.getContentPane().add(new JSeparator());
			baseWindow.getContentPane().add(sliderpanel);

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
				((PubSubNode) baseWindow.getNode()).setPppSecs(slider.getValue());
			}

		}

	}

}
