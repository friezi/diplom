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

	LinkedList<ColorEvent> events;

	ColorEventFeed(LinkedList<ColorEvent> events, RSSFeedGeneralContent generalContent) {

		super(Color.black, generalContent);
		this.events = (LinkedList<ColorEvent>) events.clone();
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

		Iterator<ColorEvent> it = events.iterator();
		ColorEvent event;

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
	public LinkedList<ColorEvent> getEvents() {
		return events;
	}

	/**
	 * @param events
	 *            The events to set.
	 */
	public void setEvents(LinkedList<ColorEvent> events) {
		this.events = events;
	}

}
