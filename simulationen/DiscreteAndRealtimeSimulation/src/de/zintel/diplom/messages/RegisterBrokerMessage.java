package de.zintel.diplom.messages;
import java.awt.*;

import de.zintel.diplom.rps.SubnetParameters;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.simulation.TransferMessage;

/**
 * For registering a new broker in network.
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class RegisterBrokerMessage extends TransferMessage {

	private static int NORMALSIZE = 5;

	private static int BIGSIZE = 15;

	private SubnetParameters subParams = null;

	private SimParameters params;

	public RegisterBrokerMessage(Node src, Node dst, SubnetParameters subparams, SimParameters params) {

		super(src, dst, params.getSubnetParamMsgRT());
		this.params = params;
		setSubParams(subparams);
		if ( params.isShowSizeBrokerMsg() == true )
			setColor(Color.white);
		else
			setColor(MessageColors.registerColor);

	}

	public int size() {
		if ( params.isShowSizeBrokerMsg() == true )
			return BIGSIZE;
		return NORMALSIZE;
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

	public String text() {
		if ( params.isShowSizeBrokerMsg() == true )
			if ( subParams != null )
				return String.valueOf(subParams.getSize());
		return "";
	}

}
