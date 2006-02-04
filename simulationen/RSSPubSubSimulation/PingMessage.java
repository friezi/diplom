import rsspubsubframework.*;

/**
 * 
 */

/**
 * @author friezi
 * 
 */
public class PingMessage extends Message {

	private static int NORMALSIZE = 8;

	public PingMessage(Node src, Node dst, int runtime) {
		super(src, dst, runtime);
		setColor(MessageColors.pingColor);
	}

	protected int size() {
		return NORMALSIZE;
	}

}
