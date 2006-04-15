/**
 * 
 */

import java.util.*;

import rsspubsubframework.Engine;

/**
 * @author Friedemann Zintel
 *
 */
public abstract class Szenario {

	protected void setStatisticObservers(RSSServerNode rssServer, LinkedList<PubSubNode> pubsubs) {

		// add the Engine as observer to RSSServer
		rssServer.getStatistics().addReceivedRSSFeedRequestObserver(Engine.getSingleton().getRpsStatistics().getReceivedRSSFeedRequestObserver());
		rssServer.getStatistics().addRequestsInQueueObserver(Engine.getSingleton().getRpsStatistics().getRequestsInQueueObserver());
		rssServer.getStatistics().addUnrepliedRequestsObserver(Engine.getSingleton().getRpsStatistics().getUnrepliedRequestsObserver());

		for ( PubSubNode pubsub : pubsubs ) {
			// Engine should be notified about omitted RSS-requests and other
			// values
			pubsub.getStatistics().addOmittedRSSFeedRequestObserver(Engine.getSingleton().getRpsStatistics().getOmittedRSSFeedRequestObserver());
			pubsub.getStatistics().addServerFeedObserver(Engine.getSingleton().getRpsStatistics().getServerFeedObserver());
			pubsub.getStatistics().addBrokerFeedObserver(Engine.getSingleton().getRpsStatistics().getBrokerFeedObserver());
			pubsub.getStatistics().addUptodateRatioObserver(Engine.getSingleton().getRpsStatistics().getAverageUptodateRatioUpdater());
			pubsub.getStatistics().addMessageDelayRatioObserver(Engine.getSingleton().getRpsStatistics().getAverageMessageDelayRatioUpdater());
		}

	}

}
