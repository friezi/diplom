package de.zintel.diplom.messages;

/**
 * 
 */
import de.zintel.diplom.rps.pubsub.PubSubNode;
import de.zintel.diplom.rps.server.RSSServerNode;
import de.zintel.diplom.simulation.SimParameters;

/**
 * @author Friedemann Zintel
 * 
 */
public class SequencedRSSFeedRequestMessage extends RSSFeedRequestMessage {

	protected long sequenceNumber;

	protected long retransmissionSequenceNumber;

	/**
	 * @param src
	 * @param dest
	 * @param params
	 */
	public SequencedRSSFeedRequestMessage(PubSubNode src, RSSServerNode dest, long sequenceNumber, long retransmissionSequenceNumber, SimParameters params) {

		super(src, dest, params);
		this.sequenceNumber = sequenceNumber;
		this.retransmissionSequenceNumber = retransmissionSequenceNumber;

	}

	/**
	 * @return Returns the retransmissionSequenceNumber.
	 */
	public synchronized long getRetransmissionSequenceNumber() {
		return retransmissionSequenceNumber;
	}

	/**
	 * @return Returns the sequenceNumber.
	 */
	public synchronized long getSequenceNumber() {
		return sequenceNumber;
	}

}
