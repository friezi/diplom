

/**
 * 
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class NetworkSizeUpdateMessage extends Message {

	private static int NORMALSIZE = 5;

	long size;

	public NetworkSizeUpdateMessage(Node src, Node dst, long size, SimParameters params) {
		super(src, dst, params.subnetParamMsgRT);
		this.size = size;
		setColor(MessageColors.subnetParamColor);
	}

	// size of Message in display!!!
	protected int size() {
		return NORMALSIZE;
	}

	/**
	 * @return Returns the size.
	 */
	protected synchronized long getSize() {
		return size;
	}

}
