import java.io.IOException;

import rsspubsubframework.DisplayableObject;
import rsspubsubframework.Engine;

/**
 * 
 */

/**
 * @author Friedemann Zintel
 *
 */
public class SimRPSOSurrQuServAdjColEvBrk {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		SimParameters params = new SimParameters(args);

		Engine.getSingleton().setTimerPeriod(params.engineTimerPeriod);

		Engine.getSingleton().init();

		SzenarioOneSurrounded szenario = new SzenarioOneSurrounded(new RPSFactory() {
			public BrokerNode newBrokerNode(int xp, int yp, SimParameters params) {
				return new AdjustingEventBroker(xp, yp, new ColorEventFeedFactory(params), params);
			}

			public PubSubNode newPubSubNode(int xp, int yp, SimParameters params) {
				return new EventPubSub(xp, yp, new ColorEventFeedFactory(params), params);
			}

			public RSSServerNode newRSSServerNode(int xp, int yp, SimParameters params) {
				return new QueueingRSSServer(xp, yp, params);
			}
		}, new ColorEventFeedFactory(params), new RSSFeedRepresentationFactory() {
			public RSSFeedRepresentation newRSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed) {
				return new ColorFeedRepresentation(dObj, (ColorFeed) feed);
			}
		}, params);

	}

}
