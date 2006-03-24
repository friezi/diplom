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
	BrokerNode newBrokerNode(int xp, int yp, SimParameters params);

	/**
	 * @param xp
	 *            xp x-position
	 * @param yp
	 *            y-position
	 * @param params
	 *            relevant parameters
	 * @return a new PubSubNode
	 */
	PubSubNode newPubSubNode(int xp, int yp, SimParameters params);

	/**
	 * @param xp
	 *            xp x-position
	 * @param yp
	 *            y-position
	 * @param params
	 *            relevant parameters
	 * @return a new RSSServerNode
	 */
	RSSServerNode newRSSServerNode(int xp, int yp, SimParameters params);

}
