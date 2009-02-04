package de.zintel.diplom.messages;

/**
 * 
 */
import de.zintel.diplom.feed.RSSFeed;
import de.zintel.diplom.feed.RSSFeedRepresentation;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;

/**
 * @author Friedemann Zintel
 * 
 */
public class InitialBrokerRSSFeedMessage extends RSSFeedMessage {

	private int numberOfSubscribers;

	private long artt;

	/**
	 * @param src
	 * @param dst
	 * @param feed
	 * @param rssFeedRepresentation
	 * @param numberOfSubscribers
	 * @param artt
	 * @param params
	 */
	public InitialBrokerRSSFeedMessage(Node src, Node dst, RSSFeed feed, RSSFeedRepresentation rssFeedRepresentation, int numberOfSubscribers, long artt,
			SimParameters params) {

		super(src, dst, feed, rssFeedRepresentation, params);
		this.numberOfSubscribers = numberOfSubscribers;
		this.artt = artt;

	}

	/**
	 * @return Returns the numberOfSubscribers.
	 */
	public int getNumberOfSubscribers() {
		return numberOfSubscribers;
	}

	/**
	 * @return Returns the rtt.
	 */
	public long getArtt() {
		return artt;
	}

}
