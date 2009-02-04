package de.zintel.diplom.messages;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.TransferMessage;



/**
 * Unregistration of a Broker
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class UnregisterBrokerMessage extends TransferMessage {

	private static int NORMALSIZE = 5;

	public UnregisterBrokerMessage(Node src, Node dst, int runtime) {
		super(src, dst, runtime);
		setColor(MessageColors.unregisterColor);
	}

	public int size() {
		return NORMALSIZE;
	}

}
