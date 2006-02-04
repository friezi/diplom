import rsspubsubframework.*;

/**
 * Unregistration of a Broker
 */

/**
 * @author friezi
 * 
 */
public class UnregisterBrokerMessage extends Message {

	private static int NORMALSIZE = 8;

	public UnregisterBrokerMessage(Node src, Node dst, int runtime) {
		super(src, dst, runtime);
		setColor(MessageColors.unregisterColor);
	}

	protected int size() {
		return NORMALSIZE;
	}

}
