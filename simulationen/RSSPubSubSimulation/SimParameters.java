/**
 * To hold the relevant parameters for the simulation
 */

/**
 * @author friezi
 * 
 */
public class SimParameters {

	/**
	 * RSSServerNode: minimum update-interval
	 */
	int minUpIntv;

	/**
	 * RSSServerNode: maximum update-interval
	 */
	int maxUpIntv;

	/**
	 * RSSServerNode: time to life of the feed
	 */
	int ttl;

	/**
	 * PubSubNode: spread-factor for update-interval of the feeds from the
	 * publishers
	 */
	int spreadFactor;

	/**
	 * RSSFeedRequestMessage: runtime for message RSSFeedRequestMessage
	 */
	int rssFdReqMsgRT;

	/**
	 * RSSFeedMessage: runtime for message RSSFeedMessage
	 */
	int rssFdMsgRT;

	/**
	 * RSSFeedMessage: if true, RSSFeedMessages will represent the feed
	 */
	boolean rssFdMsgRepr;

}
