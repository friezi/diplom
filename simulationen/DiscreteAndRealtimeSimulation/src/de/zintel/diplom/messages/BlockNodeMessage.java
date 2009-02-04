package de.zintel.diplom.messages;
import de.zintel.diplom.simulation.InternalMessage;
import de.zintel.diplom.simulation.Node;

/**
 * 
 */

/**
 * blocks a node.
 * 
 * @author Friedemann Zintel
 * 
 */
public class BlockNodeMessage extends InternalMessage {

	/**
	 * @param src
	 * @param dst
	 * @param arrivalTime
	 */
	public BlockNodeMessage(Node src, Node dst, long arrivalTime) {
		super(src, dst, arrivalTime);
	}

}
