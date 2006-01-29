import rsspubsubframework.Message;
import rsspubsubframework.Node;

public class RSSFeedMessage extends Message implements Cloneable{

	private static int RUNTIME = 15;

	private static int SIZE = 7;

	private RSSFeedRepresentation rssFeedRepresentation;

	protected RSSFeed feed;

	public RSSFeedMessage(Node src, Node dst, RSSFeed feed, RSSFeedRepresentation rssFeedRepresentation,
			boolean representRSSFeed) {
		super(src, dst, RUNTIME);
		this.feed = feed;
		this.rssFeedRepresentation = rssFeedRepresentation;
		getRssFeedRepresentation().setDObj(this);
		if ( representRSSFeed == true )
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
//	
//	public RSSFeedMessage clone(){
//		return 
//	}
}
