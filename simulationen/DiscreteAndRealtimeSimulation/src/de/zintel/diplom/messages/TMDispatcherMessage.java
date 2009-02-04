package de.zintel.diplom.messages;
import de.zintel.diplom.simulation.InternalMessage;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.TransferMessage;

/**
 * 
 */

/**
 * 
 * A Message for a TransferMessageDispatcher containing only a TransferMessage.
 * 
 * @author Friedemann Zintel
 * 
 */
public class TMDispatcherMessage extends InternalMessage {

	public TransferMessage message;

	/**
	 * @param src
	 * @param dst
	 * @param arrivalTime
	 */
	public TMDispatcherMessage(Node src, Node dst, long arrivalTime, TransferMessage message) {
		super(src, dst, arrivalTime);
		this.message = message;
	}

}
