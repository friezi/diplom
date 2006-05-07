import java.io.IOException;

import rsspubsubframework.*;

/**
 * A Simulation ofn a RSS-Distribution-System integrated in distributed
 * Publish/Subscribe
 * 
 * @author Friedemann Zintel
 * 
 */
public class SimRPSContIntervall {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		String parameterfile = "";

		if ( args.length > 1 ) {

			System.out.println("Invalid calling syntax!");
			System.out.println("usage: <applname> [<filename>]");
			System.exit(1);

		} else if ( args.length == 1 ) {
			parameterfile = args[0];
		}

		SimParameters params = new SimParameters(parameterfile);

		Engine.getSingleton().setTimerPeriod(params.engineTimerPeriod);

		Engine.getSingleton().init();

		Szenario szenario = new SzenarioAllForOne(new RPSFactory() {
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

			public RSSFeedRepresentation newRSSFeedRepresentation(RSSFeed feed) {
				return newRSSFeedRepresentation(null, (ColorFeed) feed);
			}
		}, params);
	}

}
