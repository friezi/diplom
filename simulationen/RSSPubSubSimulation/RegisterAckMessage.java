import rsspubsubframework.*;

/**
 * Acknowledgement for a register-message
 */

/**
 * @author friezi
 * 
 */
public class RegisterAckMessage extends Message {

	private static int NORMALSIZE = 8;

	RegisterAckMessage(Node src, Node dst, int runtime) {
		super(src, dst, runtime);
		setColor(MessageColors.registerAckColor);
	}

	protected int size() {
		return NORMALSIZE;
	}

}
