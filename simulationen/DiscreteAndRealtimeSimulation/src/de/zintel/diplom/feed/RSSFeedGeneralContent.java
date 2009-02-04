package de.zintel.diplom.feed;
import java.util.Date;

import de.zintel.diplom.simulation.Engine;

/**
 * The general content all RSSFeed must contain. You can add additional
 * information by building derived classes from this.
 * 
 * @author Friedemann Zintel
 * 
 */
public class RSSFeedGeneralContent {

	/**
	 * Time To Live : the minimum update interval
	 */
	protected int ttl = 0;

	/**
	 * The date of last built of the feed
	 */
	protected Date lastBuiltDate = new Date(Engine.getSingleton().getTime());

	/**
	 * @return Returns the ttl.
	 */
	public int getTtl() {
		return ttl;
	}

	/**
	 * @param ttl
	 *            The ttl to set.
	 */
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	/**
	 * @return Returns the lastBuiltDate.
	 */
	public Date getLastBuiltDate() {
		return lastBuiltDate;
	}

	/**
	 * @param lastBuiltDate
	 *            The lastBuiltDate to set.
	 */
	public void setLastBuiltDate(Date lastBuiltDate) {
		this.lastBuiltDate = lastBuiltDate;
	}

}
