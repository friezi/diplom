

/**
 * Acknowledgement for a register-message
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class RegisterAckMessage extends Message {

	private static int NORMALSIZE = 5;

	RegisterAckMessage(Node src, Node dst, int runtime) {
		super(src, dst, runtime);
		setColor(MessageColors.registerAckColor);
	}

	protected int size() {
		return NORMALSIZE;
	}

}
