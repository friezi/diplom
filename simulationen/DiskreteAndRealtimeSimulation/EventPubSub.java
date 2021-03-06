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

			if ( event1.getGeneralContend().getPubDate().before(event2.getGeneralContend().getPubDate()) )
				return -1;
			else if ( event1.getGeneralContend().getPubDate().after(event2.getGeneralContend().getPubDate()) )
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

		if ( !(feed instanceof EventFeed) ) {
			System.out.println("Internal Error: AdjustingEventFeed: handleRSSFeedMessage(): feed is not instance of EventFeed!");
			System.exit(1);
		}

		// store all additional information, if contained
		if ( fm instanceof RSSFeedRichMessage )
			storeRichInformation((RSSFeedRichMessage) fm);

		Iterator<Event> it = ((EventFeed) feed).eventIterator();
		Event event;
		TreeSet<Event> sortedEvents = new TreeSet<Event>(new EventDateComparator());

		// store all new events and send them to neighbours
		while (it.hasNext()) {

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
		while (events.size() > params.maxSubscriberEvents)
			events.removeFirst();

		LinkedList<Event> newEvents = new LinkedList<Event>(sortedEvents);

		if ( newEvents.size() > 0 ) {
			// it's a new feed

			// show the feed
			setFeed(getRssEventFeedFactory().newRSSEventFeed(events, fm.getFeed().getGeneralContent()));
			setRssFeedRepresentation(getRssFeedRepresentationFactory().newRSSFeedRepresentation(this, getFeed()));
			getRssFeedRepresentation().represent();

			// send a new feed (only with new events) to Broker, if we didn't
			// get the message from
			// him
			if ( brokerlist.contains(fm.getSrc()) == false ) {
				// sender is server

				updateRequestTimerByNewFeedFromServer();

				this.getStatistics().addServerFeed(this);

				RSSFeed newFeed = getRssEventFeedFactory().newRSSEventFeed(newEvents, fm.getFeed().getGeneralContent());
				RSSFeedRichMessage m;
				// send it to all brokers
				for (BrokerNode broker : brokerlist) {
					m = new RSSFeedRichMessage(this, broker, newFeed, fm.getRssFeedRepresentation().copyWith(null, newFeed), params);
					addRichInformation(m);
					m.send();
				}

			} else {
				// sender is a broker

				updateRequestTimerByNewFeedFromBroker();

				// just statistics
				this.getStatistics().addBrokerFeed(this);
				// if we got the message from a broker, a request for RSSServer
				// will be omitted
				// -> statistics
				this.getStatistics().addOmittedRSSFeedRequest(this);

				RSSFeedRichMessage m;
				// send it to all other brokers
				for (BrokerNode broker : brokerlist)
					if ( broker != fm.getSrc() ) {
						m = new RSSFeedRichMessage(this, broker, getFeed(), fm.getRssFeedRepresentation().copyWith(null, getFeed()), params);
						addRichInformation(m);
						m.send();
					}

			}

		} else {
			// it's an old feed

			// got an old feed; update timer only if sender is RSSServer
			if ( fm.getSrc() == getRssServer() ) {
				updateRequestTimerByOldFeedFromServer();
			} else {
				updateRequestTimerByOldFeedFromBroker();
			}
		}

	}

	protected void calculateUpdateAndDelay(long eventtime) {

		long now = new Date().getTime();
		long diffsecs = (now - eventtime) / 1000;
		long overhead = diffsecs - getPreferredPollingRate();
		int messageDelayRatio = 0;
		int uptodateRatio = 100;
		if ( overhead > 0 ) {

			messageDelayRatio = (int) ((overhead * 100) / getPreferredPollingRate());
			uptodateRatio = (int) ((getPreferredPollingRate() * 100) / diffsecs);
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

}
