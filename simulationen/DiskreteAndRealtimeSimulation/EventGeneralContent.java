/**
 * 
 */
import java.util.Date;

/**
 * The general content all Events must contain.
 * 
 * @author Friedemann Zintel
 *
 */
public class EventGeneralContent {
	
	protected Date pubDate = new Date();

	/**
	 * @return Returns the pubDate.
	 */
	public Date getPubDate() {
		return pubDate;
	}

	/**
	 * @param pubDate The pubDate to set.
	 */
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

}
