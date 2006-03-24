import java.util.Date;

/**
 * A base-class for all kinds of RSS-feeds
 * 
 * @author Friedemann Zintel
 *
 */
public class RSSFeed {

	protected RSSFeedGeneralContent generalContent;

	/**
	 * @param generalContent
	 */
	public RSSFeed(RSSFeedGeneralContent generalContent) {
		this.generalContent = generalContent;
	}

	public boolean isNewerThan(RSSFeed feed) {
		return getGeneralContent().getLastBuiltDate().after(feed.getGeneralContent().getLastBuiltDate());
	}

	/**
	 * @return Returns the generalContent.
	 */
	public RSSFeedGeneralContent getGeneralContent() {
		return generalContent;
	}

	/**
	 * @param generalContent
	 *            The generalContent to set.
	 */
	public void setGeneralContent(RSSFeedGeneralContent generalContent) {
		this.generalContent = generalContent;
	}

}
