package de.zintel.diplom.messages;
import java.awt.Color;

import de.zintel.diplom.rps.SubnetParameters;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.simulation.TransferMessage;

/**
 */

/**
 * A ping containing subnet-information
 * 
 * @author Friedemann Zintel
 * 
 */
public class PingMessage extends TransferMessage {

	private static int NORMALSIZE = 5;

	private static int BIGSIZE = 15;

	private SubnetParameters subParams = null;

	private SimParameters params;

	public PingMessage(Node src, Node dst, SubnetParameters subparams, SimParameters params) {

		super(src, dst, params.getSubnetParamMsgRT());
		this.params = params;
		setSubParams(subparams);
		if ( params.isShowSizeBrokerMsg() == true )
			setColor(Color.white);
		else
			setColor(MessageColors.pingColor);
	}

	/**
	 * @return Returns the subParams.
	 */
	public SubnetParameters getSubParams() {
		return subParams;
	}

	/**
	 * @param subparams
	 *            The subparams to set.
	 */
	public void setSubParams(SubnetParameters subparams) {
		this.subParams = subparams;
	}

	public int size() {
		if ( params.isShowSizeBrokerMsg() == true )
			return BIGSIZE;
		return NORMALSIZE;
	}

	@Override
	public String text() {
		if ( params.isShowSizeBrokerMsg() == true )
			if ( subParams != null )
				return String.valueOf(subParams.getSize());
		return "";
	}

}
