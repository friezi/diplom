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

	public EventPubSub(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {

		super(xp, yp, params);
		setRssEventFeedFactory(rssEventFeedFactory);

	}

	protected void handleRSSFeedMessage(RSSFeedMessage fm) {

		RSSFeed feed = fm.getFeed();

		if ( !(feed instanceof EventFeed) ) {
			System.out.println("Internal Error: AdjustingEventFeed: handleRSSFeedMessage(): feed is not instance of EventFeed!");
			System.exit(1);
		}

		Iterator<Event> it = ((EventFeed) feed).eventIterator();
		Event event;
		LinkedList<Event> newEvents = new LinkedList<Event>();

		// store all new events and send them to neighbours
		while ( it.hasNext() ) {

			event = it.next();
			if ( !events.contains(event) ) {

				events.addLast(event);
				newEvents.addLast(event);
			}
		}

		// deletion of event-overhead
		while ( events.size() > params.maxSubscriberEvents )
			events.removeFirst();

		if ( newEvents.size() > 0 ) {

			// show the feed
			setFeed(getRssEventFeedFactory().newRSSEventFeed(events, fm.getFeed().getGeneralContent()));
			setRssFeedRepresentation(getRssFeedRepresentationFactory().newRSSFeedRepresentation(this, getFeed()));
			getRssFeedRepresentation().represent();

			updateRequestTimer();

			// send a new feed (only with new events) to Broker, if we didn't get the message from
			// him
			if ( fm.getSrc() != getBroker() ) {

				RSSFeed newFeed = getRssEventFeedFactory().newRSSEventFeed(newEvents, fm.getFeed().getGeneralContent());
				new RSSFeedMessage(this, getBroker(), newFeed, fm.getRssFeedRepresentation().copyWith(null, newFeed), params);

			} else {
				// if we got the message from a broker, a request for RSSServer will be omitted
				// -> statistics
				this.omittedRSSFeedRequestNotifier.notifyObservers(this);
			}

		} else {

			// got an old feed; update timer only if sender is RSSServer
			if ( fm.getSrc() == getRssServer() ) {
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
	 * @param rssEventFeedFactory The rssEventFeedFactory to set.
	 */
	public void setRssEventFeedFactory(RSSEventFeedFactory rssEventFeedFactory) {
		this.rssEventFeedFactory = rssEventFeedFactory;
	}

}
