import rsspubsubframework.*;
import java.util.*;

public class RSSServer extends RSSServerNode {

	protected class UpdateFeedTask extends TimerTask {

		private RSSServer rssServer;

		public UpdateFeedTask(RSSServer rssServer) {
			this.rssServer = rssServer;
		}

		public void run() {
			
			rssServer.setFeed(rssServer.getRssFeedFactory().newRSSFeed());
			rssServer.newTimer();
			
		}
	}

	protected RSSFeed feed;

	protected static long DELAY = 4000;

	public RSSServer(int xp, int yp) {
		super(xp, yp);
	}

	private void newFeed() {
		setFeed(getRssFeedFactory().newRSSFeed());
	}

	protected void newTimer() {

		UpdateFeedTask updatefeed = new UpdateFeedTask(this);
		Timer timer = new Timer();
		timer.schedule(updatefeed, DELAY);

	}
	
	public void init(){

		setFeed(getRssFeedFactory().newRSSFeed());
		newTimer();

	}

	protected void receiveMessage(Message m) {

		if ( m instanceof RSSFeedRequestMessage ) {

			new RSSFeedMessage(this, m.getSrc(), getFeed(), getRssFeedRepresentationFactory()
					.newRSSFeedRepresentation(null, getFeed()), true);

		}

	}

	public synchronized RSSFeed getFeed() {
		return feed;
	}

	public synchronized void setFeed(RSSFeed feed) {
		this.feed = feed;
		setRssFeedRepresentation(getRssFeedRepresentationFactory().newRSSFeedRepresentation(this,feed));
		getRssFeedRepresentation().represent();
	}

}
