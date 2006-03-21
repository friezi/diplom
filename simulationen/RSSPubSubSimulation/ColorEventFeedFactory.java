/**
 * 
 */
import java.awt.Color;
import java.util.*;

/**
 * @author Friedemann Zintel
 * 
 * For producing ColorEventFeeds
 * 
 */
public class ColorEventFeedFactory implements RSSFeedFactory {

	SimParameters params;

	LinkedList<ColorEvent> events = new LinkedList<ColorEvent>();

	public ColorEventFeedFactory(SimParameters params) {
		this.params = params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RSSFeedFactory#newRSSFeed(RSSFeedGeneralContent)
	 */
	public RSSFeed newRSSFeed(RSSFeedGeneralContent generalContent) {

		int id;

		if ( events.size() == 0 )
			id = 1;
		else
			id = events.getLast().id + 1;

		events.addLast(new ColorEvent(id, ColorFactory.nextColor()));

		if ( events.size() > params.maxEventEntries )
			events.removeFirst();

		return new ColorEventFeed(events, generalContent);
	}

}
