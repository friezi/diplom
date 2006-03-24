/**
 * An interface for general event-feeds.
 */

import java.util.*;

/**
 * An interface for all types of event-feeds
 * 
 * @author Friedemann Zintel
 *
 */
public interface EventFeed {
	
	Iterator<Event> eventIterator();

}
