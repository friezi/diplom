import rsspubsubframework.DisplayableObject;

/**
 * A Simulation ofn a RSS-Distribution-System integrated in distributed
 * Publish/Subscribe
 * 
 * @author friezi
 * 
 */
public class SimRPSContIntervall {

	protected static int minUpIntv = 1;

	protected static int maxUpIntv = 1;

	protected static int ttl = 7;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Szenario1 szenario = new Szenario1(new RPSFactory() {
			public BrokerNode newBrokerNode(int xp, int yp) {
				return new Broker(xp, yp);
			}

			public PubSubNode newPubSubNode(int xp, int yp) {
				return new PubSub(xp, yp);
			}

			public RSSServerNode newRSSServerNode(int xp, int yp, int minUpIntv, int maxUpIntv, int ttl) {
				return new RSSServer(xp, yp, minUpIntv, maxUpIntv, ttl);
			}
		}, new ColorFeedFactory(), new RSSFeedRepresentationFactory() {
			public RSSFeedRepresentation newRSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed) {
				return new ColorFeedRepresentation(dObj, (ColorFeed) feed);
			}
		}, minUpIntv, maxUpIntv, ttl);
	}

}
