import rsspubsubframework.*;
import java.awt.*;

/**
 * 
 */

/**
 * @author friezi
 * 
 */
public class SubnetParamMessage extends Message {

	private static int NORMALSIZE = 8;

	private static int BIGSIZE = 15;

	private SubnetParameters subParams = null;

	private SimParameters params;

	public SubnetParamMessage(Node src, Node dst, SubnetParameters subparams, SimParameters params) {

		super(src, dst, params.subntSzMsgRT);
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

}
