package de.zintel.diplom.messages;
import de.zintel.diplom.simulation.InternalMessage;
import de.zintel.diplom.simulation.Node;

/**
 * 
 */

/**
 * unblocks a node.
 * 
 * @author Friedemann Zintel
 *
 */
public class UnblockNodeMessage extends InternalMessage {

	/**
	 * @param src
	 * @param dst
	 * @param arrivalTime
	 */
	public UnblockNodeMessage(Node src, Node dst, long arrivalTime) {
		super(src, dst, arrivalTime);
	}

}
