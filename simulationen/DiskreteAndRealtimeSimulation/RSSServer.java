import java.util.*;

import javax.swing.JFrame;

public class RSSServer extends RSSServerNode {

	protected class GenerateFeedTask extends ExtendedTimerTask {

		private RSSServer rssServer;

		public GenerateFeedTask(RSSServer rssServer) {
			this.rssServer = rssServer;
		}

		public void run() {

			super.run();
			new GenerateFeedMessage(rssServer, rssServer).send();
			rssServer.scheduleTimer();

		}
	}

	/**
	 * Message from a timertask to indicate that a new feed should be generated
	 * 
	 * @author Friedemann Zintel
	 * 
	 */
	protected class GenerateFeedMessage extends InternalMessage {

		GenerateFeedMessage(Node src, Node dst) {
			super(src, dst);
		}

	}

	protected RSSFeed feed;

	protected Random random;

	protected JFrame infoWindow;

	ExtendedTimer timer = new ExtendedTimer();

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
		random = new Random();
	}

	public synchronized void finalize() {
		timer.cancel();
	}

	/**
	 * generates a new feed
	 */
	private synchronized void newFeed() {

		RSSFeedGeneralContent generalContent = new RSSFeedGeneralContent();
		generalContent.setLastBuiltDate(new Date());
		generalContent.setTtl(getTtl());
		setFeed(getRssFeedFactory().newRSSFeed(generalContent));

	}

	/**
	 * gerenates a new feed-update-timer
	 */
	protected synchronized void scheduleTimer() {

		GenerateFeedTask generateFeedTask = new GenerateFeedTask(this);
		// int r = random.nextInt((maxUpIntv - minUpIntv) + 1) + minUpIntv;
		timer.schedule(generateFeedTask, 1000 * (random.nextInt((maxUpIntv - minUpIntv) + 1) + minUpIntv));

	}

	public void init() {

		newFeed();
		scheduleTimer();

	}

	protected void receiveMessage(Message m) {

		// process only if not blocked
		if ( isBlocked() == true )
			return;

		if ( m instanceof RSSFeedRequestMessage ) {

			handleRSSFeedRequestMessage((RSSFeedRequestMessage) m);

		} else if ( m instanceof GenerateFeedMessage ) {

			handleUpdateFeedMessage((GenerateFeedMessage) m);

		}

	}

	protected void handleRSSFeedRequestMessage(RSSFeedRequestMessage rfrm) {

		// observers (most probably the Engine) have to be informed about the
		// request
		this.getStatistics().addReceivedRSSFeedRequest(this);

		new RSSFeedMessage(this, rfrm.getSrc(), getFeed(), getRssFeedRepresentationFactory().newRSSFeedRepresentation(getFeed()), params).send();

	}

	protected void handleUpdateFeedMessage(GenerateFeedMessage gfm) {
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
