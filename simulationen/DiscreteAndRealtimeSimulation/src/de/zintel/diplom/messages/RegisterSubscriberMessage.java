package de.zintel.diplom.messages;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.TransferMessage;



/**
 * Registers a new subscriber at broker.
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class RegisterSubscriberMessage extends TransferMessage {

	private static int SIZE = 5;

	/**
	 * @param src
	 *            sourcenode
	 * @param dst
	 *            destinationnode
	 * @param runtime
	 *            runtime for message
	 */
	public RegisterSubscriberMessage(Node src, Node dst, int runtime) {
		super(src, dst, runtime);
		setColor(MessageColors.registerColor);
	}

	public int size() {
		return SIZE;
	}

}
