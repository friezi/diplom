/**
 * 
 */
import java.util.*;

import rsspubsubframework.Node;


/**
 * @author Friedemann Zintel
 *
 */
public interface RSSEventFeedFactory extends RSSFeedFactory {
	
	public RSSFeed newRSSEventFeed(Object events, RSSFeedGeneralContent generalContent);

}
