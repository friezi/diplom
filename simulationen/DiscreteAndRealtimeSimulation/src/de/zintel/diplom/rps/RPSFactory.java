package de.zintel.diplom.rps;

import de.zintel.diplom.rps.broker.BrokerNode;
import de.zintel.diplom.rps.pubsub.PubSubNode;
import de.zintel.diplom.rps.server.RSSServerNode;
import de.zintel.diplom.simulation.SimParameters;

/**
 * For construction RSS-Servers, Publishers/Subscribers and Brokers.
 * 
 * @author Friedemann Zintel
 * 
 */
public interface RPSFactory {

	/**
	 * @param xp
	 *            x-position
	 * @param yp
	 *            y-position
	 * @param params
	 *            relevant parameters
	 * @return a new BrokerNode
	 */
	BrokerNode newBrokerNode(int xp, int yp, SimParameters params) throws Exception;

	/**
	 * @param xp
	 *            xp x-position
	 * @param yp
	 *            y-position
	 * @param params
	 *            relevant parameters
	 * @return a new PubSubNode
	 */
	PubSubNode newPubSubNode(int xp, int yp, SimParameters params) throws Exception;

	/**
	 * @param xp
	 *            xp x-position
	 * @param yp
	 *            y-position
	 * @param params
	 *            relevant parameters
	 * @return a new RSSServerNode
	 */
	RSSServerNode newRSSServerNode(int xp, int yp, SimParameters params) throws Exception;

}
