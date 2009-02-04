package de.zintel.diplom.messages;
import java.awt.*;

import de.zintel.diplom.rps.SubnetParameters;
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
public class SubnetParamMessage extends TransferMessage {

	private static int NORMALSIZE = 5;

	private static int BIGSIZE = 15;

	private SubnetParameters subParams = null;

	private SimParameters params;

	/**
	 * the node at which a change of subnet-size occured and which initiated the
	 * message
	 */
	protected Node messageInitiator;

	/**
	 * the node which caused a change of subnet-size
	 */
	protected Node causeOfMessage;

	public SubnetParamMessage(Node src, Node dst, Node messageInitiator, Node causeOfMessage,
			SubnetParameters subparams, SimParameters params) {

		super(src, dst, params.getSubnetParamMsgRT());
		this.messageInitiator = messageInitiator;
		this.causeOfMessage = causeOfMessage;
		this.params = params;
		setSubParams(subparams);
		if ( params.isShowSizeBrokerMsg() == true )
			setColor(Color.white);
		else
			setColor(MessageColors.subnetParamColor);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsspubsubframework.DisplayableObject#text()
	 */
	@Override
	public String text() {
		if ( params.isShowSizeBrokerMsg() == true )
			if ( subParams != null )
				return String.valueOf(subParams.getSize());
		return "";
	}

	/**
	 * @return Returns the causeOfMessage.
	 */
	public synchronized Node getCOM() {
		return causeOfMessage;
	}

	/**
	 * @return Returns the messageInitiator.
	 */
	public synchronized Node getMI() {
		return messageInitiator;
	}

}
