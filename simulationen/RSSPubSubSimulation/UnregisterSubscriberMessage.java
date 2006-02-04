import rsspubsubframework.*;

/**
 * Ubregisters a new subscriber from broker.
 */

/**
 * @author friezi
 * 
 */
public class UnregisterSubscriberMessage extends Message {

	private static int SIZE = 8;

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

	protected int size() {
		return SIZE;
	}

}
