package de.zintel.diplom.messages;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.TransferMessage;


/**
 * Acknowledgement for a register-message
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class RegisterAckMessage extends TransferMessage {

	private static int NORMALSIZE = 5;

	protected Node registrator;

	public RegisterAckMessage(Node src, Node dst, Node registrator, int runtime) {

		super(src, dst, runtime);
		setColor(MessageColors.registerAckColor);

		this.registrator = registrator;

	}

	public int size() {
		return NORMALSIZE;
	}

	/**
	 * @return Returns the registrator.
	 */
	public Node getRegistrator() {
		return registrator;
	}

}
