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
 * 
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class TopologyOneSurrounded extends Topology {

	private static int MAXBROKER = 25;

	private static int BR_XPOS = 100;

	private static int BR_XINTV = 60; // intervall between Broker-x-Positions

	private static int BR_YPOS = 850;

	private static int BR_YINTV = 70; // intervall between Broker-y-Positions

	private static int BR_YPOS2 = 150;

	private static int BR_MID_XOFFS = 50;

	private static int PS_XPOS = BR_XPOS - 15;

	private static int PS_XINTV = BR_XINTV / 4;

	private static int PS_YPOS = 750;

	private static int PS_YPOS2 = 250;

	private static int RSS_XPOS = BR_XPOS + ((((MAXBROKER + 1) / 2) - 1) * BR_XINTV) / 2;

	private static int RSS_YPOS = 500;

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
	public TopologyOneSurrounded(RPSFactory rpsFactory, RSSFeedFactory rssFeedFactory, RSSFeedRepresentationFactory rssFeedRepresentationFactory,
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

		for ( int i = 0; i < ((int) (MAXBROKER / 2)) + 1; i++ ) {
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
		edgefactory.newEdge(brokerlist.get(14), brokerlist.get(15)).register();
		edgefactory.newEdge(brokerlist.get(15), brokerlist.get(16)).register();
		edgefactory.newEdge(brokerlist.get(16), brokerlist.get(17)).register();
		edgefactory.newEdge(brokerlist.get(17), brokerlist.get(18)).register();
		edgefactory.newEdge(brokerlist.get(18), brokerlist.get(19)).register();
		edgefactory.newEdge(brokerlist.get(19), brokerlist.get(20)).register();
		edgefactory.newEdge(brokerlist.get(20), brokerlist.get(21)).register();
		edgefactory.newEdge(brokerlist.get(21), brokerlist.get(22)).register();
		edgefactory.newEdge(brokerlist.get(22), brokerlist.get(23)).register();
		edgefactory.newEdge(brokerlist.get(23), brokerlist.get(24)).register();

		for ( int i = 0; i < ((int) (MAXBROKER / 2)) + 1; i++ ) {
			brokerlist.add(rpsFactory.newBrokerNode(BR_XPOS + BR_XINTV * i, BR_YPOS2, params).register());
			if ( i < ((int) (MAXBROKER / 2)) )
				brokerlist.add(rpsFactory.newBrokerNode(BR_XPOS + BR_XINTV * i + BR_XINTV / 2, BR_YPOS2 - BR_YINTV, params).register());
		}

		edgefactory.newEdge(brokerlist.get(0 + MAXBROKER), brokerlist.get(1 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(0 + MAXBROKER), brokerlist.get(2 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(2 + MAXBROKER), brokerlist.get(3 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(2 + MAXBROKER), brokerlist.get(4 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(2 + MAXBROKER), brokerlist.get(5 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(4 + MAXBROKER), brokerlist.get(7 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(7 + MAXBROKER), brokerlist.get(8 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(6 + MAXBROKER), brokerlist.get(8 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(8 + MAXBROKER), brokerlist.get(9 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(9 + MAXBROKER), brokerlist.get(10 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(10 + MAXBROKER), brokerlist.get(12 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(12 + MAXBROKER), brokerlist.get(13 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(13 + MAXBROKER), brokerlist.get(11 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(13 + MAXBROKER), brokerlist.get(14 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(14 + MAXBROKER), brokerlist.get(15 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(15 + MAXBROKER), brokerlist.get(16 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(16 + MAXBROKER), brokerlist.get(17 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(17 + MAXBROKER), brokerlist.get(18 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(18 + MAXBROKER), brokerlist.get(19 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(19 + MAXBROKER), brokerlist.get(20 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(20 + MAXBROKER), brokerlist.get(21 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(21 + MAXBROKER), brokerlist.get(22 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(22 + MAXBROKER), brokerlist.get(23 + MAXBROKER)).register();
		edgefactory.newEdge(brokerlist.get(23 + MAXBROKER), brokerlist.get(24 + MAXBROKER)).register();

		// the PubSubs:

		LinkedList<PubSubNode> pubsublist = new LinkedList<PubSubNode>();

		for ( int i = 0; i < 2 * MAXBROKER; i++ ) {

			PubSubNode pubsub = rpsFactory.newPubSubNode(PS_XPOS + PS_XINTV * i, PS_YPOS, params).register();
			pubsublist.add(pubsub);
			pubsub.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);

			// connect broker and pubsub
			BrokerNode broker = brokerlist.get((int) (i / 2));
			edgefactory.newEdge(pubsub, broker).register();

		}

		for ( int i = 0; i < 2 * MAXBROKER; i++ ) {

			PubSubNode pubsub = rpsFactory.newPubSubNode(PS_XPOS + PS_XINTV * i, PS_YPOS2, params).register();
			pubsublist.add(pubsub);
			pubsub.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);

			// connect broker and pubsub
			BrokerNode broker = brokerlist.get((int) (i / 2) + MAXBROKER);
			edgefactory.newEdge(pubsub, broker).register();

		}

		// the left-side and right-side brokers with publishers

		PubSubNode pubsub;
		int xpos, ypos;
		xpos = BR_XPOS - BR_MID_XOFFS;
		ypos = RSS_YPOS;
		BrokerNode bn1 = rpsFactory.newBrokerNode(xpos, ypos, params).register();
		brokerlist.add(bn1);
		pubsub = rpsFactory.newPubSubNode(xpos + BR_XINTV, ypos - 10, params).register();
		pubsublist.add(pubsub);
		pubsub.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);
		edgefactory.newEdge(pubsub, bn1).register();
		pubsub = rpsFactory.newPubSubNode(xpos + BR_XINTV, ypos + 10, params).register();
		pubsublist.add(pubsub);
		pubsub.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);
		edgefactory.newEdge(pubsub, bn1).register();
		{
			BrokerNode bntmp1 = bn1;
			BrokerNode bntmp2 = bn1;
			for ( int i = 1; i < 3; i++ ) {
				xpos = BR_XPOS - BR_MID_XOFFS;
				ypos = RSS_YPOS - i * BR_YINTV;
				BrokerNode b1 = rpsFactory.newBrokerNode(xpos, ypos, params).register();
				brokerlist.add(b1);
				edgefactory.newEdge(bntmp1, b1).register();
				pubsub = rpsFactory.newPubSubNode(xpos + BR_XINTV, ypos - 10, params).register();
				pubsublist.add(pubsub);
				pubsub.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);
				edgefactory.newEdge(pubsub, b1).register();
				pubsub = rpsFactory.newPubSubNode(xpos + BR_XINTV, ypos + 10, params).register();
				pubsublist.add(pubsub);
				pubsub.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);
				edgefactory.newEdge(pubsub, b1).register();
				bntmp1 = b1;

				xpos = BR_XPOS - BR_MID_XOFFS;
				ypos = RSS_YPOS + i * BR_YINTV;
				BrokerNode b2 = rpsFactory.newBrokerNode(xpos, ypos, params).register();
				brokerlist.add(b2);
				edgefactory.newEdge(bntmp2, b2).register();
				pubsub = rpsFactory.newPubSubNode(xpos + BR_XINTV, ypos - 10, params).register();
				pubsublist.add(pubsub);
				pubsub.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);
				edgefactory.newEdge(pubsub, b2).register();
				pubsub = rpsFactory.newPubSubNode(xpos + BR_XINTV, ypos + 10, params).register();
				pubsublist.add(pubsub);
				pubsub.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);
				edgefactory.newEdge(pubsub, b2).register();
				bntmp2 = b2;
			}
		}

		xpos = BR_XPOS + (MAXBROKER / 2) * BR_XINTV + BR_MID_XOFFS;
		ypos = RSS_YPOS;
		BrokerNode bn2 = rpsFactory.newBrokerNode(xpos, ypos, params).register();
		brokerlist.add(bn2);
		pubsub = rpsFactory.newPubSubNode(xpos - BR_XINTV, ypos - 10, params).register();
		pubsublist.add(pubsub);
		pubsub.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);
		edgefactory.newEdge(pubsub, bn2).register();
		pubsub = rpsFactory.newPubSubNode(xpos - BR_XINTV, ypos + 10, params).register();
		pubsublist.add(pubsub);
		pubsub.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);
		edgefactory.newEdge(pubsub, bn2).register();
		{
			BrokerNode bntmp1 = bn2;
			BrokerNode bntmp2 = bn2;
			for ( int i = 1; i < 3; i++ ) {

				xpos = BR_XPOS + (MAXBROKER / 2) * BR_XINTV + BR_MID_XOFFS;
				ypos = RSS_YPOS - i * BR_YINTV;
				BrokerNode b1 = rpsFactory.newBrokerNode(xpos, ypos, params).register();
				brokerlist.add(b1);
				edgefactory.newEdge(bntmp1, b1).register();
				pubsub = rpsFactory.newPubSubNode(xpos - BR_XINTV, ypos - 10, params).register();
				pubsublist.add(pubsub);
				pubsub.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);
				edgefactory.newEdge(pubsub, b1).register();
				pubsub = rpsFactory.newPubSubNode(xpos - BR_XINTV, ypos + 10, params).register();
				pubsublist.add(pubsub);
				pubsub.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);
				edgefactory.newEdge(pubsub, b1).register();
				bntmp1 = b1;

				xpos = BR_XPOS + (MAXBROKER / 2) * BR_XINTV + BR_MID_XOFFS;
				ypos = RSS_YPOS + i * BR_YINTV;
				BrokerNode b2 = rpsFactory.newBrokerNode(xpos, ypos, params).register();
				edgefactory.newEdge(bntmp2, b2).register();
				brokerlist.add(b2);
				pubsub = rpsFactory.newPubSubNode(xpos - BR_XINTV, ypos - 10, params).register();
				pubsublist.add(pubsub);
				pubsub.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);
				edgefactory.newEdge(pubsub, b2).register();
				pubsub = rpsFactory.newPubSubNode(xpos - BR_XINTV, ypos + 10, params).register();
				pubsublist.add(pubsub);
				pubsub.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);
				edgefactory.newEdge(pubsub, b2).register();
				bntmp2 = b2;

			}
		}

		edgefactory.newEdge(brokerlist.get(0), brokerlist.get(brokerlist.size() - 6)).register();
		edgefactory.newEdge(brokerlist.get(MAXBROKER - 1), brokerlist.get(brokerlist.size() - 1)).register();
		edgefactory.newEdge(brokerlist.get(2 * MAXBROKER - 1), brokerlist.get(brokerlist.size() - 2)).register();

		for ( PubSubNode currpubsub : pubsublist ) {
			// connect RSSServer and pubsub
			edgefactory.newEdge(rssServer, currpubsub).register();
			currpubsub.setRSSServer(rssServer);
		}

		rssServer.addToInitList();

		for ( BrokerNode broker : brokerlist )
			broker.addToInitList();

		// pubsublist.get(0).addToInitList();

		for ( PubSubNode currpubsub : pubsublist ) {
			currpubsub.addToInitList();
		}

		// all setup is done, gui-observers should be removed

		for ( BrokerNode broker : brokerlist )
			broker.deleteGuiObservers();

		for ( PubSubNode currpubsub : pubsublist )
			currpubsub.deleteGuiObservers();

		// set observers for statistics
		setStatisticObservers(rssServer, pubsublist);

	}
}
