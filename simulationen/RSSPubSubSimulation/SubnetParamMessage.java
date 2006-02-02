import rsspubsubframework.*;

/**
 * 
 */

/**
 * @author friezi
 * 
 */
public class SubnetParamMessage extends Message {

	private static int SIZE = 8;

	private SubnetParameters subParams=null;

	public SubnetParamMessage(Node src, Node dst, SubnetParameters subparams, int runtime) {

		super(src, dst, runtime);
		setSubParams(subparams);
		setColor(new java.awt.Color((float) 0.5, (float) 0.5, 0));

	}

	protected int size() {
		return SIZE;
	}

	/**
	 * @return Returns the subParams.
	 */
	public SubnetParameters getSubParams() {
		return subParams;
	}

	/**
	 * @param subParams The subParams to set.
	 */
	public void setSubParams(SubnetParameters subparams) {
		this.subParams = subparams;
	}

}
