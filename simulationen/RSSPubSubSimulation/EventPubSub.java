/**
 * 
 */
import java.util.*;

/**
 * An PubSub-Node which can handle event-feeds
 * 
 * @author Friedemann Zintel
 * 
 */
public class EventPubSub extends PubSub {

	protected RSSEventFeedFactory rssEventFeedFactory;

	LinkedList<Event> events = new LinkedList<Event>();

	protected class EventDateComparator implements Comparator<Event> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(T, T)
		 */
		public int compare(Event event1, Event event2) {

			if (event1.getGeneralContend().getPubDate().before(event2.getGeneralContend().getPubDate()))
				return -1;
			else if (event1.getGeneralContend().getPubDate().after(event2.getGeneralContend().getPubDate()))
				return 1;
			else
				return 0;
		}

	}

	public EventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {

		super(xp, yp, params);
		setRssEventFeedFactory(rssEventFeedFactory);

	}

	protected void handleRSSFeedMessage(RSSFeedMessage fm) {

		RSSFeed feed = fm.getFeed();

		if (!(feed instanceof EventFeed)) {
			System.out.println("Internal Error: AdjustingEventFeed: handleRSSFeedMessage(): feed is not instance of EventFeed!");
			System.exit(1);
		}

		Iterator<Event> it = ((EventFeed) feed).eventIterator();
		Event event;
		TreeSet<Event> sortedEvents = new TreeSet<Event>(new EventDateComparator());

		// store all new events and send them to neighbours
		while (it.hasNext()) {

			event = it.next();
			if (!events.contains(event)) {

				events.addLast(event);
				sortedEvents.add(event);
			}

		}

		if (!sortedEvents.isEmpty()) {

			// calculate ratio of delayed event:
			long eventtime = sortedEvents.first().getGeneralContend().getPubDate().getTime();
			long now = new Date().getTime();
			long diff = (now - eventtime) / 1000;
			long overhead = diff - params.maxRefreshRate;
			if (overhead > 0) {

				int delayRatio = (int) ((overhead * 100) / params.maxRefreshRate);
				int uptodateRatio = (int) ((params.maxRefreshRate * 100) / diff);

				System.out.print("Delayed message: " + delayRatio + "%");
				if (uptodateRatio < 100)
					System.out.print("    uptodateRatio: " + uptodateRatio + "%");
				System.out.println();

			}

		}

		// deletion of event-overhead
		while (events.size() > params.maxSubscriberEvents)
			events.removeFirst();

		LinkedList<Event> newEvents = new LinkedList<Event>(sortedEvents);

		if (newEvents.size() > 0) {

			// show the feed
			setFeed(getRssEventFeedFactory().newRSSEventFeed(events, fm.getFeed().getGeneralContent()));
			setRssFeedRepresentation(getRssFeedRepresentationFactory().newRSSFeedRepresentation(this, getFeed()));
			getRssFeedRepresentation().represent();

			updateRequestTimer();

			// send a new feed (only with new events) to Broker, if we didn't
			// get the message from
			// him
			if (fm.getSrc() != getBroker()) {

				this.getStatistics().addServerFeed(this);

				RSSFeed newFeed = getRssEventFeedFactory().newRSSEventFeed(newEvents, fm.getFeed().getGeneralContent());
				new RSSFeedMessage(this, getBroker(), newFeed, fm.getRssFeedRepresentation().copyWith(null, newFeed), params);

			} else {

				// just statistics

				this.getStatistics().addBrokerFeed(this);
				// if we got the message from a broker, a request for RSSServer
				// will be omitted
				// -> statistics
				this.getStatistics().addOmittedRSSFeedRequest(this);

			}

		} else {

			// got an old feed; update timer only if sender is RSSServer
			if (fm.getSrc() == getRssServer()) {
				updateRequestTimer();
			}
		}

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

}
