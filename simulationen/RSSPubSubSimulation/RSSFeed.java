import java.util.Date;

public class RSSFeed {

	protected RSSFeedGeneralContent generalContent;

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
