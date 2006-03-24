import rsspubsubframework.*;
import java.awt.*;

/**
 * For registering a new broker in network.
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class RegisterBrokerMessage extends Message {

	private static int NORMALSIZE = 8;

	private static int BIGSIZE = 15;

	private SubnetParameters subParams = null;

	private SimParameters params;

	public RegisterBrokerMessage(Node src, Node dst, SubnetParameters subparams, SimParameters params) {

		super(src, dst, params.subnetParamMsgRT);
		this.params = params;
		setSubParams(subparams);
		if ( params.showSizeBrokerMsg == true )
			setColor(Color.white);
		else
			setColor(MessageColors.registerColor);

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

	protected String text() {
		// TODO Auto-generated method stub
		if ( params.showSizeBrokerMsg == true )
			if ( subParams != null )
				return String.valueOf(subParams.getSize());
		return "";
	}

}
