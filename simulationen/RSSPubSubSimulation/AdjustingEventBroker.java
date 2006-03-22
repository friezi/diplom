import java.util.*;

import rsspubsubframework.Node;

/**
 * A Broker which receives Feeds consisting of events. Only the new events will be sent whithin a new feed to
 * all neighbours. The new events will be stored at the broker upto a maximum size of SimParams.maxSubscriberEvents.
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class AdjustingEventBroker extends AdjustingBroker {

	protected RSSEventFeedFactory rssEventFeedFactory;

	LinkedList<Event> events = new LinkedList<Event>();

	public AdjustingEventBroker(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {

		super(xp, yp, params);
		setRssEventFeedFactory(rssEventFeedFactory);
		// TODO Auto-generated constructor stub
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

			setFeed(getRssEventFeedFactory().newRSSEventFeed(events, fm.getFeed().getGeneralContent()));

			// send a new RSSFeedMessage to all other Brokers and
			// Subscribers
			Set<Node> peers = getPeers();
			RSSFeed newFeed = getRssEventFeedFactory().newRSSEventFeed(newEvents, fm.getFeed().getGeneralContent());

			for ( Node peer : peers ) {
				if ( peer != fm.getSrc() )
					new RSSFeedMessage(this, peer, newFeed, fm.getRssFeedRepresentation().copyWith(null, newFeed), params);
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