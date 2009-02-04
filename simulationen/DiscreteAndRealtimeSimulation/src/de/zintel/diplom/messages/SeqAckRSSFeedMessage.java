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
public class SeqAckRSSFeedMessage extends RSSFeedMessage {

	protected long sequenceNumber;

	protected long retransmissionSequenceNumber;

	/**
	 * @param src
	 * @param dst
	 * @param feed
	 * @param rssFeedRepresentation
	 * @param params
	 */
	public SeqAckRSSFeedMessage(Node src, Node dst, long sequenceNumber, long retransmissionSequenceNumber, RSSFeed feed,
			RSSFeedRepresentation rssFeedRepresentation, SimParameters params) {

		super(src, dst, feed, rssFeedRepresentation, params);
		this.sequenceNumber = sequenceNumber;
		this.retransmissionSequenceNumber = retransmissionSequenceNumber;

	}

	public synchronized long getRetransmissionSequenceNumber() {
		return retransmissionSequenceNumber;
	}

	public synchronized long getSequenceNumber() {
		return sequenceNumber;
	}

}
