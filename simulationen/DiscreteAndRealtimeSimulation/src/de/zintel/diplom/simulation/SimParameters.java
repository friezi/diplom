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
	protected int minUpIntv = 5;

	/**
	 * RSSServerNode: maximum update-interval
	 */
	protected int maxUpIntv = 30;

	/**
	 * RSSServerNode: time to life of the feed
	 */
	protected int ttl = 10;

	/**
	 * PubSubNode: spread-divisor for update-interval of the feeds from the
	 * publishers: the time-range whithin feed-requests cann occur calculates
	 * like this: ttl + networksize / spreadDivisor
	 */
	protected int spreadDivisor = 6;

	/**
	 * RSSFeedRequestMessage: runtime for message RSSFeedRequestMessage
	 */
	protected int rssFeedRequestMsgRT = 15;

	/**
	 * RSSFeedMessage: runtime for message RSSFeedMessage
	 */
	protected int rssFeedMsgRT = 20;

	/**
	 * RSSFeedMessage: if true, RSSFeedMessages will represent the feed
	 */
	protected boolean rssFeedMsgRepresent = true;

	/**
	 * SubnetParamMessage: runtime
	 */
	protected int subnetParamMsgRT = 40;

	/**
	 * SubnetParamMessage: if set, size of originating subnet will be shown in
	 * message
	 */
	protected boolean showSizeBrokerMsg = false;

	/**
	 * AdjustingBroker: Timeout for the next ping
	 */
	protected int pingTimer = 8000;

	/**
	 * AdjustingBroker: factor for the time to wait for next ping
	 */
	protected int pingTimeoutFactor = 2;

	/**
	 * AdjustingBroker: Timeout for informing other brokers about change in
	 * number of subscribers
	 */
	protected int informBrokersTimeout = 2000;

	/**
	 * AdjustingBroker: Timeout for informing subscribers about change of
	 * network-size
	 */
	protected int informSubscribersTimeout = 3000;

	/**
	 * ColorEventFeed: the maximum number of event-entries in the feed
	 */
	protected int maxFeedEvents = 5;

	/**
	 * PubSub: the time after which a new feed should be received by the
	 * subscriber at most
	 */
	protected int preferredPollingPeriod = 5;

	/**
	 * PubSub: the maximum polling-period which won't be exceeded.
	 */
	protected long maxPollingPeriod = Long.MAX_VALUE / 1000;

	/**
	 * AdjustingEventBroker, EventPubSub: maximum number of events thatb will be
	 * stored at the node
	 */
	protected int maxSubscriberEvents = 10;

	/**
	 * Engine: the refresh-rate in milliseconds
	 */
	protected int engineTimerPeriod = 50;

	/**
	 * QueueingRSSServer: the processingtime of a FeedRequest in miliseconds
	 */
	protected int processingTimeFeedRequest = 350;

	/**
	 * QueueingRSSServer: the processingtime for an unreplies FeedRequest in
	 * miliseconds
	 */
	protected long processingTimeUnrepliedRequest = 43;

	/**
	 * QueueingRSSServer: size of queue for feed-requests
	 */
	protected int serverQueueSize = 20;

	/**
	 * General: number of subscribers in the system. Must not exceed number of
	 * nodes minus number of brokers minus one rss-server-node; if set to 0,
	 * number of subscriber will be half of the remaining nodes (nodes - brokers -
	 * one)
	 */
	protected int subscribers = 0;

	/**
	 * general: if true, this simulation is discrete, i.e. the engine is a
	 * discrete-event-machine
	 */
	protected boolean isDiscreteSimulation = false;

	/**
	 * general: if true, a gui will be started, otherwise simulation starts and
	 * outputs to commandline
	 */
	protected boolean gui = true;

	/**
	 * SimParameters: if true, the properties will be written to the
	 * parameter-file given on command-line
	 */
	protected boolean saveProperties = true;

	/**
	 * general: recording-steps in secs for gnuplot
	 */
	protected long gnuplotTimeStepSecs = 5;

	/**
	 * RSSServerNodeStatistics: the gnuplot-file totalTemporaryRequests-datas
	 * are written to
	 */
	protected String gnuplotFileTotalTemporaryRequests = "totalTemporaryRequests.gnuplotdata";

	/**
	 * general: gnuplot-file which stores the serverqueue-size as
	 * gnuplot-command
	 */
	protected String gnuplotFileQueueSize = "queue.gnuplot";

	/**
	 * general: gnuplot-file which stores the mean-value of
	 * currentPollingPeriods
	 */
	protected String gnuplotFileMeanValueCPP = "meanValueCPP.gnuplotdata";

	/**
	 * general: gnuplot-file which stores the mean-deviation of
	 * currentPollingPeriods
	 */
	protected String gnuplotFileStdDevCPP = "stdDevCPP.gnuplotdata";

	/**
	 * general: gnuplot-file which stores the mean-deviation-coefficient of
	 * currentPollingPeriods
	 */
	protected String gnuplotFileCoeffVarCPP = "coeffVarCPP.gnuplotdata";

	/**
	 * general: gnuplot-file which stores the average-nessage-delay-ratio
	 */
	protected String gnuplotFileAvgMsgDelayRatio = "avgMsgDelayRatio.gnuplotdata";

	/**
	 * general: gnuplot-file which stores the relative ratio between all
	 * requests and omitted requests
	 */
	protected String gnuplotFileRelReOmRatio = "relReOmRatio.gnuplotdata";

	/**
	 * general: gnuplot-file which stores the average uptodate-ratio of the
	 * messages
	 */
	protected String gnuplotFileAvgUptodateRatio = "avgUptodateRatio.gnuplotdata";

	/**
	 * gerenal: gnuplot-file which stores actions as arrows and labels as
	 * gnuplot-commands
	 */
	protected String gnuplotFileMarkers = "markers.gnuplot";

	/**
	 * general: if not "none" it defines a file containing a list of actions to
	 * be performed during a discrete simulation.
	 */
	protected String actionFile = NONE;

	/**
	 * general: a file which defines the components the simulation consists of.
	 */
	protected String scenarioFile = NONE;

	/**
	 * The seed for random number generator. If set to "none" no initial
	 * seed-value will be used so each starting of the simulation will produce
	 * different random numbers.
	 */
	protected String seedValue = String.valueOf(new Random().nextLong());

	protected String comment = "Parameters for a RPSSimulation";

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

			} catch (FileNotFoundException ifne) {

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

				} catch (FileNotFoundException ofne) {
					System.out.println("Warning: couldn't open file " + filename + " for writing.");
				}

			}

		}

	}

	protected void setDefaultParameters(Properties properties) {

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
		properties.setProperty("preferredPollingPeriod", String.valueOf(preferredPollingPeriod));
		properties.setProperty("maxPollingPeriod", String.valueOf(maxPollingPeriod));
		properties.setProperty("engineTimerPeriod", String.valueOf(engineTimerPeriod));
		properties.setProperty("processingTimeFeedRequest", String.valueOf(processingTimeFeedRequest));
		properties.setProperty("processingTimeUnrepliedRequest", String.valueOf(processingTimeUnrepliedRequest));
		properties.setProperty("serverQueueSize", String.valueOf(serverQueueSize));
		properties.setProperty("subscribers", String.valueOf(subscribers));
		properties.setProperty("isDiscreteSimulation", String.valueOf(isDiscreteSimulation));
		properties.setProperty("gui", String.valueOf(gui));
		properties.setProperty("saveProperties", String.valueOf(saveProperties));
		properties.setProperty("gnuplotFileTotalTemporaryRequests", gnuplotFileTotalTemporaryRequests);
		properties.setProperty("gnuplotTimeStepSecs", String.valueOf(gnuplotTimeStepSecs));
		properties.setProperty("gnuplotFileQueueSize", gnuplotFileQueueSize);
		properties.setProperty("gnuplotFileMeanValueCPP", gnuplotFileMeanValueCPP);
		properties.setProperty("gnuplotFileStdDevCPP", gnuplotFileStdDevCPP);
		properties.setProperty("gnuplotFileCoeffVarCPP", gnuplotFileCoeffVarCPP);
		properties.setProperty("gnuplotFileAvgMsgDelayRatio", gnuplotFileAvgMsgDelayRatio);
		properties.setProperty("gnuplotFileRelReOmRatio", gnuplotFileRelReOmRatio);
		properties.setProperty("gnuplotFileAvgUptodateRatio", gnuplotFileAvgUptodateRatio);
		properties.setProperty("gnuplotFileMarkers", gnuplotFileMarkers);
		properties.setProperty("actionFile", actionFile);
		properties.setProperty("scenarioFile", scenarioFile);
		properties.setProperty("seedValue", seedValue);

	}

	protected void setParameters(Properties properties) throws ValueOutOfRangeException {

		minUpIntv = Integer.valueOf(properties.getProperty("minUpIntv"));
		maxUpIntv = Integer.valueOf(properties.getProperty("maxUpIntv"));
		ttl = Integer.valueOf(properties.getProperty("ttl"));
		spreadDivisor = Integer.valueOf(properties.getProperty("spreadDivisor"));
		rssFeedRequestMsgRT = Integer.valueOf(properties.getProperty("rssFeedRequestMsgRT"));
		rssFeedMsgRT = Integer.valueOf(properties.getProperty("rssFeedMsgRT"));
		subnetParamMsgRT = Integer.valueOf(properties.getProperty("subnetParamMsgRT"));
		pingTimer = Integer.valueOf(properties.getProperty("pingTimer"));
		pingTimeoutFactor = Integer.valueOf(properties.getProperty("pingTimeoutFactor"));
		informBrokersTimeout = Integer.valueOf(properties.getProperty("informBrokersTimeout"));
		informSubscribersTimeout = Integer.valueOf(properties.getProperty("informSubscribersTimeout"));
		maxFeedEvents = Integer.valueOf(properties.getProperty("maxFeedEvents"));
		maxSubscriberEvents = Integer.valueOf(properties.getProperty("maxSubscriberEvents"));
		preferredPollingPeriod = Integer.valueOf(properties.getProperty("preferredPollingPeriod"));
		maxPollingPeriod = Long.valueOf(properties.getProperty("maxPollingPeriod"));
		if ( maxPollingPeriod * 1000 < 0 )
			throw new ValueOutOfRangeException("maxPollingPeriod negative or too big!");
		engineTimerPeriod = Integer.valueOf(properties.getProperty("engineTimerPeriod"));
		processingTimeFeedRequest = Integer.valueOf(properties.getProperty("processingTimeFeedRequest"));
		processingTimeUnrepliedRequest = Long.valueOf(properties.getProperty("processingTimeUnrepliedRequest"));
		serverQueueSize = Integer.valueOf(properties.getProperty("serverQueueSize"));
		subscribers = Integer.valueOf(properties.getProperty("subscribers"));
		actionFile = properties.getProperty("actionFile").trim();
		scenarioFile = properties.getProperty("scenarioFile").trim();
		gnuplotFileTotalTemporaryRequests = properties.getProperty("gnuplotFileTotalTemporaryRequests").trim();
		gnuplotFileQueueSize = properties.getProperty("gnuplotFileQueueSize").trim();
		gnuplotFileMeanValueCPP = properties.getProperty("gnuplotFileMeanValueCPP").trim();
		gnuplotFileStdDevCPP = properties.getProperty("gnuplotFileStdDevCPP").trim();
		gnuplotFileCoeffVarCPP = properties.getProperty("gnuplotFileCoeffVarCPP").trim();
		gnuplotFileAvgMsgDelayRatio = properties.getProperty("gnuplotFileAvgMsgDelayRatio").trim();
		gnuplotFileRelReOmRatio = properties.getProperty("gnuplotFileRelReOmRatio");
		gnuplotFileAvgUptodateRatio = properties.getProperty("gnuplotFileAvgUptodateRatio");
		gnuplotTimeStepSecs = Long.valueOf(properties.getProperty("gnuplotTimeStepSecs"));
		gnuplotFileMarkers = properties.getProperty("gnuplotFileMarkers");
		seedValue = properties.getProperty("seedValue").trim().toLowerCase();
		showSizeBrokerMsg = Boolean.valueOf(properties.getProperty("showSizeBrokerMsg").trim());
		rssFeedMsgRepresent = Boolean.valueOf(properties.getProperty("rssFeedMsgRepresent").trim());
		isDiscreteSimulation = Boolean.valueOf(properties.getProperty("isDiscreteSimulation").trim());
		gui = Boolean.valueOf(properties.getProperty("gui").trim());
		saveProperties = Boolean.valueOf(properties.getProperty("saveProperties").trim());

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
