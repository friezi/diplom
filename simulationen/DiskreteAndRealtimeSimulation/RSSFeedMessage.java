import rsspubsubframework.Message;
import rsspubsubframework.Node;

public class RSSFeedMessage extends Message {

	private static int SIZE = 7;

	private RSSFeedRepresentation rssFeedRepresentation;

	SimParameters params;

	protected RSSFeed feed;

	public RSSFeedMessage(Node src, Node dst, RSSFeed feed, RSSFeedRepresentation rssFeedRepresentation, SimParameters params) {
		super(src, dst, params.rssFeedMsgRT);
		this.feed = feed;
		this.rssFeedRepresentation = rssFeedRepresentation;
		this.params = params;

		// for representing at the correct object
		getRssFeedRepresentation().setDObj(this);

		// only represent feed on special demand
		if ( params.rssFeedMsgRepresent == true )
			if ( getRssFeedRepresentation() != null )
				getRssFeedRepresentation().represent();
	}

	public RSSFeedRepresentation getRssFeedRepresentation() {
		return rssFeedRepresentation;
	}

	protected int size() {
		return SIZE;
	}

	public RSSFeed getFeed() {
		return feed;
	}

}
