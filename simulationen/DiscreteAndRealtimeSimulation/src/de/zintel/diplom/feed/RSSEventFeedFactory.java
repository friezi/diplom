package de.zintel.diplom.feed;
/**
 * 
 */

/**
 * A base-interface for all kinds of feed-factories which will produce
 * event-feeds
 * 
 * @author Friedemann Zintel
 * 
 */
public interface RSSEventFeedFactory extends RSSFeedFactory {

	public RSSFeed newRSSEventFeed(Object events, RSSFeedGeneralContent generalContent);

}
