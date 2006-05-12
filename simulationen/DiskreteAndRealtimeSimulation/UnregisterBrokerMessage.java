

/**
 * Unregistration of a Broker
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class UnregisterBrokerMessage extends Message {

	private static int NORMALSIZE = 5;

	public UnregisterBrokerMessage(Node src, Node dst, int runtime) {
		super(src, dst, runtime);
		setColor(MessageColors.unregisterColor);
	}

	protected int size() {
		return NORMALSIZE;
	}

}
