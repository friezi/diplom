import rsspubsubframework.*;
import java.awt.*;
import java.util.*;

public class PubSub extends PubSubNode {

	protected class FeedRequestTask extends TimerTask {

		public void run() {
			requestFeed();
			// feedRequestTimer.schedule(new
			// FeedRequestTask(),calculateInterval());
		}

	}

	// dummy feed to prevent NullPointerException
	protected RSSFeed feed = new RSSFeed(new RSSFeedGeneralContent());

	protected Timer feedRequestTimer = new Timer();

	protected FeedRequestTask feedRequestTask = new FeedRequestTask();

	protected int spreadFactor;

	public PubSub(int xp, int yp, SimParameters params) {
		super(xp, yp, params);
		this.spreadFactor = params.spreadFactor;
	}

	public void init() {
		requestFeed();
	}

	public void receiveMessage(Message m) {

		if ( m instanceof RSSFeedMessage ) {

			RSSFeedMessage fm = (RSSFeedMessage) m;

			// do only with new feeds
			if ( fm.getFeed().isNewerThan(getFeed()) ) {

				// show the feed
				setFeed(fm.getFeed());
				setRssFeedRepresentation(getRssFeedRepresentationFactory().newRSSFeedRepresentation(this,
						getFeed()));
				getRssFeedRepresentation().represent();

				updateRequestTimer();

				// send the feed to Broker, if we didn't get the message from
				// him
				if ( fm.getSrc() != getBroker() )
					new RSSFeedMessage(this, getBroker(), getFeed(), fm.getRssFeedRepresentation().copyWith(
							null, getFeed()), params);

			} else {

				// got an old feed; update timer only if sender is RSSServer
				if ( fm.getSrc() == getRssServer() ) {
					updateRequestTimer();
				}
			}
		}
	}

	protected long calculateInterval() {

		Date now = new Date();
		Date feedDate = getFeed().getGeneralContent().getLastBuiltDate();
		int ttl = getFeed().getGeneralContent().getTtl();
		int diff = (int) ((now.getTime() - feedDate.getTime()) / 1000);
		if ( diff > ttl )
			diff = ttl;
		return (new Random().nextInt(spreadFactor * ttl + 1) + (ttl - diff)) * 1000;

	}

	synchronized protected void updateRequestTimer() {

		feedRequestTask.cancel();
		feedRequestTask = new FeedRequestTask();
		feedRequestTimer.schedule(feedRequestTask, calculateInterval());

	}

	synchronized protected void requestFeed() {
		new RSSFeedRequestMessage(this, getRssServer(), params);
	}

	public RSSFeed getFeed() {
		return feed;
	}

	public void setFeed(RSSFeed feed) {
		this.feed = feed;
	}

	public synchronized void setDefaultColor(){
		getRssFeedRepresentation().represent();
	}
}
