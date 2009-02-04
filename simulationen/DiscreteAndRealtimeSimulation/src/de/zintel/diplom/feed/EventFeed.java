package de.zintel.diplom.feed;
/**
 * An interface for general event-feeds.
 */

import java.util.*;

import de.zintel.diplom.event.Event;

/**
 * An interface for all types of event-feeds
 * 
 * @author Friedemann Zintel
 *
 */
public interface EventFeed {
	
	public Iterator<Event> eventIterator();

}
