package de.zintel.diplom.messages;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.TransferMessage;



/**
 * Unregisters a new subscriber from broker.
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class UnregisterSubscriberMessage extends TransferMessage {

	private static int SIZE = 5;

	/**
	 * @param src
	 *            sourcenode
	 * @param dst
	 *            destinationnode
	 * @param runtime
	 *            runtime for message
	 */
	public UnregisterSubscriberMessage(Node src, Node dst, int runtime) {
		super(src, dst, runtime);
		setColor(MessageColors.unregisterColor);
	}

	public int size() {
		return SIZE;
	}

}
