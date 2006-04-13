import rsspubsubframework.*;
import java.awt.*;

/**
 * 
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class SubnetParamMessage extends Message {

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

		super(src, dst, params.subnetParamMsgRT);
		this.messageInitiator = messageInitiator;
		this.causeOfMessage = causeOfMessage;
		this.params = params;
		setSubParams(subparams);
		if ( params.showSizeBrokerMsg == true )
			setColor(Color.white);
		else
			setColor(MessageColors.subnetParamColor);

	}

	protected int size() {
		if ( params.showSizeBrokerMsg == true )
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
	protected String text() {
		// TODO Auto-generated method stub
		if ( params.showSizeBrokerMsg == true )
			if ( subParams != null )
				return String.valueOf(subParams.getSize());
		return "";
	}

	/**
	 * @return Returns the causeOfMessage.
	 */
	protected synchronized Node getCOM() {
		return causeOfMessage;
	}

	/**
	 * @return Returns the messageInitiator.
	 */
	protected synchronized Node getMI() {
		return messageInitiator;
	}

}
