package de.zintel.diplom.topology;
import java.util.LinkedList;

import de.zintel.diplom.feed.RSSFeedFactory;
import de.zintel.diplom.feed.RSSFeedRepresentationFactory;
import de.zintel.diplom.rps.RPSFactory;
import de.zintel.diplom.rps.broker.BrokerNode;
import de.zintel.diplom.rps.pubsub.PubSubNode;
import de.zintel.diplom.rps.server.RSSServerNode;
import de.zintel.diplom.simulation.EdgeFactory;
import de.zintel.diplom.simulation.SimParameters;

/**
 * A Topology consisting of one RSS-Server and several Brokers, each o them has
 * two Publishers/Subscribers connected to.
 * 
 * @author Friedemann Zintel
 * 
 * 
 */
public class TopologyAllForOne extends Topology {

	private static int MAXBROKER = 15;

	private static int BR_XPOS = 150;

	private static int BR_XINTV = 100; // interval between Broker-x-Positions

	private static int BR_YPOS = 700;

	private static int BR_YINTV = 80; // interval between Broker-y-Positions

	private static int PS_XPOS = BR_XPOS - 15;

	private static int PS_XINTV = 25;

	private static int PS_YPOS = 550;

	private static int RSS_XPOS = BR_XPOS + ((((MAXBROKER + 1) / 2) - 1) * BR_XINTV) / 2;

	private static int RSS_YPOS = 250;

	/**
	 * @param rpsFactory
	 *            the factory to construct RSS-Server,Publishers and Brokers
	 * @param rssFeedFactory
	 *            to construct feeds
	 * @param rssFeedRepresentationFactory
	 *            to construct feed-representations
	 * @param params
	 *            relevant parameters
	 */
	public TopologyAllForOne(RPSFactory rpsFactory, RSSFeedFactory rssFeedFactory, RSSFeedRepresentationFactory rssFeedRepresentationFactory,
			SimParameters params) throws Exception {

		super(rpsFactory, rssFeedFactory, rssFeedRepresentationFactory, params);

		EdgeFactory edgefactory = new EdgeFactory();

		// RSS-Server:

		RSSServerNode rssServer = rpsFactory.newRSSServerNode(RSS_XPOS, RSS_YPOS, params).register();

		// set the factories for creating and displaying the feeds
		rssServer.setRssFeedFactory(rssFeedFactory);
		rssServer.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);

		// the Brokers:

		LinkedList<BrokerNode> brokerlist = new LinkedList<BrokerNode>();

		for (int i = 0; i < ((int) (MAXBROKER / 2)) + 1; i++) {
			brokerlist.add(rpsFactory.newBrokerNode(BR_XPOS + BR_XINTV * i, BR_YPOS, params).register());
			if ( i < ((int) (MAXBROKER / 2)) )
				brokerlist.add(rpsFactory.newBrokerNode(BR_XPOS + BR_XINTV * i + BR_XINTV / 2, BR_YPOS + BR_YINTV, params).register());
		}

		edgefactory.newEdge(brokerlist.get(0), brokerlist.get(1)).register();
		edgefactory.newEdge(brokerlist.get(0), brokerlist.get(2)).register();
		edgefactory.newEdge(brokerlist.get(2), brokerlist.get(3)).register();
		edgefactory.newEdge(brokerlist.get(2), brokerlist.get(4)).register();
		edgefactory.newEdge(brokerlist.get(2), brokerlist.get(5)).register();
		edgefactory.newEdge(brokerlist.get(4), brokerlist.get(7)).register();
		edgefactory.newEdge(brokerlist.get(7), brokerlist.get(8)).register();
		edgefactory.newEdge(brokerlist.get(6), brokerlist.get(8)).register();
		edgefactory.newEdge(brokerlist.get(8), brokerlist.get(9)).register();
		edgefactory.newEdge(brokerlist.get(9), brokerlist.get(10)).register();
		edgefactory.newEdge(brokerlist.get(10), brokerlist.get(12)).register();
		edgefactory.newEdge(brokerlist.get(12), brokerlist.get(13)).register();
		edgefactory.newEdge(brokerlist.get(13), brokerlist.get(11)).register();
		edgefactory.newEdge(brokerlist.get(13), brokerlist.get(14)).register();

		// the PubSubs:

		LinkedList<PubSubNode> pubsublist = new LinkedList<PubSubNode>();

		for (int i = 0; i < 2 * MAXBROKER; i++) {

			PubSubNode pubsub = rpsFactory.newPubSubNode(PS_XPOS + PS_XINTV * i, PS_YPOS, params).register();
			pubsublist.add(pubsub);
			pubsub.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);

			// connect RSSServer and pubsub
			edgefactory.newEdge(rssServer, pubsub).register();
			pubsub.setRSSServer(rssServer);

			// connect broker and pubsub
			BrokerNode broker = brokerlist.get((int) (i / 2));
			edgefactory.newEdge(pubsub, broker).register();
			//			pubsub.setBroker(broker);

		}

		rssServer.addToInitList();

		// pubsublist.get(0).addToInitList();

		for (PubSubNode pubsub : pubsublist) {
			pubsub.addToInitList();
		}

		// all setup is done, gui-observers should be removed

		for (BrokerNode broker : brokerlist)
			broker.deleteGuiObservers();

		for (PubSubNode currpubsub : pubsublist)
			currpubsub.deleteGuiObservers();

	}
}
