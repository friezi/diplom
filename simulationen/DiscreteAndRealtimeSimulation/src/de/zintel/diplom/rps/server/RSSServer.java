package de.zintel.diplom.rps.server;
import java.util.*;

import javax.swing.JFrame;

import de.zintel.diplom.feed.RSSFeed;
import de.zintel.diplom.feed.RSSFeedGeneralContent;
import de.zintel.diplom.gui.InfoWindow;
import de.zintel.diplom.messages.RSSFeedMessage;
import de.zintel.diplom.messages.RSSFeedRequestMessage;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.InternalMessage;
import de.zintel.diplom.simulation.Message;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.synchronization.AbstractTimer;
import de.zintel.diplom.synchronization.ExtendedTimerTask;

public class RSSServer extends RSSServerNode {

	protected class GenerateFeedTask extends ExtendedTimerTask {

		private RSSServer rssServer;

		public GenerateFeedTask(RSSServer rssServer, SimParameters params) {
			super(params);
			this.rssServer = rssServer;
		}

		public void run() {

			super.run();
			setMessage(new GenerateFeedMessage(rssServer, rssServer, getNextExecutionTime()).send());

		}
	}

	/**
	 * Message from a timertask to indicate that a new feed should be generated
	 * 
	 * @author Friedemann Zintel
	 * 
	 */
	protected class GenerateFeedMessage extends InternalMessage {

		GenerateFeedMessage(Node src, Node dst, long arrivalTime) {
			super(src, dst, arrivalTime);
		}

	}

	protected RSSFeed feed;

	protected GenerateFeedTask generateFeedTask;

	protected Random random;

	protected JFrame infoWindow;

	AbstractTimer timer;

	/**
	 * @param xp
	 *            x-position
	 * @param yp
	 *            y-position
	 * @param params
	 *            relevant parameters
	 */
	public RSSServer(int xp, int yp, SimParameters params) {
		super(xp, yp, params);
		random = Engine.getSingleton().getRandom();

		timer = getTimerfactory().newTimer();
		generateFeedTask = new GenerateFeedTask(this, params);
	}

	public synchronized void finalize() {
		timer.cancel();
	}

	/**
	 * generates a new feed
	 */
	private synchronized void newFeed() {

		RSSFeedGeneralContent generalContent = new RSSFeedGeneralContent();
		generalContent.setLastBuiltDate(new Date(getEngine().getTime()));
		generalContent.setTtl(getTtl());
		setFeed(getRssFeedFactory().newRSSFeed(generalContent));

	}

	/**
	 * gerenates a new feed-update-timer
	 */
	protected synchronized void scheduleTimer() {

		generateFeedTask.cancel();
		generateFeedTask = new GenerateFeedTask(this, params);
		// int r = random.nextInt((maxUpIntv - minUpIntv) + 1) + minUpIntv;
		timer.purge();
		timer.schedule(generateFeedTask, 1000 * (random.nextInt((maxUpIntv - minUpIntv) + 1) + minUpIntv));

	}

	public void start() {

		newFeed();
		scheduleTimer();

	}

	@Override
	protected void handleStandardMessage(Message m) {

		// process only if not blocked
		if ( isBlocked() == true )
			return;

		if ( m instanceof RSSFeedRequestMessage ) {

			handleRSSFeedRequestMessage((RSSFeedRequestMessage) m);

		} else if ( m instanceof GenerateFeedMessage ) {

			handleGenerateFeedMessage((GenerateFeedMessage) m);

		}

	}

	@Override
	protected void handleInternalMessage(InternalMessage m) {

		if ( m instanceof GenerateFeedMessage ) {

			handleInternalMessageGenerateFeedMessage((GenerateFeedMessage) m);

		} else {

			handleStandardMessage(m);

		}

	}

	protected void handleInternalMessageGenerateFeedMessage(GenerateFeedMessage m) {

		handleStandardMessage(m);

		scheduleTimer();

	}

	protected void handleRSSFeedRequestMessage(RSSFeedRequestMessage rfrm) {

		// observers (most probably the Engine) have to be informed about the
		// request
		this.getStatistics().addReceivedRSSFeedRequest(this);

		new RSSFeedMessage(this, rfrm.getOrigin(), getFeed(), getRssFeedRepresentationFactory().newRSSFeedRepresentation(getFeed()), params).send();

	}

	protected void handleGenerateFeedMessage(GenerateFeedMessage gfm) {
		newFeed();
	}

	/**
	 * @return the feed
	 */
	public synchronized RSSFeed getFeed() {
		return feed;
	}

	/**
	 * @param feed
	 *            the feed
	 */
	public synchronized void setFeed(RSSFeed feed) {
		this.feed = feed;
		setRssFeedRepresentation(getRssFeedRepresentationFactory().newRSSFeedRepresentation(this, feed));
		getRssFeedRepresentation().represent();
	}

	public synchronized void setDefaultColor() {
		getRssFeedRepresentation().represent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RSSServerNode#showInfo()
	 */
	@Override
	public void showInfo() {
		super.showInfo();
		(infoWindow = new InfoWindow("RSSServer-Info", this)).setVisible(true);
	}

}
