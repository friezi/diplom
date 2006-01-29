import rsspubsubframework.DisplayableObject;

/**
 * A Simulation ofn a RSS-Distribution-System integrated in distributed
 * Publish/Subscribe
 * 
 * @author friezi
 * 
 */
public class SimRPSContIntervall {

	protected static int minUpIntv = 5;

	protected static int maxUpIntv = 30;

	protected static int ttl = 10;

	protected static int spreadFactor = 3;

	protected static int rssFdReqMsgRT = 10;

	protected static int rssFdMsgRT = 5;

	protected static boolean rssFdMsgRepr = true;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		SimParameters params = new SimParameters();

		params.minUpIntv = minUpIntv;
		params.maxUpIntv = maxUpIntv;
		params.ttl = ttl;
		params.spreadFactor = spreadFactor;
		params.rssFdReqMsgRT = rssFdReqMsgRT;
		params.rssFdMsgRT = rssFdMsgRT;
		params.rssFdMsgRepr = rssFdMsgRepr;

		SzenarioAllForOne szenario = new SzenarioAllForOne(new RPSFactory() {
			public BrokerNode newBrokerNode(int xp, int yp, SimParameters params) {
				return new Broker(xp, yp, params);
			}

			public PubSubNode newPubSubNode(int xp, int yp, SimParameters params) {
				return new PubSub(xp, yp, params);
			}

			public RSSServerNode newRSSServerNode(int xp, int yp, SimParameters params) {
				return new RSSServer(xp, yp, params);
			}
		}, new ColorFeedFactory(), new RSSFeedRepresentationFactory() {
			public RSSFeedRepresentation newRSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed) {
				return new ColorFeedRepresentation(dObj, (ColorFeed) feed);
			}
		}, params);
	}

}
