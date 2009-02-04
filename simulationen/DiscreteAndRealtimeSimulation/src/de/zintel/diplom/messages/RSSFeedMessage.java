package de.zintel.diplom.messages;
import de.zintel.diplom.feed.RSSFeed;
import de.zintel.diplom.feed.RSSFeedRepresentation;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.simulation.TransferMessage;



public class RSSFeedMessage extends TransferMessage {

	private static int SIZE = 7;

	private RSSFeedRepresentation rssFeedRepresentation;

	SimParameters params;

	protected RSSFeed feed;

	public RSSFeedMessage(Node src, Node dst, RSSFeed feed, RSSFeedRepresentation rssFeedRepresentation, SimParameters params) {
		super(src, dst, params.getRssFeedMsgRT());
		this.feed = feed;
		this.rssFeedRepresentation = rssFeedRepresentation;
		this.params = params;

		// for representing at the correct object
		getRssFeedRepresentation().setDObj(this);

		// only represent feed on special demand
		if ( params.isRssFeedMsgRepresent() == true )
			if ( getRssFeedRepresentation() != null )
				getRssFeedRepresentation().represent();
	}

	public RSSFeedRepresentation getRssFeedRepresentation() {
		return rssFeedRepresentation;
	}

	public int size() {
		return SIZE;
	}

	public RSSFeed getFeed() {
		return feed;
	}

	/**
	 * @param feed The feed to set.
	 */
	public void setFeed(RSSFeed feed) {
		this.feed = feed;
	}

}
