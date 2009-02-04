package de.zintel.diplom.simulation;

import java.util.*;
import java.io.*;

import de.zintel.diplom.tools.SortedLinesSetOutputStream;

/**
 * To hold the relevant parameters for the simulation
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class SimParameters {

	private static final String SEED_VALUE = "seedValue";

	private static final String SCENARIO_FILE = "scenarioFile";

	private static final String ACTION_FILE = "actionFile";

	private static final String GNUPLOT_FILE_MARKERS = "gnuplotFileMarkers";

	private static final String GNUPLOT_FILE_AVG_UPTODATE_RATIO = "gnuplotFileAvgUptodateRatio";

	private static final String GNUPLOT_FILE_REL_RE_OM_RATIO = "gnuplotFileRelReOmRatio";

	private static final String GNUPLOT_FILE_AVG_MSG_DELAY_RATIO = "gnuplotFileAvgMsgDelayRatio";

	private static final String GNUPLOT_FILE_COEFF_VAR_CPP = "gnuplotFileCoeffVarCPP";

	private static final String GNUPLOT_FILE_STD_DEV_CPP = "gnuplotFileStdDevCPP";

	private static final String GNUPLOT_FILE_MEAN_VALUE_CPP = "gnuplotFileMeanValueCPP";

	private static final String GNUPLOT_FILE_QUEUE_SIZE = "gnuplotFileQueueSize";

	private static final String GNUPLOT_TIME_STEP_SECS = "gnuplotTimeStepSecs";

	private static final String GNUPLOT_FILE_TOTAL_TEMPORARY_REQUESTS = "gnuplotFileTotalTemporaryRequests";

	private static final String SAVE_PROPERTIES = "saveProperties";

	private static final String GUI = "gui";

	private static final String IS_DISCRETE_SIMULATION = "isDiscreteSimulation";

	private static final String SUBSCRIBERS = "subscribers";

	private static final String SERVER_QUEUE_SIZE = "serverQueueSize";

	private static final String PROCESSING_TIME_UNREPLIED_REQUEST = "processingTimeUnrepliedRequest";

	private static final String PROCESSING_TIME_FEED_REQUEST = "processingTimeFeedRequest";

	private static final String ENGINE_TIMER_PERIOD = "engineTimerPeriod";

	private static final String MAX_POLLING_PERIOD = "maxPollingPeriod";

	private static final String PREFERRED_POLLING_PERIOD = "preferredPollingPeriod";

	private static final String MAX_SUBSCRIBER_EVENTS = "maxSubscriberEvents";

	private static final String MAX_FEED_EVENTS = "maxFeedEvents";

	private static final String INFORM_SUBSCRIBERS_TIMEOUT = "informSubscribersTimeout";

	private static final String INFORM_BROKERS_TIMEOUT = "informBrokersTimeout";

	private static final String PING_TIMEOUT_FACTOR = "pingTimeoutFactor";

	private static final String PING_TIMER = "pingTimer";

	private static final String SHOW_SIZE_BROKER_MSG = "showSizeBrokerMsg";

	private static final String SUBNET_PARAM_MSG_RT = "subnetParamMsgRT";

	private static final String RSS_FEED_MSG_REPRESENT = "rssFeedMsgRepresent";

	private static final String RSS_FEED_MSG_RT = "rssFeedMsgRT";

	private static final String RSS_FEED_REQUEST_MSG_RT = "rssFeedRequestMsgRT";

	private static final String SPREAD_DIVISOR = "spreadDivisor";

	private static final String TTL = "ttl";

	private static final String MAX_UP_INTV = "maxUpIntv";

	private static final String MIN_UP_INTV = "minUpIntv";

	@SuppressWarnings("serial")
	public class ValueOutOfRangeException extends Exception {

		public ValueOutOfRangeException(String message) {
			super(message);
		}

	}

	public static final String NONE = "none";

	// default parameters

	/**
	 * RSSServerNode: minimum update-interval
	 */
	private int minUpIntv = 5;

	/**
	 * RSSServerNode: maximum update-interval
	 */
	private int maxUpIntv = 30;

	/**
	 * RSSServerNode: time to live of the feed
	 */
	private int ttl = 10;

	/**
	 * PubSubNode: spread-divisor for update-interval of the feeds from the
	 * publishers: the time-range whithin feed-requests can occur - calculates
	 * like this: ttl + networksize / spreadDivisor
	 */
	private int spreadDivisor = 6;

	/**
	 * RSSFeedRequestMessage: runtime for message RSSFeedRequestMessage
	 */
	private int rssFeedRequestMsgRT = 15;

	/**
	 * RSSFeedMessage: runtime for message RSSFeedMessage
	 */
	private int rssFeedMsgRT = 20;

	/**
	 * RSSFeedMessage: if true, RSSFeedMessages will represent the feed
	 */
	private boolean rssFeedMsgRepresent = true;

	/**
	 * SubnetParamMessage: runtime
	 */
	private int subnetParamMsgRT = 40;

	/**
	 * SubnetParamMessage: if set, size of originating subnet will be shown in
	 * message
	 */
	private boolean showSizeBrokerMsg = false;

	/**
	 * AdjustingBroker: Timeout for the next ping
	 */
	private int pingTimer = 8000;

	/**
	 * AdjustingBroker: factor for the time to wait for next ping
	 */
	private int pingTimeoutFactor = 2;

	/**
	 * AdjustingBroker: Timeout for informing other brokers about change in
	 * number of subscribers
	 */
	private int informBrokersTimeout = 2000;

	/**
	 * AdjustingBroker: Timeout for informing subscribers about change of
	 * network-size
	 */
	private int informSubscribersTimeout = 3000;

	/**
	 * ColorEventFeed: the maximum number of event-entries in the feed
	 */
	private int maxFeedEvents = 5;

	/**
	 * PubSub: the time after which a new feed should be received by the
	 * subscriber at most
	 */
	private int preferredPollingPeriod = 5;

	/**
	 * PubSub: the maximum polling-period which won't be exceeded.
	 */
	private long maxPollingPeriod = Long.MAX_VALUE / 1000;

	/**
	 * AdjustingEventBroker, EventPubSub: maximum number of events thatb will be
	 * stored at the node
	 */
	private int maxSubscriberEvents = 10;

	/**
	 * Engine: the refresh-rate in milliseconds
	 */
	private int engineTimerPeriod = 50;

	/**
	 * QueueingRSSServer: the processingtime of a FeedRequest in miliseconds
	 */
	private int processingTimeFeedRequest = 350;

	/**
	 * QueueingRSSServer: the processingtime for an unreplies FeedRequest in
	 * miliseconds
	 */
	private long processingTimeUnrepliedRequest = 43;

	/**
	 * QueueingRSSServer: size of queue for feed-requests
	 */
	private int serverQueueSize = 20;

	/**
	 * General: number of subscribers in the system. Must not exceed number of
	 * nodes minus number of brokers minus one rss-server-node; if set to 0,
	 * number of subscriber will be half of the remaining nodes (nodes - brokers -
	 * one)
	 */
	private int subscribers = 0;

	/**
	 * general: if true, this simulation is discrete, i.e. the engine is a
	 * discrete-event-machine
	 */
	private boolean isDiscreteSimulation = false;

	/**
	 * general: if true, a gui will be started, otherwise simulation starts and
	 * outputs to commandline
	 */
	private boolean gui = true;

	/**
	 * SimParameters: if true, the properties will be written to the
	 * parameter-file given on command-line
	 */
	private boolean saveProperties = true;

	/**
	 * general: recording-steps in secs for gnuplot
	 */
	private long gnuplotTimeStepSecs = 5;

	/**
	 * RSSServerNodeStatistics: the gnuplot-file totalTemporaryRequests-datas
	 * are written to
	 */
	private String gnuplotFileTotalTemporaryRequests = "totalTemporaryRequests.gnuplotdata";

	/**
	 * general: gnuplot-file which stores the serverqueue-size as
	 * gnuplot-command
	 */
	private String gnuplotFileQueueSize = "queue.gnuplot";

	/**
	 * general: gnuplot-file which stores the mean-value of
	 * currentPollingPeriods
	 */
	private String gnuplotFileMeanValueCPP = "meanValueCPP.gnuplotdata";

	/**
	 * general: gnuplot-file which stores the mean-deviation of
	 * currentPollingPeriods
	 */
	private String gnuplotFileStdDevCPP = "stdDevCPP.gnuplotdata";

	/**
	 * general: gnuplot-file which stores the mean-deviation-coefficient of
	 * currentPollingPeriods
	 */
	private String gnuplotFileCoeffVarCPP = "coeffVarCPP.gnuplotdata";

	/**
	 * general: gnuplot-file which stores the average-nessage-delay-ratio
	 */
	private String gnuplotFileAvgMsgDelayRatio = "avgMsgDelayRatio.gnuplotdata";

	/**
	 * general: gnuplot-file which stores the relative ratio between all
	 * requests and omitted requests
	 */
	private String gnuplotFileRelReOmRatio = "relReOmRatio.gnuplotdata";

	/**
	 * general: gnuplot-file which stores the average uptodate-ratio of the
	 * messages
	 */
	private String gnuplotFileAvgUptodateRatio = "avgUptodateRatio.gnuplotdata";

	/**
	 * gerenal: gnuplot-file which stores actions as arrows and labels as
	 * gnuplot-commands
	 */
	private String gnuplotFileMarkers = "markers.gnuplot";

	/**
	 * general: if not "none" it defines a file containing a list of actions to
	 * be performed during a discrete simulation.
	 */
	private String actionFile = NONE;

	/**
	 * general: a file which defines the components the simulation consists of.
	 */
	private String scenarioFile = NONE;

	/**
	 * The seed for random number generator. If set to "none" no initial
	 * seed-value will be used so each starting of the simulation will produce
	 * different random numbers.
	 */
	private String seedValue = String.valueOf(new Random().nextLong());

	private String comment = "Parameters for a RPSSimulation";

	/**
	 * @param filename
	 *            from the command-line. Should hold the filename to read
	 *            parameters from.
	 * 
	 * Opens the file args[0] and reads the parameters.
	 * @throws IOException
	 * @throws ValueOutOfRangeException
	 */
	SimParameters(String filename) throws IOException, ValueOutOfRangeException {

		Properties properties = new Properties();

		FileInputStream infile;
		SortedLinesSetOutputStream outfile;
		// String filename;

		// set default-parameters
		setDefaultParameters(properties);

		// try to load config-file
		if ( filename.equals("") ) {
			System.out.println("No input-file given! Using default-parameters");
		} else {

			try {

				infile = new FileInputStream(filename);
				properties.load(infile);

			} catch ( FileNotFoundException ifne ) {

				System.out.println("Warning: file " + filename + " not found. Will create it with default parameters");
			}

		}

		// set params
		setParameters(properties);

		if ( this.saveProperties == true ) {

			if ( filename.equals("") == false ) {

				try {

					outfile = new SortedLinesSetOutputStream(new FileOutputStream(filename));
					properties.store(outfile, comment);
					outfile.close();

				} catch ( FileNotFoundException ofne ) {
					System.out.println("Warning: couldn't open file " + filename + " for writing.");
				}

			}

		}

	}

	protected void setDefaultParameters(Properties properties) {

		properties.setProperty(MIN_UP_INTV, String.valueOf(minUpIntv));
		properties.setProperty(MAX_UP_INTV, String.valueOf(maxUpIntv));
		properties.setProperty(TTL, String.valueOf(ttl));
		properties.setProperty(SPREAD_DIVISOR, String.valueOf(spreadDivisor));
		properties.setProperty(RSS_FEED_REQUEST_MSG_RT, String.valueOf(rssFeedRequestMsgRT));
		properties.setProperty(RSS_FEED_MSG_RT, String.valueOf(rssFeedMsgRT));
		properties.setProperty(RSS_FEED_MSG_REPRESENT, String.valueOf(rssFeedMsgRepresent));
		properties.setProperty(SUBNET_PARAM_MSG_RT, String.valueOf(subnetParamMsgRT));
		properties.setProperty(SHOW_SIZE_BROKER_MSG, String.valueOf(showSizeBrokerMsg));
		properties.setProperty(PING_TIMER, String.valueOf(pingTimer));
		properties.setProperty(PING_TIMEOUT_FACTOR, String.valueOf(pingTimeoutFactor));
		properties.setProperty(INFORM_BROKERS_TIMEOUT, String.valueOf(informBrokersTimeout));
		properties.setProperty(INFORM_SUBSCRIBERS_TIMEOUT, String.valueOf(informSubscribersTimeout));
		properties.setProperty(MAX_FEED_EVENTS, String.valueOf(maxFeedEvents));
		properties.setProperty(MAX_SUBSCRIBER_EVENTS, String.valueOf(maxSubscriberEvents));
		properties.setProperty(PREFERRED_POLLING_PERIOD, String.valueOf(preferredPollingPeriod));
		properties.setProperty(MAX_POLLING_PERIOD, String.valueOf(maxPollingPeriod));
		properties.setProperty(ENGINE_TIMER_PERIOD, String.valueOf(engineTimerPeriod));
		properties.setProperty(PROCESSING_TIME_FEED_REQUEST, String.valueOf(processingTimeFeedRequest));
		properties.setProperty(PROCESSING_TIME_UNREPLIED_REQUEST, String.valueOf(processingTimeUnrepliedRequest));
		properties.setProperty(SERVER_QUEUE_SIZE, String.valueOf(serverQueueSize));
		properties.setProperty(SUBSCRIBERS, String.valueOf(subscribers));
		properties.setProperty(IS_DISCRETE_SIMULATION, String.valueOf(isDiscreteSimulation));
		properties.setProperty(GUI, String.valueOf(gui));
		properties.setProperty(SAVE_PROPERTIES, String.valueOf(saveProperties));
		properties.setProperty(GNUPLOT_FILE_TOTAL_TEMPORARY_REQUESTS, gnuplotFileTotalTemporaryRequests);
		properties.setProperty(GNUPLOT_TIME_STEP_SECS, String.valueOf(gnuplotTimeStepSecs));
		properties.setProperty(GNUPLOT_FILE_QUEUE_SIZE, gnuplotFileQueueSize);
		properties.setProperty(GNUPLOT_FILE_MEAN_VALUE_CPP, gnuplotFileMeanValueCPP);
		properties.setProperty(GNUPLOT_FILE_STD_DEV_CPP, gnuplotFileStdDevCPP);
		properties.setProperty(GNUPLOT_FILE_COEFF_VAR_CPP, gnuplotFileCoeffVarCPP);
		properties.setProperty(GNUPLOT_FILE_AVG_MSG_DELAY_RATIO, gnuplotFileAvgMsgDelayRatio);
		properties.setProperty(GNUPLOT_FILE_REL_RE_OM_RATIO, gnuplotFileRelReOmRatio);
		properties.setProperty(GNUPLOT_FILE_AVG_UPTODATE_RATIO, gnuplotFileAvgUptodateRatio);
		properties.setProperty(GNUPLOT_FILE_MARKERS, gnuplotFileMarkers);
		properties.setProperty(ACTION_FILE, actionFile);
		properties.setProperty(SCENARIO_FILE, scenarioFile);
		properties.setProperty(SEED_VALUE, seedValue);

	}

	protected void setParameters(Properties properties) throws ValueOutOfRangeException {

		minUpIntv = Integer.valueOf(properties.getProperty(MIN_UP_INTV));
		maxUpIntv = Integer.valueOf(properties.getProperty(MAX_UP_INTV));
		ttl = Integer.valueOf(properties.getProperty(TTL));
		spreadDivisor = Integer.valueOf(properties.getProperty(SPREAD_DIVISOR));
		rssFeedRequestMsgRT = Integer.valueOf(properties.getProperty(RSS_FEED_REQUEST_MSG_RT));
		rssFeedMsgRT = Integer.valueOf(properties.getProperty(RSS_FEED_MSG_RT));
		subnetParamMsgRT = Integer.valueOf(properties.getProperty(SUBNET_PARAM_MSG_RT));
		pingTimer = Integer.valueOf(properties.getProperty(PING_TIMER));
		pingTimeoutFactor = Integer.valueOf(properties.getProperty(PING_TIMEOUT_FACTOR));
		informBrokersTimeout = Integer.valueOf(properties.getProperty(INFORM_BROKERS_TIMEOUT));
		informSubscribersTimeout = Integer.valueOf(properties.getProperty(INFORM_SUBSCRIBERS_TIMEOUT));
		maxFeedEvents = Integer.valueOf(properties.getProperty(MAX_FEED_EVENTS));
		maxSubscriberEvents = Integer.valueOf(properties.getProperty(MAX_SUBSCRIBER_EVENTS));
		preferredPollingPeriod = Integer.valueOf(properties.getProperty(PREFERRED_POLLING_PERIOD));
		maxPollingPeriod = Long.valueOf(properties.getProperty(MAX_POLLING_PERIOD));
		if ( maxPollingPeriod * 1000 < 0 )
			throw new ValueOutOfRangeException("maxPollingPeriod negative or too big!");
		engineTimerPeriod = Integer.valueOf(properties.getProperty(ENGINE_TIMER_PERIOD));
		processingTimeFeedRequest = Integer.valueOf(properties.getProperty(PROCESSING_TIME_FEED_REQUEST));
		processingTimeUnrepliedRequest = Long.valueOf(properties.getProperty(PROCESSING_TIME_UNREPLIED_REQUEST));
		serverQueueSize = Integer.valueOf(properties.getProperty(SERVER_QUEUE_SIZE));
		subscribers = Integer.valueOf(properties.getProperty(SUBSCRIBERS));
		actionFile = properties.getProperty(ACTION_FILE).trim();
		scenarioFile = properties.getProperty(SCENARIO_FILE).trim();
		gnuplotFileTotalTemporaryRequests = properties.getProperty(GNUPLOT_FILE_TOTAL_TEMPORARY_REQUESTS).trim();
		gnuplotFileQueueSize = properties.getProperty(GNUPLOT_FILE_QUEUE_SIZE).trim();
		gnuplotFileMeanValueCPP = properties.getProperty(GNUPLOT_FILE_MEAN_VALUE_CPP).trim();
		gnuplotFileStdDevCPP = properties.getProperty(GNUPLOT_FILE_STD_DEV_CPP).trim();
		gnuplotFileCoeffVarCPP = properties.getProperty(GNUPLOT_FILE_COEFF_VAR_CPP).trim();
		gnuplotFileAvgMsgDelayRatio = properties.getProperty(GNUPLOT_FILE_AVG_MSG_DELAY_RATIO).trim();
		gnuplotFileRelReOmRatio = properties.getProperty(GNUPLOT_FILE_REL_RE_OM_RATIO);
		gnuplotFileAvgUptodateRatio = properties.getProperty(GNUPLOT_FILE_AVG_UPTODATE_RATIO);
		gnuplotTimeStepSecs = Long.valueOf(properties.getProperty(GNUPLOT_TIME_STEP_SECS));
		gnuplotFileMarkers = properties.getProperty(GNUPLOT_FILE_MARKERS);
		seedValue = properties.getProperty(SEED_VALUE).trim().toLowerCase();
		showSizeBrokerMsg = Boolean.valueOf(properties.getProperty(SHOW_SIZE_BROKER_MSG).trim());
		rssFeedMsgRepresent = Boolean.valueOf(properties.getProperty(RSS_FEED_MSG_REPRESENT).trim());
		isDiscreteSimulation = Boolean.valueOf(properties.getProperty(IS_DISCRETE_SIMULATION).trim());
		gui = Boolean.valueOf(properties.getProperty(GUI).trim());
		saveProperties = Boolean.valueOf(properties.getProperty(SAVE_PROPERTIES).trim());

	}

	/**
	 * @return Returns the comment.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return Returns the engineTimerPeriod.
	 */
	public int getEngineTimerPeriod() {
		return engineTimerPeriod;
	}

	/**
	 * @return Returns the informBrokersTimeout.
	 */
	public int getInformBrokersTimeout() {
		return informBrokersTimeout;
	}

	/**
	 * @return Returns the informSubscribersTimeout.
	 */
	public int getInformSubscribersTimeout() {
		return informSubscribersTimeout;
	}

	/**
	 * @return Returns the isDiscreteSimulation.
	 */
	public boolean isDiscreteSimulation() {
		return isDiscreteSimulation;
	}

	/**
	 * @return Returns the maxFeedEvents.
	 */
	public int getMaxFeedEvents() {
		return maxFeedEvents;
	}

	/**
	 * @return Returns the maxSubscriberEvents.
	 */
	public int getMaxSubscriberEvents() {
		return maxSubscriberEvents;
	}

	/**
	 * @return Returns the maxUpIntv.
	 */
	public int getMaxUpIntv() {
		return maxUpIntv;
	}

	/**
	 * @return Returns the minUpIntv.
	 */
	public int getMinUpIntv() {
		return minUpIntv;
	}

	/**
	 * @return Returns the pingTimeoutFactor.
	 */
	public int getPingTimeoutFactor() {
		return pingTimeoutFactor;
	}

	/**
	 * @return Returns the pingTimer.
	 */
	public int getPingTimer() {
		return pingTimer;
	}

	/**
	 * @return Returns the preferredPollingPeriod.
	 */
	public int getPreferredPollingPeriod() {
		return preferredPollingPeriod;
	}

	/**
	 * @return Returns the rssFeedMsgRepresent.
	 */
	public boolean isRssFeedMsgRepresent() {
		return rssFeedMsgRepresent;
	}

	/**
	 * @return Returns the rssFeedMsgRT.
	 */
	public int getRssFeedMsgRT() {
		return rssFeedMsgRT;
	}

	/**
	 * @return Returns the rssFeedRequestMsgRT.
	 */
	public int getRssFeedRequestMsgRT() {
		return rssFeedRequestMsgRT;
	}

	/**
	 * @return Returns the processingTimeFeedRequest.
	 */
	public int getProcessingTimeFeedRequest() {
		return processingTimeFeedRequest;
	}

	/**
	 * @return Returns the serverQueueSize.
	 */
	public int getServerQueueSize() {
		return serverQueueSize;
	}

	/**
	 * @return Returns the showSizeBrokerMsg.
	 */
	public boolean isShowSizeBrokerMsg() {
		return showSizeBrokerMsg;
	}

	/**
	 * @return Returns the spreadDivisor.
	 */
	public int getSpreadDivisor() {
		return spreadDivisor;
	}

	/**
	 * @return Returns the subnetParamMsgRT.
	 */
	public int getSubnetParamMsgRT() {
		return subnetParamMsgRT;
	}

	/**
	 * @return Returns the ttl.
	 */
	public int getTtl() {
		return ttl;
	}

	/**
	 * @return Returns the gui.
	 */
	public boolean isGui() {
		return gui;
	}

	/**
	 * @return Returns the saveProperties.
	 */
	public boolean isSaveProperties() {
		return saveProperties;
	}

	/**
	 * @return Returns the gnuplotFileTotalTemporaryRequests.
	 */
	public String getGnuplotFileTotalTemporaryRequests() {
		return gnuplotFileTotalTemporaryRequests;
	}

	/**
	 * @return Returns the gnuplotTimeStepSecs.
	 */
	public long getGnuplotTimeStepSecs() {
		return gnuplotTimeStepSecs;
	}

	/**
	 * @return Returns the gnuplotFileQueueSize.
	 */
	public String getGnuplotFileQueueSize() {
		return gnuplotFileQueueSize;
	}

	/**
	 * @return Returns the maxPollingPeriod.
	 */
	public long getMaxPollingPeriod() {
		return maxPollingPeriod;
	}

	/**
	 * @return Returns the gnuplotFileStdDevCPP.
	 */
	public String getGnuplotFileStdDevCPP() {
		return gnuplotFileStdDevCPP;
	}

	/**
	 * @return Returns the gnuplotFileCoeffVarCPP.
	 */
	public String getGnuplotFileCoeffVarCPP() {
		return gnuplotFileCoeffVarCPP;
	}

	/**
	 * @return Returns the gnuplotFileMeanValueCPP.
	 */
	public String getGnuplotFileMeanValueCPP() {
		return gnuplotFileMeanValueCPP;
	}

	/**
	 * @return Returns the actionFile.
	 */
	public String getActionFile() {
		return actionFile;
	}

	/**
	 * @return Returns the scenarioFile.
	 */
	public String getScenarioFile() {
		return scenarioFile;
	}

	/**
	 * @return Returns the seedValue.
	 */
	public String getSeedValue() {
		return seedValue;
	}

	/**
	 * @return Returns the gnuplotFileAvgMsgDelayRatio.
	 */
	public String getGnuplotFileAvgMsgDelayRatio() {
		return gnuplotFileAvgMsgDelayRatio;
	}

	/**
	 * @return Returns the gnuplotFileRelReOmRatio.
	 */
	public String getGnuplotFileRelReOmRatio() {
		return gnuplotFileRelReOmRatio;
	}

	/**
	 * @return Returns the gnuplotFileAvgUptodateRatio.
	 */
	public String getGnuplotFileAvgUptodateRatio() {
		return gnuplotFileAvgUptodateRatio;
	}

	/**
	 * @return Returns the gnuplotFileMarkers.
	 */
	public String getGnuplotFileMarkers() {
		return gnuplotFileMarkers;
	}

	/**
	 * @return Returns the processingTimeUnrepliedRequest.
	 */
	public long getProcessingTimeUnrepliedRequest() {
		return processingTimeUnrepliedRequest;
	}

	/**
	 * @return the subscribers
	 */
	public int getSubscribers() {
		return subscribers;
	}

}
