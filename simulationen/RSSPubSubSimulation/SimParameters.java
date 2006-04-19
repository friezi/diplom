import java.util.*;
import java.io.*;

/**
 * To hold the relevant parameters for the simulation
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class SimParameters {

	// default parameters

	/**
	 * RSSServerNode: minimum update-interval
	 */
	int minUpIntv = 5;

	/**
	 * RSSServerNode: maximum update-interval
	 */
	int maxUpIntv = 30;

	/**
	 * RSSServerNode: time to life of the feed
	 */
	int ttl = 10;

	/**
	 * PubSubNode: spread-divisor for update-interval of the feeds from the
	 * publishers: the time-range whithin feed-requests cann occur calculates
	 * like this: ttl + networksize / spreadDivisor
	 */
	int spreadDivisor = 6;

	/**
	 * RSSFeedRequestMessage: runtime for message RSSFeedRequestMessage
	 */
	int rssFeedRequestMsgRT = 15;

	/**
	 * RSSFeedMessage: runtime for message RSSFeedMessage
	 */
	int rssFeedMsgRT = 20;

	/**
	 * RSSFeedMessage: if true, RSSFeedMessages will represent the feed
	 */
	boolean rssFeedMsgRepresent = true;

	/**
	 * SubnetParamMessage: runtime
	 */
	int subnetParamMsgRT = 40;

	/**
	 * SubnetParamMessage: if set, size of originating subnet will be shown in
	 * message
	 */
	boolean showSizeBrokerMsg = false;

	/**
	 * AdjustingBroker: Timeout for the next ping
	 */
	int pingTimer = 8000;

	/**
	 * AdjustingBroker: factor for the time to wait for next ping
	 */
	int pingTimeoutFactor = 2;

	/**
	 * AdjustingBroker: Timeout for informing other brokers about change in
	 * number of subscribers
	 */
	int informBrokersTimeout = 2000;

	/**
	 * AdjustingBroker: Timeout for informing subscribers about change of
	 * network-size
	 */
	int informSubscribersTimeout = 3000;

	/**
	 * ColorEventFeed: the maximum number of event-entries in the feed
	 */
	int maxFeedEvents = 5;

	/**
	 * PubSub: the time after which a new feed should be received by the
	 * subscriber at most
	 */
	int maxRefreshRate = 5;

	/**
	 * AdjustingEventBroker, EventPubSub: maximum number of events thatb will be
	 * stored at the node
	 */
	int maxSubscriberEvents = 10;

	/**
	 * Engine: the refresh-rate in milliseconds
	 */
	int engineTimerPeriod = 50;

	/**
	 * QueueingRSSServer: the processingtime of a FeedRequest in miliseconds
	 */
	int serverDelay = 200;

	/**
	 * QueueingRSSServer: size of queue for feed-requests
	 */
	int serverQueueSize = 20;

	private String comment = "Parameters for a RPSSimulation";

	/**
	 * @param filename
	 *            from the command-line. Should hold the filename to read
	 *            parameters from.
	 * 
	 * Opens the file args[0] and reads the parameters.
	 * @throws IOException
	 */
	SimParameters(String filename) throws IOException {

		Properties properties = new Properties();

		FileInputStream infile;
		FileOutputStream outfile;
//		String filename;

		// set default-parameters
		properties.setProperty("minUpIntv", String.valueOf(minUpIntv));
		properties.setProperty("maxUpIntv", String.valueOf(maxUpIntv));
		properties.setProperty("ttl", String.valueOf(ttl));
		properties.setProperty("spreadDivisor", String.valueOf(spreadDivisor));
		properties.setProperty("rssFeedRequestMsgRT", String.valueOf(rssFeedRequestMsgRT));
		properties.setProperty("rssFeedMsgRT", String.valueOf(rssFeedMsgRT));
		properties.setProperty("rssFeedMsgRepresent", String.valueOf(rssFeedMsgRepresent));
		properties.setProperty("subnetParamMsgRT", String.valueOf(subnetParamMsgRT));
		properties.setProperty("showSizeBrokerMsg", String.valueOf(showSizeBrokerMsg));
		properties.setProperty("pingTimer", String.valueOf(pingTimer));
		properties.setProperty("pingTimeoutFactor", String.valueOf(pingTimeoutFactor));
		properties.setProperty("informBrokersTimeout", String.valueOf(informBrokersTimeout));
		properties.setProperty("informSubscribersTimeout", String.valueOf(informSubscribersTimeout));
		properties.setProperty("maxFeedEvents", String.valueOf(maxFeedEvents));
		properties.setProperty("maxSubscriberEvents", String.valueOf(maxSubscriberEvents));
		properties.setProperty("maxRefreshRate", String.valueOf(maxRefreshRate));
		properties.setProperty("engineTimerPeriod", String.valueOf(engineTimerPeriod));
		properties.setProperty("serverDelay", String.valueOf(serverDelay));
		properties.setProperty("serverQueueSize", String.valueOf(serverQueueSize));
//
//		if ( parameterfile.length > 1 ) {
//
//			System.out.println("Invalid calling syntax!");
//			System.out.println("usage: <applname> [<filename>]");
//			System.exit(1);
//
//		} else if ( parameterfile.length < 1 ) {
//			System.out.println("No input-file given! Using default-parameters");
//		} else {

//			filename = filename[0];

		if (filename.equals("")){
			System.out.println("No input-file given! Using default-parameters");
		}else{
		
			try {

				infile = new FileInputStream(filename);
				properties.load(infile);

			} catch ( FileNotFoundException ifne ) {

				System.out.println("Warning: file " + filename
						+ " not found. Will create it with default parameters");

				try {

					outfile = new FileOutputStream(filename);
					properties.store(outfile, comment);

				} catch ( FileNotFoundException ofne ) {
					System.out.println("Warning: couldn't open file " + filename + " for writing.");
				}
			}
			
		}

//		}

		// set params
		minUpIntv = Integer.valueOf((String) properties.getProperty("minUpIntv"));
		maxUpIntv = Integer.valueOf((String) properties.getProperty("maxUpIntv"));
		ttl = Integer.valueOf((String) properties.getProperty("ttl"));
		spreadDivisor = Integer.valueOf((String) properties.getProperty("spreadDivisor"));
		rssFeedRequestMsgRT = Integer.valueOf((String) properties.getProperty("rssFeedRequestMsgRT"));
		rssFeedMsgRT = Integer.valueOf((String) properties.getProperty("rssFeedMsgRT"));
		subnetParamMsgRT = Integer.valueOf((String) properties.getProperty("subnetParamMsgRT"));
		if ( properties.getProperty("showSizeBrokerMsg").toLowerCase().equals("true") )
			showSizeBrokerMsg = true;
		else
			showSizeBrokerMsg = false;
		pingTimer = Integer.valueOf((String) properties.getProperty("pingTimer"));
		pingTimeoutFactor = Integer.valueOf((String) properties.getProperty("pingTimeoutFactor"));
		informBrokersTimeout = Integer.valueOf((String) properties.getProperty("informBrokersTimeout"));
		informSubscribersTimeout = Integer.valueOf((String) properties
				.getProperty("informSubscribersTimeout"));
		maxFeedEvents = Integer.valueOf((String) properties.getProperty("maxFeedEvents"));
		maxSubscriberEvents = Integer.valueOf((String) properties.getProperty("maxSubscriberEvents"));
		maxRefreshRate = Integer.valueOf((String) properties.getProperty("maxRefreshRate"));
		engineTimerPeriod = Integer.valueOf((String) properties.getProperty("engineTimerPeriod"));
		serverDelay = Integer.valueOf((String) properties.getProperty("serverDelay"));
		serverQueueSize = Integer.valueOf((String) properties.getProperty("serverQueueSize"));
		if ( properties.getProperty("rssFeedMsgRepresent").toLowerCase().equals("true") )
			rssFeedMsgRepresent = true;
		else
			rssFeedMsgRepresent = false;

	}

}
