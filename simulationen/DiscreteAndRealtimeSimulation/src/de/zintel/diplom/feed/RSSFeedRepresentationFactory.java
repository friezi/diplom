package de.zintel.diplom.feed;

import de.zintel.diplom.graphics.DisplayableObject;


public interface RSSFeedRepresentationFactory {
	public RSSFeedRepresentation newRSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed);

	public RSSFeedRepresentation newRSSFeedRepresentation(RSSFeed feed);
}
