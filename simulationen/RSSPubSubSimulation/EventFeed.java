/**
 * An interface for general event-feeds.
 */

import java.util.*;

/**
 * @author Friedemann Zintel
 *
 */
public interface EventFeed {
	
	Iterator<Event> eventIterator();

}
