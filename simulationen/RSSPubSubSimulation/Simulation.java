import rsspubsubframework.DisplayableObject;
import rsspubsubframework.Engine;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * 
 */

/**
 * This is a general Simulation. On running main you have to provide it with to filenames as arguments. The first file
 * defines all the Szenario-, Broker-, etc-Classes as a key/value-pair. The values are the classnames of the 
 * appropriate class. The second file defines all the parameters.
 * 
 * @author Friedemann Zintel
 * 
 */
public class Simulation {

	protected static final String SZENARIO_CLASSKEY = "SzenarioClass";

	protected static final String SZENARIOALLFORONE = "SzenarioAllForOne";

	protected static final String SZENARIOONESURROUNDED = "SzenarioOneSurrounded";

	protected static String szenarioclass;

	protected static final String BROKER_CLASSKEY = "BrokerClass";

	protected static final String BROKER = "Broker";

	protected static final String ADJUSTINGBROKER = "AdjustingBroker";

	protected static final String ADJUSTINGEVENTBROKER = "AdjustingEventBroker";

	protected static String brokerclass;

	protected static final String RSSSERVER_CLASSKEY = "RSSServerClass";

	protected static final String RSSSERVER = "RSSServer";

	protected static final String QUEUEINGRSSSERVER = "QueueingRSSServer";

	protected static String rssserverclass;

	protected static final String PUBSUB_CLASSKEY = "PubSubClass";

	protected static final String PUBSUB = "PubSub";

	protected static final String EVENTPUBSUB = "EventPubSub";

	protected static final String DFLTREFRESHRATEEVENTPUBSUB = "DfltRefreshRateEventPubSub";

	protected static final String CONGESTIONCONTROLEVENTPUBSUB = "CongestionControlEventPubSub";

	protected static String pubsubclass;

	protected static final String RSSFEED_CLASSKEY = "RSSFeedClass";

	protected static final String COLORFEED = "ColorFeed";

	protected static final String COLOREVENTFEED = "ColorEventFeed";

	protected static String rssfeedclass;

	protected static SimParameters params;

	private Simulation() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String parameterfile = "";
		String szenariofile = "";

		// check commandline
		if ( args.length > 2 || args.length < 1 ) {

			System.out.println("Invalid calling syntax!");
			System.out.println("usage: <applname> <szenariofile> [<parameterfile>]");
			System.exit(1);

		} else if ( args.length == 2 ) {
			parameterfile = args[1];
		}

		szenariofile = args[0];

		// set szenario-configuration
		setSzenarioConfiguration(szenariofile);

		// read parameters
		params = new SimParameters(parameterfile);

		Engine.getSingleton().setTimerPeriod(params.engineTimerPeriod);

		Engine.getSingleton().init();

		Szenario szenario = newSzenario();
		
		Engine.getSingleton().repaintGUI();

	}

	protected static Szenario newSzenario() {

		String methodname = "Simulation.newSzenario()";

		if ( szenarioclass.equals(SZENARIOALLFORONE) ) {

			return new SzenarioAllForOne(newRPSFactory(), newRSSFeedFactory(), newRSSFeedRepresentationFactory(), params);

		} else if ( szenarioclass.equals(SZENARIOONESURROUNDED) ) {

			return new SzenarioOneSurrounded(newRPSFactory(), newRSSFeedFactory(), newRSSFeedRepresentationFactory(), params);

		}

		System.err.println(methodname + ": Warning: no case defined for SzenarioClass " + szenarioclass);
		System.err.println(methodname + ": Warning: using default SzenarioClass " + SZENARIOALLFORONE);
		return new SzenarioAllForOne(newRPSFactory(), newRSSFeedFactory(), newRSSFeedRepresentationFactory(), params);

	}

	protected static RPSFactory newRPSFactory() {

		return new RPSFactory() {
			public BrokerNode newBrokerNode(int xp, int yp, SimParameters params) {
				return Simulation.newBrokerNode(xp, yp, params);
			}

			public PubSubNode newPubSubNode(int xp, int yp, SimParameters params) {
				return Simulation.newPubSubNode(xp, yp, params);
			}

			public RSSServerNode newRSSServerNode(int xp, int yp, SimParameters params) {
				return Simulation.newRSSServerNode(xp, yp, params);
			}

		};

	}

	protected static BrokerNode newBrokerNode(int xp, int yp, SimParameters params) {

		String methodname = "Simulation.newBrokerNode()";

		if ( brokerclass.equals(BROKER) ) {

			return new Broker(xp, yp, params);

		} else if ( brokerclass.equals(ADJUSTINGBROKER) ) {

			return new AdjustingBroker(xp, yp, params);

		} else if ( brokerclass.equals(ADJUSTINGEVENTBROKER) ) {

			return new AdjustingEventBroker(xp, yp, new ColorEventFeedFactory(params), params);

		}

		System.err.println(methodname + ": Warning: no case defined for BrokerClass " + brokerclass);
		System.err.println(methodname + ": Warning: using default BrokerClass " + BROKER);
		return new Broker(xp, yp, params);

	}

	protected static PubSubNode newPubSubNode(int xp, int yp, SimParameters params) {

		String methodname = "Simulation.newPubSubNode()";

		if ( pubsubclass.equals(PUBSUB) ) {

			return new PubSub(xp, yp, params);

		} else if ( pubsubclass.equals(EVENTPUBSUB) ) {

			return new EventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		} else if ( pubsubclass.equals(DFLTREFRESHRATEEVENTPUBSUB) ) {

			return new DfltRefreshRateEventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		} else if ( pubsubclass.equals(CONGESTIONCONTROLEVENTPUBSUB) ) {

			return new CongestionControlEventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		}

		System.err.println(methodname + ": Warning: no case defined for PubSubClass " + pubsubclass);
		System.err.println(methodname + ": Warning: using default PubSubClass " + PUBSUB);
		return new PubSub(xp, yp, params);

	}

	protected static RSSServerNode newRSSServerNode(int xp, int yp, SimParameters params) {

		String methodname = "Simulation.newRSSServerNode()";

		if ( rssserverclass.equals(RSSSERVER) ) {

			return new RSSServer(xp, yp, params);

		} else if ( rssserverclass.equals(QUEUEINGRSSSERVER) ) {

			return new QueueingRSSServer(xp, yp, params);

		}

		System.err.println(methodname + ": Warning: no case defined for RSSServerClass " + rssserverclass);
		System.err.println(methodname + ": Warning: using default RSSServerClass " + RSSSERVER);
		return new RSSServer(xp, yp, params);

	}

	protected static RSSFeedFactory newRSSFeedFactory() {

		String methodname = "Simulation.newRSSFeedFactoryNode()";

		if ( rssfeedclass.equals(COLORFEED) ) {

			return new ColorFeedFactory();

		} else if ( rssfeedclass.equals(COLOREVENTFEED) ) {

			return new ColorEventFeedFactory(params);
		}

		System.err.println(methodname + ": Warning: no case defined for RSSFeedClass " + rssfeedclass);
		System.err.println(methodname + ": Warning: using default RSSFeedClass " + COLORFEED);
		return new ColorFeedFactory();
	}

	protected static RSSFeedRepresentationFactory newRSSFeedRepresentationFactory() {
		return new RSSFeedRepresentationFactory() {
			public RSSFeedRepresentation newRSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed) {
				return new ColorFeedRepresentation(dObj, (ColorFeed) feed);
			}
		};
	}

	protected static void setSzenarioConfiguration(String filename) throws Exception {

		boolean error = false;

		Properties szenarioConfiguration = new Properties();

		szenarioConfiguration.load(new FileInputStream(filename));

		if ( (szenarioclass = szenarioConfiguration.getProperty(SZENARIO_CLASSKEY)) == null ) {
			System.err.println("Please define the key/value pair for key " + SZENARIO_CLASSKEY + " in file " + filename);
			error = true;
		}

		if ( (brokerclass = szenarioConfiguration.getProperty(BROKER_CLASSKEY)) == null ) {
			System.err.println("Please define the key/value pair for key " + BROKER_CLASSKEY + " in file " + filename);
			error = true;
		}

		if ( (pubsubclass = szenarioConfiguration.getProperty(PUBSUB_CLASSKEY)) == null ) {
			System.err.println("Please define the key/value pair for key " + PUBSUB_CLASSKEY + " in file " + filename);
			error = true;
		}

		if ( (rssserverclass = szenarioConfiguration.getProperty(RSSSERVER_CLASSKEY)) == null ) {
			System.err.println("Please define the key/value pair for key " + RSSSERVER_CLASSKEY + " in file " + filename);
			error = true;
		}

		if ( (rssfeedclass = szenarioConfiguration.getProperty(RSSFEED_CLASSKEY)) == null ) {
			System.err.println("Please define the key/value pair for key " + RSSFEED_CLASSKEY + " in file " + filename);
			error = true;
		}

		if ( error == true )
			System.exit(1);

	}

}
