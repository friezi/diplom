import rsspubsubframework.*;
import java.util.*;

public class RSSServer extends RSSServerNode {

	protected class UpdateFeedTask extends TimerTask {

		private RSSServer rssServer;

		public UpdateFeedTask(RSSServer rssServer) {
			this.rssServer = rssServer;
		}

		public void run() {

			rssServer.newFeed();
			rssServer.newTimer();

		}
	}

	protected RSSFeed feed;

	protected Random random;

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
	protected void newTimer() {

		UpdateFeedTask updatefeed = new UpdateFeedTask(this);
		Timer timer = new Timer();
		// int r = random.nextInt((maxUpIntv - minUpIntv) + 1) + minUpIntv;
		timer.schedule(updatefeed, 1000 * (random.nextInt((maxUpIntv - minUpIntv) + 1) + minUpIntv));

	}

	public void init() {

		newFeed();
		newTimer();

	}

	protected void receiveMessage(Message m) {

		// process only if not blocked
		if ( isBlocked() == true )
			return;

		if ( m instanceof RSSFeedRequestMessage ) {

			new RSSFeedMessage(this, m.getSrc(), getFeed(), getRssFeedRepresentationFactory()
					.newRSSFeedRepresentation(null, getFeed()), params);

		}

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

}
