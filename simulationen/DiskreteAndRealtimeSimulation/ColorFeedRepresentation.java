

public class ColorFeedRepresentation extends RSSFeedRepresentation {

	/**
	 * @param dObj
	 *            The object which should represent the feed
	 * @param feed
	 *            The feed
	 */
	public ColorFeedRepresentation(DisplayableObject dObj, ColorFeed feed) {
		super(dObj, feed);
	}

	/**
	 * Will represent resp. show the Feed.
	 * 
	 */
	public void represent() {
		dObj.setColor(((ColorFeed) feed).getColor());
	}

	/**
	 * Creates a copy of RSSFeedRepresentation with the new parameters
	 * 
	 * @param dObj
	 *            The object to represent the feed
	 * @param feed
	 *            The feed
	 * @return A new ColorFeedRepresentation
	 */
	public RSSFeedRepresentation copyWith(DisplayableObject dObj, RSSFeed feed) {
		return new ColorFeedRepresentation(dObj, (ColorFeed) feed);
	}
}
