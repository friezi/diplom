package de.zintel.diplom.feed;
/**
 * 
 */
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import de.zintel.diplom.event.ColorEvent;
import de.zintel.diplom.event.Event;
import de.zintel.diplom.event.EventGeneralContent;
import de.zintel.diplom.graphics.EventListDisplay;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.util.ColorFactory;

/**
 * For producing ColorEventFeeds
 * 
 * @author Friedemann Zintel
 * 
 */
public class ColorEventFeedFactory implements RSSEventFeedFactory {

	protected class NewEventsNotifier extends Observable {

		public void notifyObservers(LinkedList<Event> events) {
			if ( this.countObservers() > 0 ) {
				setChanged();
				super.notifyObservers(new LinkedList<Event>(events));
			}
		}

	}

	SimParameters params;

	LinkedList<Event> events = new LinkedList<Event>();

	NewEventsNotifier newEventsNotifier = new NewEventsNotifier();

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

			if ( ((ColorEvent) events.getLast()).id == Integer.MAX_VALUE )
				id = 1;
			else
				id = ((ColorEvent) events.getLast()).id + 1;

		}

		events.addLast(new ColorEvent(id, ColorFactory.nextColor(), new EventGeneralContent()));

		if ( events.size() > params.getMaxFeedEvents() )
			events.removeFirst();

		getFeedNotifier().notifyObservers(events);

		return new ColorEventFeed(events, generalContent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RSSEventFeedFactory#newRSSEventFeed(java.util.LinkedList,
	 *      RSSFeedGeneralContent)
	 */
	public RSSFeed newRSSEventFeed(Object events, RSSFeedGeneralContent generalContent) {

		return new ColorEventFeed((LinkedList<Event>) events, generalContent);
	}

	/**
	 * @return Returns the neweventsnotifier.
	 */
	public synchronized NewEventsNotifier getFeedNotifier() {
		return newEventsNotifier;
	}

	/**
	 * @param observer
	 *            the observer
	 */
	public synchronized void addFeedObserver(Observer observer) {
		newEventsNotifier.addObserver(observer);
	}

	/**
	 * @param observer
	 *            the observer
	 */
	public synchronized void deleteFeedObserver(Observer observer) {
		newEventsNotifier.deleteObserver(observer);
	}

	public EventListDisplay createAndStartNewFeedDisplay() {

		EventListDisplay eventlistdisplay = new EventListDisplay(getFeedNotifier());
		eventlistdisplay.startObserve();
		eventlistdisplay.update(getFeedNotifier(), events);

		return eventlistdisplay;

	}

}
