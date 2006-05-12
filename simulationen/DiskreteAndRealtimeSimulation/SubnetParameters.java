import java.util.*;

/**
 * This class can store all relevant informations for a subnet
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class SubnetParameters {

	/**
	 * size of the subnet
	 */
	private long size = 0;

	private Date lastUpdate = new Date();

	public SubnetParameters() {
//		try {
//			Thread.sleep(1);
//		} catch ( InterruptedException ie ) {
//		}
	}

	public SubnetParameters(long size, Date updateTime) {
		setSize(size);
		setLastUpdate(updateTime);
//		try {
//			Thread.sleep(1);
//		} catch ( InterruptedException ie ) {
//		}
	}

	/**
	 * @return Returns the size.
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size
	 *            The size to set.
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * @return Returns the lastUpdate.
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate
	 *            The lastUpdate to set.
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

}
