import rsspubsubframework.DisplayableObject;

public class RSSFeedRepresentation {

	protected RSSFeed feed;

	protected DisplayableObject dObj = null;

	/**
	 * @param dObj
	 *            The object which should represent the feed
	 * @param feed
	 *            The feed
	 */
	public RSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed) {
		this.dObj = dObj;
		this.feed = feed;
	}

	public RSSFeedRepresentation(RSSFeed feed) {
		this.feed = feed;
	}

	/**
	 * Will represent resp. show the feed.
	 * 
	 * Should be overwritten in derived classes.
	 * 
	 */
	public void represent() {
		dObj.setColor(MessageColors.registerAckColor);
	}

	/**
	 * @param obj
	 *            the new obj
	 */
	public void setDObj(DisplayableObject obj) {
		dObj = obj;
	}

	/**
	 * Creates a copy of RSSFeedRepresentation with the new parameters.
	 * 
	 * Should be overwritten in derived classes
	 * 
	 * @param dObj
	 *            The object to represent the feed
	 * @param feed
	 *            The feed
	 * @return A new RSSFeedRepresentation
	 */
	public RSSFeedRepresentation copyWith(DisplayableObject dObj, RSSFeed feed) {
		return new RSSFeedRepresentation(dObj, feed);
	}
}
