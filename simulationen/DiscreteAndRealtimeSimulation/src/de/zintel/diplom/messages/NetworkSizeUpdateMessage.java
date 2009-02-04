package de.zintel.diplom.messages;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.simulation.TransferMessage;



/**
 * 
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class NetworkSizeUpdateMessage extends TransferMessage {

	private static int NORMALSIZE = 5;

	long size;

	public NetworkSizeUpdateMessage(Node src, Node dst, long size, SimParameters params) {
		super(src, dst, params.getSubnetParamMsgRT());
		this.size = size;
		setColor(MessageColors.subnetParamColor);
	}

	// size of Message in display!!!
	public int size() {
		return NORMALSIZE;
	}

	/**
	 * @return Returns the size.
	 */
	public synchronized long getSize() {
		return size;
	}

}
