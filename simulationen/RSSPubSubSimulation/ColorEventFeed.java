/**
 * 
 */

import java.awt.Color;
import java.util.*;

/**
 * A Feed which stores SimnpleEventFeeds
 * 
 * @author Friedemann Zintel
 * 
 */
public class ColorEventFeed extends ColorFeed {

	LinkedList<SimpleColorEvent> events;

	ColorEventFeed(LinkedList<SimpleColorEvent> events, RSSFeedGeneralContent generalContent) {

		super(Color.black, generalContent);
		this.events = (LinkedList<SimpleColorEvent>) events.clone();
		this.color = calcColor();
	}

	/**
	 * It will calculate the median-color from all events
	 * 
	 * @return the color
	 */
	Color calcColor() {

		float r_f, g_f, b_f;
		int size = events.size();

		r_f = g_f = b_f = 0;

		Iterator<SimpleColorEvent> it = events.iterator();
		SimpleColorEvent event;

		while ( it.hasNext() ) {

			event = it.next();
			r_f += event.color.getRed() / size;
			g_f += event.color.getGreen() / size;
			b_f += event.color.getBlue() / size;

		}

		return new Color((int) r_f, (int) g_f, (int) b_f);
	}

	/**
	 * @return Returns the events.
	 */
	public LinkedList<SimpleColorEvent> getEvents() {
		return events;
	}

	/**
	 * @param events
	 *            The events to set.
	 */
	public void setEvents(LinkedList<SimpleColorEvent> events) {
		this.events = events;
	}

}
