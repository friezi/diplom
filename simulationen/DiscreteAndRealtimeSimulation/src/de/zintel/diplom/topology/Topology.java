package de.zintel.diplom.topology;
/**
 * 
 */

import java.util.*;

import de.zintel.diplom.feed.RSSFeedFactory;
import de.zintel.diplom.feed.RSSFeedRepresentationFactory;
import de.zintel.diplom.rps.RPSFactory;
import de.zintel.diplom.rps.pubsub.PubSubNode;
import de.zintel.diplom.rps.server.RSSServerNode;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.SimParameters;

/**
 * @author Friedemann Zintel
 * 
 */
public abstract class Topology {

	RPSFactory rpsFactory;

	RSSFeedFactory rssFeedFactory;

	RSSFeedRepresentationFactory rssFeedRepresentationFactory;

	SimParameters params;

	public Topology(RPSFactory rpsFactory, RSSFeedFactory rssFeedFactory, RSSFeedRepresentationFactory rssFeedRepresentationFactory,
			SimParameters params) throws Exception {

		this.rpsFactory = rpsFactory;
		this.rssFeedFactory = rssFeedFactory;
		this.rssFeedRepresentationFactory = rssFeedRepresentationFactory;
		this.params = params;

	}

	protected void setStatisticObservers(RSSServerNode rssServer, LinkedList<PubSubNode> pubsubs) {

		// add the Engine as observer to RSSServer
		rssServer.getStatistics().addReceivedRSSFeedRequestObserver(Engine.getSingleton().getRpsStatistics().getReceivedRSSFeedRequestObserver());
		rssServer.getStatistics().addRequestsInQueueObserver(Engine.getSingleton().getRpsStatistics().getRequestsInQueueObserver());
		rssServer.getStatistics().addUnrepliedRequestsObserver(Engine.getSingleton().getRpsStatistics().getUnrepliedRequestsObserver());
		rssServer.getStatistics().addTotalTemporaryRequestsObserver(Engine.getSingleton().getRpsStatistics().getTotalTemporaryRequestsObserver());

		for (PubSubNode pubsub : pubsubs) {
			// Engine should be notified about omitted RSS-requests and other
			// values
			pubsub.getStatistics().addOmittedRSSFeedRequestObserver(Engine.getSingleton().getRpsStatistics().getOmittedRSSFeedRequestObserver());
			pubsub.getStatistics().addServerFeedObserver(Engine.getSingleton().getRpsStatistics().getServerFeedObserver());
			pubsub.getStatistics().addBrokerFeedObserver(Engine.getSingleton().getRpsStatistics().getBrokerFeedObserver());
			pubsub.getStatistics().addUptodateRatioObserver(Engine.getSingleton().getRpsStatistics().getAverageUptodateRatioUpdater());
			pubsub.getStatistics().addMessageDelayRatioObserver(Engine.getSingleton().getRpsStatistics().getAverageMessageDelayRatioUpdater());
			pubsub.getStatistics().addCurrPollPeriodObserver(Engine.getSingleton().getRpsStatistics().getCurrPollPeriodsObserver());
		}

	}

}
