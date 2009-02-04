package de.zintel.diplom.rps.pubsub;
/**
 * 
 */
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;

import de.zintel.diplom.event.Event;
import de.zintel.diplom.feed.EventFeed;
import de.zintel.diplom.feed.RSSEventFeedFactory;
import de.zintel.diplom.feed.RSSFeed;
import de.zintel.diplom.graphics.EventListDisplay;
import de.zintel.diplom.gui.InfoWindow;
import de.zintel.diplom.messages.InitialBrokerRSSFeedMessage;
import de.zintel.diplom.messages.RSSFeedMessage;
import de.zintel.diplom.messages.RSSFeedRichMessage;
import de.zintel.diplom.rps.broker.BrokerNode;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;

/**
 * An PubSub-Node which can handle event-feeds
 * 
 * @author Friedemann Zintel
 * 
 */
public class EventPubSub extends PubSub {

	protected RSSEventFeedFactory rssEventFeedFactory = null;

	LinkedList<Event> events = new LinkedList<Event>();

	NewEventsNotifier newEventsNotifier = new NewEventsNotifier();

	protected class EventDateComparator implements Comparator<Event> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(T, T)
		 */
		public int compare(Event event1, Event event2) {

			if ( event1.getGeneralContend().getPubDate().before(event2.getGeneralContend().getPubDate()) )
				return -1;
			else if ( event1.getGeneralContend().getPubDate().after(event2.getGeneralContend().getPubDate()) )
				return 1;
			else
				return 0;
		}

	}

	protected class NewEventsNotifier extends Observable {

		public void notifyObservers() {
			if ( this.countObservers() > 0 ) {
				setChanged();
				super.notifyObservers(new LinkedList<Event>(events));
			}
		}

	}

	public EventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {

		super(xp, yp, params);
		setRssEventFeedFactory(rssEventFeedFactory);

	}

	@Override
	public void reset() {

		super.reset();
		events.clear();
		newEventsNotifier.notifyObservers();

	}

	protected void storeInitialBrokerInformation(InitialBrokerRSSFeedMessage ibrm) {

	}

	protected void handleRSSFeedMessage(RSSFeedMessage fm) {

		RSSFeed feed = fm.getFeed();

		if ( !(feed instanceof EventFeed) ) {
			System.out.println("Internal Error: AdjustingEventFeed: handleRSSFeedMessage(): feed is not instance of EventFeed!");
			System.exit(1);
		}

		// store all additional information, if contained
		if ( fm instanceof RSSFeedRichMessage ) {

			storeRichInformation((RSSFeedRichMessage) fm);

		} else if ( fm instanceof InitialBrokerRSSFeedMessage ) {

			storeInitialBrokerInformation((InitialBrokerRSSFeedMessage) fm);

		}

		Iterator<Event> it = ((EventFeed) feed).eventIterator();
		Event event;
		TreeSet<Event> sortedEvents = new TreeSet<Event>(new EventDateComparator());

		// store all new events and send them to neighbours
		while ( it.hasNext() ) {

			event = it.next();
			if ( events.contains(event) == false ) {

				events.addLast(event);
				sortedEvents.add(event);
			}

		}

		// calculate ratio of delayed event:
		if ( !sortedEvents.isEmpty() )
			calculateUpdateAndDelay(sortedEvents.first().getGeneralContend().getPubDate().getTime());

		// deletion of event-overhead
		while ( events.size() > params.getMaxSubscriberEvents() )
			events.removeFirst();
		//
		LinkedList<Event> newEvents = new LinkedList<Event>(sortedEvents);

		if ( newEvents.size() > 0 ) {
			// it's a new feed

			// inform observers
			newEventsNotifier.notifyObservers();

			// show the feed
			setFeed(getRssEventFeedFactory().newRSSEventFeed(events, fm.getFeed().getGeneralContent()));
			setRssFeedRepresentation(getRssFeedRepresentationFactory().newRSSFeedRepresentation(this, getFeed()));
			getRssFeedRepresentation().represent();

			// send a new feed (only with new events) to Broker, if we didn't
			// get the message from
			// him
			if ( brokerlist.contains(fm.getOrigin()) == false ) {
				// sender is server

				updateFeedRequestTimerByNewFeedFromServer();

				this.getStatistics().addServerFeed(this);

				RSSFeed newFeed = getRssEventFeedFactory().newRSSEventFeed(newEvents, fm.getFeed().getGeneralContent());
				RSSFeedRichMessage m;
				// send it to all brokers
				for ( BrokerNode broker : brokerlist ) {
					m = new RSSFeedRichMessage(this, broker, newFeed, fm.getRssFeedRepresentation().copyWith(null, newFeed), params);
					addRichInformation(m);
					m.send();
				}

			} else {
				// sender is a broker

				updateFeedRequestTimerByNewFeedFromBroker();

				// just statistics
				this.getStatistics().addBrokerFeed(this);
				// if we got the message from a broker, a request for RSSServer
				// will be omitted
				// -> statistics
				this.getStatistics().addOmittedRSSFeedRequest(this);

				RSSFeedRichMessage m;
				// send it to all other brokers
				for ( BrokerNode broker : brokerlist )
					if ( broker != fm.getOrigin() ) {
						m = new RSSFeedRichMessage(this, broker, getFeed(), fm.getRssFeedRepresentation().copyWith(null, getFeed()), params);
						addRichInformation(m);
						m.send();
					}

			}

		} else {
			// it's an old feed

			// got an old feed; update timer only if sender is RSSServer
			if ( fm.getOrigin() == getRssServer() ) {
				updateFeedRequestTimerByOldFeedFromServer();
			} else {
				updateFeedRequestTimerByOldFeedFromBroker();
			}
		}

	}

	protected void calculateUpdateAndDelay(long eventtime) {

		long now = getEngine().getTime();
		long diffsecs = (now - eventtime) / 1000;
		long overhead = diffsecs - getPppSecs();
		int messageDelayRatio = 0;
		int uptodateRatio = 100;
		if ( overhead > 0 ) {

			messageDelayRatio = (int) ((overhead * 100) / getPppSecs());
			uptodateRatio = (int) ((getPppSecs() * 100) / diffsecs);
			/*
			 * System.out.print("message-delay: " + messageDelayRatio + "%"); if
			 * (uptodateRatio < 100) System.out.print(" uptodateRatio: " +
			 * uptodateRatio + "%"); System.out.println();
			 */
		}

		getStatistics().setUptodateRatio(uptodateRatio);
		getStatistics().setMessageDelayRatio(messageDelayRatio);

	}

	/**
	 * @return Returns the rssEventFeedFactory.
	 */
	public RSSEventFeedFactory getRssEventFeedFactory() {
		return rssEventFeedFactory;
	}

	/**
	 * @param rssEventFeedFactory
	 *            The rssEventFeedFactory to set.
	 */
	public void setRssEventFeedFactory(RSSEventFeedFactory rssEventFeedFactory) {
		this.rssEventFeedFactory = rssEventFeedFactory;
	}

	/**
	 * @return Returns the neweventsnotifier.
	 */
	public synchronized NewEventsNotifier getNewEventsNotifier() {
		return newEventsNotifier;
	}

	/**
	 * @param observer the observer
	 */
	public synchronized void addNewEventsObserver(Observer observer) {
		newEventsNotifier.addObserver(observer);
	}


	/**
	 * @param observer the observer
	 */
	public synchronized void deleteNewEventsObserver(Observer observer) {
		newEventsNotifier.deleteObserver(observer);
	}

	/**
	 * To override. Adds additional information to a RSSFeedRichMessage.
	 * 
	 * @param rfrm
	 */
	protected void addRichInformation(RSSFeedRichMessage rfrm) {
	}

	/**
	 * To override. For storing additional information of a RSSFeedRichMessage.
	 * 
	 * @param rfrm
	 */
	protected void storeRichInformation(RSSFeedRichMessage rfrm) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PubSub#showMoreInfo(InfoWindow)
	 */
	@Override
	public void showMoreInfo(InfoWindow moreinfowindow) {

		super.showMoreInfo(moreinfowindow);
		new EventWindow(moreinfowindow.getNode());

	}

	private class EventWindow {

		private class WindowChecker extends WindowAdapter {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosed(WindowEvent arg0) {

				eventlistcomponent.stopObserve();
				super.windowClosed(arg0);

			}

		}

		private InfoWindow infowindow;

		private EventListDisplay eventlistcomponent;

		public EventWindow(Node relatedNode) {

			eventlistcomponent = new EventListDisplay(getNewEventsNotifier());

			infowindow = new InfoWindow("Events", relatedNode);
			infowindow.getContentPane().add(eventlistcomponent);
			infowindow.pack();

			eventlistcomponent.startObserve();

			eventlistcomponent.update(getNewEventsNotifier(), new LinkedList<Event>(events));

		}

	}

}
