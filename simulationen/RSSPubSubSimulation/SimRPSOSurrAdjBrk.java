import java.io.IOException;

import rsspubsubframework.*;

/**
 */

/**
 * Simulation of RSS with Publish/Subscribe on top of Szenario
 * "One Server surrounded" in combination with Adjusting Broker
 *
 * @author Friedemann Zintel
 * 
 */
public class SimRPSOSurrAdjBrk {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		SimParameters params = new SimParameters(args);

		SzenarioOneSurrounded szenario = new SzenarioOneSurrounded(new RPSFactory() {
			public BrokerNode newBrokerNode(int xp, int yp, SimParameters params) {
				return new AdjustingBroker(xp, yp, params);
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
