import java.io.IOException;

import rsspubsubframework.*;

/**
 * 
 */

/**
 * Simulation of RSS with Publish/Subscribe on top of Szenario
 * "One Server surrounded" in combination with Adjusting ColorEvent-Broker
 * 
 * @author Friedemann Zintel
 * 
 */
public class SimRPSOSurrAdjColEvBrk {

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

		Szenario szenario = new SzenarioOneSurrounded(new RPSFactory() {
			public BrokerNode newBrokerNode(int xp, int yp, SimParameters params) {
				return new AdjustingEventBroker(xp, yp, new ColorEventFeedFactory(params), params);
			}

			public PubSubNode newPubSubNode(int xp, int yp, SimParameters params) {
				return new EventPubSub(xp, yp, new ColorEventFeedFactory(params), params);
			}

			public RSSServerNode newRSSServerNode(int xp, int yp, SimParameters params) {
				return new RSSServer(xp, yp, params);
			}
		}, new ColorEventFeedFactory(params), new RSSFeedRepresentationFactory() {
			public RSSFeedRepresentation newRSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed) {
				return new ColorFeedRepresentation(dObj, (ColorFeed) feed);
			}
		}, params);
	}

}
