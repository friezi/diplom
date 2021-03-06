package de.zintel.diplom.rps.broker;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import de.zintel.diplom.event.Event;
import de.zintel.diplom.feed.EventFeed;
import de.zintel.diplom.feed.RSSEventFeedFactory;
import de.zintel.diplom.feed.RSSFeed;
import de.zintel.diplom.feed.RSSFeedGeneralContent;
import de.zintel.diplom.messages.RSSFeedMessage;
import de.zintel.diplom.messages.RSSFeedRichMessage;
import de.zintel.diplom.rps.pubsub.PubSubNode;
import de.zintel.diplom.rps.server.AbstractUploader;
import de.zintel.diplom.rps.server.AbstractUploaderFactory;
import de.zintel.diplom.simulation.Message;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.synchronization.AbstractThreadFactory;

/**
 */

/**
 * A Broker which receives Feeds consisting of events. Only the new events will
 * be sent whithin a new feed to all neighbours. The new events will be stored
 * at the broker upto a maximum size of SimParams.maxSubscriberEvents. It also
 * checks, if a received RSSFeedMessage is a RSSFeedRichMessage and stores all
 * additional including information in the new resent-message.
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

		/*
		 * (non-Javadoc)
		 * 
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
				if ( events.contains(event) == false ) {

					events.addLast(event);
					newEvents.addLast(event);
				}
			}

			// deletion of event-overhead
			while ( events.size() > params.getMaxSubscriberEvents() )
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
					if ( broker != fm.getOrigin() ) {
						m = new RSSFeedRichMessage(ourself, broker, newFeed, fm.getRssFeedRepresentation().copyWith(null, newFeed), params);
						if ( fm instanceof RSSFeedRichMessage )
							((RSSFeedRichMessage) m).copyRichInformation((RSSFeedRichMessage) fm);
						m.send();
						uploader.upload(m);
					}
				}

				for ( PubSubNode subscriber : subscribers ) {
					if ( subscriber != fm.getOrigin() ) {
						m = new RSSFeedRichMessage(ourself, subscriber, newFeed, fm.getRssFeedRepresentation().copyWith(null, newFeed), params);
						if ( fm instanceof RSSFeedRichMessage )
							((RSSFeedRichMessage) m).copyRichInformation((RSSFeedRichMessage) fm);
						m.send();
						uploader.upload(m);
					}
				}

			}

		}

	}

	protected RSSEventFeedFactory rssEventFeedFactory;

	private AbstractThreadFactory threadfactory;

	private AbstractUploaderFactory uploaderfactory;

	private AbstractUploader uploader;

	LinkedList<Event> events = new LinkedList<Event>();

	public AdjustingEventBroker(int xp, int yp, RSSEventFeedFactory rssEventFeedFactory, SimParameters params) throws Exception {

		super(xp, yp, params);
		threadfactory = new AbstractThreadFactory(params);
		uploaderfactory = new AbstractUploaderFactory(params);
		uploader = uploaderfactory.newUploader(this, AbstractUploaderFactory.BROKER_U);
		setRssEventFeedFactory(rssEventFeedFactory);
		feed = rssEventFeedFactory.newRSSEventFeed(new LinkedList<Event>(), new RSSFeedGeneralContent());
		
	}

	protected void handleRSSFeedMessage(RSSFeedMessage fm) {

		if ( fm instanceof RSSFeedRichMessage )
			handleRSSFeedRichMessage((RSSFeedRichMessage) fm);

		RSSFeedHandler handler = new RSSFeedHandler(this, fm);

		getThreadfactory().newThread(handler).start();

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
	 * @return Returns the threadfactory.
	 */
	public AbstractThreadFactory getThreadfactory() {
		return threadfactory;
	}

	/**
	 * @return Returns the uploaderfactory.
	 */
	public AbstractUploaderFactory getUploaderfactory() {
		return uploaderfactory;
	}

}
