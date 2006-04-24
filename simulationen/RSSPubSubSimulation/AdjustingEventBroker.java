import java.util.*;

import rsspubsubframework.*;

/**
 */

/**
 * A Broker which receives Feeds consisting of events. Only the new events will
 * be sent whithin a new feed to all neighbours. The new events will be stored
 * at the broker upto a maximum size of SimParams.maxSubscriberEvents.
 * 
 * @author Friedemann Zintel
 * 
 */
public class AdjustingEventBroker extends AdjustingBroker {

	protected class RSSFeedHandler implements Runnable {

		Node ourself;

		RSSFeedMessage fm;

		public RSSFeedHandler(Node ourself, RSSFeedMessage fm) {
			this.ourself = ourself;
			this.fm = fm;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {

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
				RSSFeed newFeed = getRssEventFeedFactory().newRSSEventFeed(newEvents, fm.getFeed().getGeneralContent());

				Set<BrokerNode> brokers = getBrokers();
				Set<PubSubNode> subscribers = getSubscribers();
				Message m;

				// inform first all brokers, then subscribers, simulate upload
				for ( BrokerNode broker : brokers ) {
					if ( broker != fm.getSrc() ) {
						(m = new RSSFeedMessage(ourself, broker, newFeed, fm.getRssFeedRepresentation().copyWith(null, newFeed), params)).send();
						upload(m);
					}
				}

				for ( PubSubNode subscriber : subscribers ) {
					if ( subscriber != fm.getSrc() ) {
						(m = new RSSFeedMessage(ourself, subscriber, newFeed, fm.getRssFeedRepresentation().copyWith(null, newFeed), params)).send();
						upload(m);
					}
				}

			}

		}

	}

	protected RSSEventFeedFactory rssEventFeedFactory;

	LinkedList<Event> events = new LinkedList<Event>();

	public AdjustingEventBroker(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) {

		super(xp, yp, params);
		setRssEventFeedFactory(rssEventFeedFactory);
	}

	protected void handleRSSFeedMessage(RSSFeedMessage fm) {

		RSSFeedHandler handler = new RSSFeedHandler(this, fm);

		new Thread(handler).start();

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
