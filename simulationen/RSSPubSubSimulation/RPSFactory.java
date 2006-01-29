/**
 * For construction RSS-Servers, Publishers/Subscribers and Brokers.
 * 
 * @author friezi
 * 
 */
public interface RPSFactory {

	/**
	 * @param xp
	 *            x-position
	 * @param yp
	 *            y-position
	 * @return a new BrokerNode
	 */
	BrokerNode newBrokerNode(int xp, int yp);

	/**
	 * @param xp
	 *            xp x-position
	 * @param yp
	 *            y-position
	 * @return a new PubSubNode
	 */
	PubSubNode newPubSubNode(int xp, int yp);

	/**
	 * @param xp
	 *            xp x-position
	 * @param yp
	 *            y-position
	 * @param minUpIntv
	 *            minimal update-intervall for RSS-feeds
	 * @param minUpIntvOffs
	 *            an offset added to the minimal update-intervall for RSS-feeds;
	 *            the purpose is to facilitate the possibility of achieving
	 *            always much greater update-intervals than clients expect.
	 * @param maxUpIntv
	 *            maximal update-intervall for RSS-feeds
	 * @return a new RSSServerNode
	 */
	RSSServerNode newRSSServerNode(int xp, int yp, int minUpIntv, int minUpIntvOffs, int maxUpIntv);

}
