/**
 * 
 */
import java.util.*;

import rsspubsubframework.Node;


/**
 * A base-interface for all kinds of feed-factories which will produce event-feeds
 * 
 * @author Friedemann Zintel
 *
 */
public interface RSSEventFeedFactory extends RSSFeedFactory {
	
	public RSSFeed newRSSEventFeed(Object events, RSSFeedGeneralContent generalContent);

}
