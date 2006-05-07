import java.io.IOException;

import rsspubsubframework.DisplayableObject;
import rsspubsubframework.Engine;

/**
 * 
 */

/**
 * @author friezi
 * 
 */
public class SimRPSOSurrQuServAdjColEvBrkCongContPubSub extends Szenario {
	/**
	 * @param args
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

		Szenario szenario = new SzenarioOneSurrounded(new RPSFactory() {
			public BrokerNode newBrokerNode(int xp, int yp, SimParameters params) {
				return new AdjustingEventBroker(xp, yp, new ColorEventFeedFactory(params), params);
			}

			public PubSubNode newPubSubNode(int xp, int yp, SimParameters params) {
				return new CongestionControlEventPubSub(xp, yp, new ColorEventFeedFactory(params), params);
			}

			public RSSServerNode newRSSServerNode(int xp, int yp, SimParameters params) {
				return new QueueingRSSServer(xp, yp, params);
			}
		}, new ColorEventFeedFactory(params), new RSSFeedRepresentationFactory() {
			public RSSFeedRepresentation newRSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed) {
				return new ColorFeedRepresentation(dObj, (ColorFeed) feed);
			}

			public RSSFeedRepresentation newRSSFeedRepresentation(RSSFeed feed) {
				return newRSSFeedRepresentation(null, (ColorFeed) feed);
			}
		}, params);

	}

}
