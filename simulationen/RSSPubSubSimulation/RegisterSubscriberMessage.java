import rsspubsubframework.*;

/**
 * Regsiters a new subscriber at broker.
 */

/**
 * @author friezi
 * 
 */
public class RegisterSubscriberMessage extends Message {

	private static int SIZE = 8;

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
		setColor(new java.awt.Color(0, 0, (float) 0.5));
	}

	protected int size() {
		return SIZE;
	}

}
