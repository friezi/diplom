package de.zintel.diplom.messages;

import de.zintel.diplom.feed.RSSFeed;
import de.zintel.diplom.feed.RSSFeedRepresentation;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;


/**
 * 
 */

/**
 * In this Message additional (network-relevant-)information can be stored.
 * 
 * @author Friedemann Zintel
 * 
 */
public class RSSFeedRichMessage extends RSSFeedMessage {

	/**
	 * The adjusted roundtriptime
	 */
	long artt = 0;

	/**
	 * @param src
	 * @param dst
	 * @param feed
	 * @param rssFeedRepresentation
	 * @param params
	 */
	public RSSFeedRichMessage(Node src, Node dst, RSSFeed feed, RSSFeedRepresentation rssFeedRepresentation, SimParameters params) {
		super(src, dst, feed, rssFeedRepresentation, params);
	}

	/**
	 * @return Returns the rtt.
	 */
	public long getArtt() {
		return artt;
	}

	/**
	 * @param artt
	 *            The artt to set.
	 */
	public void setArtt(long artt) {
		this.artt = artt;
	}

	public void copyRichInformation(RSSFeedRichMessage rfrm) {
		this.artt = rfrm.getArtt();
	}

}
