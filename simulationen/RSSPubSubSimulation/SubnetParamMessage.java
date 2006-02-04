import rsspubsubframework.*;

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
			setColor(new java.awt.Color(1, 1, (float) 1));
		else
			setColor(new java.awt.Color((float) 0.5, (float) 0.5, 0));

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
	 * @param subParams
	 *            The subParams to set.
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
