/**
 * 
 */
import java.awt.Color;
import java.util.*;

/**
 * For producing ColorEventFeeds
 * 
 * @author Friedemann Zintel
 * 
 */
public class ColorEventFeedFactory implements RSSEventFeedFactory {

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

		Integer id;

		if ( events.size() == 0 )
			id = 1;
		else {

			if ( events.getLast().id == Integer.MAX_VALUE )
				id = 1;
			else
				id = events.getLast().id + 1;

		}

		events.addLast(new ColorEvent(id, ColorFactory.nextColor()));

		if ( events.size() > params.maxFeedEvents )
			events.removeFirst();

		return new ColorEventFeed(events, generalContent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RSSEventFeedFactory#newRSSEventFeed(java.util.LinkedList,
	 *      RSSFeedGeneralContent)
	 */
	public RSSFeed newRSSEventFeed(Object events, RSSFeedGeneralContent generalContent) {
		// TODO Auto-generated method stub

		return new ColorEventFeed((LinkedList<ColorEvent>) events, generalContent);
	}

}
