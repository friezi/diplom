package de.zintel.diplom.handler;
/**
 * 
 */

import java.util.*;
import java.io.*;

import de.zintel.diplom.gnuplot.GnuplotMarkerWriter;
import de.zintel.diplom.rps.broker.BrokerType;
import de.zintel.diplom.rps.pubsub.PubSubType;
import de.zintel.diplom.rps.server.RSSServerNode;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.InternalMessage;
import de.zintel.diplom.simulation.Message;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.simulation.VirtualNode;
import de.zintel.diplom.util.ProgressBarAccessor;

/**
 * <p>
 * For handling actions defined in an actionfile. An actionfile contains of the
 * following entries:
 * </p>
 * 
 * <pre>
 *          &lt;time_1 &gt; : &lt;action_1 &gt;
 *          &lt;time_2 &gt; : &lt;action_2 &gt;
 *          ...
 * </pre>
 * 
 * <p>
 * Time is given in seconds. The following actions are supported:
 * </p>
 * 
 * <pre>
 *        	exitSimulation
 *        	startGnuplotRecording
 *        	stopGnuplotRecording
 *        	setServiceTimeFactor(float factor)
 *        	startChurn(int percent, long timerange)
 *        	stopChurn
 *        	blockSubscribers(int percent)
 *        	unblockSubscribers
 *        	subscribersLeave(int percent)
 *        	subscribersRejoin
 *         subscribersJoin(long timerange)
 * </pre>
 * 
 * @author Friedemann Zintel
 * 
 */
public class ActionHandler extends VirtualNode {

	protected class ActionElement {

		Long time;

		String action;

		public ActionElement(Long time, String action) {
			this.time = time;
			this.action = action;
		}

	}

	/**
	 * @author Friedemann Zintel
	 * 
	 */
	protected class ActionElementFloatArg extends ActionElement {

		float value;

		/**
		 * @param time
		 * @param action
		 */
		public ActionElementFloatArg(Long time, String action, float value) {

			super(time, action);
			this.value = value;

		}

	}

	protected class ActionElementIntArg extends ActionElement {

		int value;

		/**
		 * @param time
		 * @param action
		 * @param value
		 */
		public ActionElementIntArg(Long time, String action, int value) {

			super(time, action);
			this.value = value;

		}

	}

	protected class ActionElementChurn extends ActionElement {

		int percent;

		long timerange;

		/**
		 * @param time
		 * @param action
		 * @param percent
		 *            percent of churning subscribers
		 * @param timerange
		 *            timerange of churning percent
		 */
		public ActionElementChurn(Long time, String action, int percent, long timerange) {

			super(time, action);
			this.percent = percent;
			this.timerange = timerange;
		}

	}

	public class ExitSimulationMessage extends InternalMessage {

		ExitSimulationMessage(long arrivalTime) {
			super(actionHandler, actionHandler, arrivalTime);
		}

	}

	public class StartGnuplotRecordingMessage extends InternalMessage {

		StartGnuplotRecordingMessage(Node dst, long arrivalTime) {
			super(actionHandler, dst, arrivalTime);
		}

	}

	public class StopGnuplotRecordingMessage extends InternalMessage {

		StopGnuplotRecordingMessage(Node dst, long arrivalTime) {
			super(actionHandler, dst, arrivalTime);
		}

	}

	public class SetServiceTimeFactorMessage extends InternalMessage {

		public float value;

		SetServiceTimeFactorMessage(Node dst, long arrivalTime, float value) {
			super(actionHandler, dst, arrivalTime);
			this.value = value;
		}

	}

	public class StartChurnMessage extends InternalMessage {

		public int percent;

		public long timerange;

		StartChurnMessage(Node dst, long arrivalTime, int percent, long timerange) {

			super(actionHandler, dst, arrivalTime);
			this.percent = percent;
			this.timerange = timerange;

		}

	}

	public class StopChurnMessage extends InternalMessage {

		StopChurnMessage(Node dst, long arrivalTime) {
			super(actionHandler, dst, arrivalTime);
		}

	}

	public class BlockSubscribersMessage extends InternalMessage {

		public int percent;

		BlockSubscribersMessage(Node dst, long arrivalTime, int percent) {

			super(actionHandler, dst, arrivalTime);
			this.percent = percent;
		}

	}

	public class UnblockSubscribersMessage extends InternalMessage {

		UnblockSubscribersMessage(Node dst, long arrivalTime) {
			super(actionHandler, dst, arrivalTime);
		}

	}

	public class SubscribersLeaveMessage extends InternalMessage {

		public int percent;

		SubscribersLeaveMessage(Node dst, long arrivalTime, int percent) {

			super(actionHandler, dst, arrivalTime);
			this.percent = percent;
		}

	}

	public class SubscribersRejoinMessage extends InternalMessage {

		SubscribersRejoinMessage(Node dst, long arrivalTime) {
			super(actionHandler, dst, arrivalTime);
		}

	}

	public class RetreatSubscribersMessage extends InternalMessage {

		long starttime;

		long stoptime;

		RetreatSubscribersMessage(Node dst, long arrivalTime, long starttime, long stoptime) {

			super(actionHandler, dst, arrivalTime);
			this.starttime = starttime;
			this.stoptime = stoptime;

		}

	}

	public class JoinBeginMessage extends InternalMessage {

		JoinBeginMessage(Node dst, long arrivalTime) {
			super(actionHandler, dst, arrivalTime);
		}

	}

	public class JoinEndMessage extends InternalMessage {

		JoinEndMessage(Node dst, long arrivalTime) {
			super(actionHandler, dst, arrivalTime);
		}

	}

	protected static final String EXITSIMULATION = "exitSimulation";

	protected static final String STARTGNUPLOTRECORDING = "startGnuplotRecording";

	protected static final String STOPGNUPLOTRECORDING = "stopGnuplotRecording";

	protected static final String SETSERVICETIMEFACTOR = "setServiceTimeFactor";

	protected static final String STARTCHURN = "startChurn";

	protected static final String STOPCHURN = "stopChurn";

	protected static final String BLOCKSBUSCRIBERS = "blockSubscribers";

	protected static final String UNBLOCKSBUSCRIBERS = "unblockSubscribers";

	protected static final String SUBSCRIBERSLEAVE = "subscribersLeave";

	protected static final String SUBSCRIBERSREJOIN = "subscribersRejoin";

	protected static final String SUBSCRIBERSJOIN = "subscribersJoin";

	protected SimParameters params;

	protected ActionHandler actionHandler = this;

	protected ChurnHandler churnHandler = null;

	protected JoinHandler joinHandler = null;

	protected GnuplotMarkerWriter gnuplotMarkerWriter = null;

	protected String filename;

	protected LinkedList<String> supportedActions = new LinkedList<String>();

	protected LinkedList<ActionElement> actionList = new LinkedList<ActionElement>();

	protected HashMap<PubSubType, HashSet<BrokerType>> oldPubsubConnections = new HashMap<PubSubType, HashSet<BrokerType>>();

	/**
	 * 
	 */
	public ActionHandler(SimParameters params) throws Exception {

		super();

		this.params = params;

		gnuplotMarkerWriter = new GnuplotMarkerWriter(params);

		setSupportedActions();

		this.filename = params.getActionFile();

		Engine.getSingleton().getInfoOutput().write("Parsing actionfile " + filename + " ... ");

		parseActionFile();

		Engine.getSingleton().getInfoOutput().write("done\n");
		//
		// showActionList();

		sendActionMessages();

	}

	protected void parseActionFile() throws Exception {

		FileReader file = new FileReader(filename);

		StreamTokenizer scanner = new StreamTokenizer(file);
		long time, prevtime = 0;
		String action;
		scanner.commentChar('#');
		scanner.eolIsSignificant(false);
		scanner.lowerCaseMode(true);

		// a line must be like: <long_value> : <string>

		scanner.nextToken();

		while ( scanner.ttype != StreamTokenizer.TT_EOF ) {

			if ( scanner.ttype != StreamTokenizer.TT_NUMBER )
				throw new Exception("ActionHandler: Error in file " + filename + " line " + scanner.lineno() + ": missing time!");

			time = (long) scanner.nval;

			if ( time < 0 )
				throw new Exception("ActionHandler: Error in file " + filename + " line " + scanner.lineno() + ": time is negative!");

			if ( time < prevtime )
				throw new Exception("ActionHandler: Error in file " + filename + " line " + scanner.lineno() + ": time not in proper order!");

			prevtime = time;

			scanner.nextToken();

			if ( scanner.ttype != ':' )
				throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing \":\"");

			scanner.nextToken();

			if ( scanner.ttype != StreamTokenizer.TT_WORD )
				throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing action!");

			action = scanner.sval;

			if ( !isSupportedAction(action) )
				throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": action \"" + action
						+ "\" not supported!!");

			if ( action.equals(SETSERVICETIMEFACTOR.toLowerCase()) )
				actionList.add(parseSetServiceTimeFactor(scanner, time));
			else if ( action.equals(STARTCHURN.toLowerCase()) )
				actionList.add(parseStartChurn(scanner, time));
			else if ( action.equals(BLOCKSBUSCRIBERS.toLowerCase()) )
				actionList.add(parseBlockSubscribers(scanner, time));
			else if ( action.equals(SUBSCRIBERSLEAVE.toLowerCase()) )
				actionList.add(parseSubscribersLeave(scanner, time));
			else if ( action.equals(SUBSCRIBERSJOIN.toLowerCase()) )
				actionList.add(parseSubscribersJoin(scanner, time));
			else
				actionList.add(new ActionElement(time, action));

			scanner.nextToken();

		}

		file.close();

	}

	protected ActionElementFloatArg parseSetServiceTimeFactor(StreamTokenizer scanner, long time) throws Exception {

		float value;

		scanner.nextToken();

		if ( scanner.ttype != '(' )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing \"(\"");

		scanner.nextToken();

		if ( scanner.ttype != StreamTokenizer.TT_NUMBER )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing value for action "
					+ SETSERVICETIMEFACTOR);

		value = (float) scanner.nval;

		scanner.nextToken();

		if ( scanner.ttype != ')' )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing \")\"");

		return new ActionElementFloatArg(time, SETSERVICETIMEFACTOR.toLowerCase(), value);

	}

	protected ActionElementChurn parseStartChurn(StreamTokenizer scanner, long time) throws Exception {

		int percent;
		long timerange;

		scanner.nextToken();

		if ( scanner.ttype != '(' )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing \"(\"");

		scanner.nextToken();

		if ( scanner.ttype != StreamTokenizer.TT_NUMBER )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing first parameter for action "
					+ STARTCHURN);

		percent = (int) scanner.nval;

		if ( percent < 0 || percent > 100 )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": value for percent out of range for action "
					+ STARTCHURN);

		scanner.nextToken();

		if ( scanner.ttype != ',' )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing \",\"");

		scanner.nextToken();

		if ( scanner.ttype != StreamTokenizer.TT_NUMBER )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing second parameter for action "
					+ STARTCHURN);

		timerange = (long) scanner.nval;

		scanner.nextToken();

		if ( scanner.ttype != ')' )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing \")\"");

		return new ActionElementChurn(time, STARTCHURN.toLowerCase(), percent, timerange);

	}

	protected ActionElementIntArg parseBlockSubscribers(StreamTokenizer scanner, long time) throws Exception {

		int value;

		scanner.nextToken();

		if ( scanner.ttype != '(' )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing \"(\"");

		scanner.nextToken();

		if ( scanner.ttype != StreamTokenizer.TT_NUMBER )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing value for action "
					+ BLOCKSBUSCRIBERS);

		value = (int) scanner.nval;

		scanner.nextToken();

		if ( scanner.ttype != ')' )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing \")\"");

		return new ActionElementIntArg(time, BLOCKSBUSCRIBERS.toLowerCase(), value);

	}

	protected ActionElementIntArg parseSubscribersLeave(StreamTokenizer scanner, long time) throws Exception {

		int value;

		scanner.nextToken();

		if ( scanner.ttype != '(' )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing \"(\"");

		scanner.nextToken();

		if ( scanner.ttype != StreamTokenizer.TT_NUMBER )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing value for action "
					+ SUBSCRIBERSLEAVE);

		value = (int) scanner.nval;

		scanner.nextToken();

		if ( scanner.ttype != ')' )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing \")\"");

		return new ActionElementIntArg(time, SUBSCRIBERSLEAVE.toLowerCase(), value);

	}

	protected ActionElementIntArg parseSubscribersJoin(StreamTokenizer scanner, long time) throws Exception {

		int value;

		scanner.nextToken();

		if ( scanner.ttype != '(' )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing \"(\"");

		scanner.nextToken();

		if ( scanner.ttype != StreamTokenizer.TT_NUMBER )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing value for action "
					+ SUBSCRIBERSJOIN);

		value = (int) scanner.nval;

		scanner.nextToken();

		if ( scanner.ttype != ')' )
			throw new Exception("ActionHandler: Error in file " + filename + " line: " + scanner.lineno() + ": missing \")\"");

		return new ActionElementIntArg(time, SUBSCRIBERSJOIN.toLowerCase(), value);

	}

	@Override
	protected void receiveMessage(Message m) {

		if ( m instanceof ExitSimulationMessage ) {

			exitSimulation();

		} else if ( m instanceof StartGnuplotRecordingMessage ) {

			Engine.getSingleton().getRpsStatistics().startGnuplotRecording();

		} else if ( m instanceof StopGnuplotRecordingMessage ) {

			Engine.getSingleton().getRpsStatistics().stopGnuplotRecording();

		} else if ( m instanceof BlockSubscribersMessage ) {

			blockSubscribers(((BlockSubscribersMessage) m).percent);

		} else if ( m instanceof UnblockSubscribersMessage ) {

			unblockSubscribers();

		} else if ( m instanceof SetServiceTimeFactorMessage ) {

			setServiceTimeFactor(((SetServiceTimeFactorMessage) m).value);

		} else if ( m instanceof SubscribersLeaveMessage ) {

			subscribersLeave(((SubscribersLeaveMessage) m).percent);

		} else if ( m instanceof SubscribersRejoinMessage ) {

			subscribersRejoin();

		}

	}

	protected void sendActionMessages() throws Exception {

		long time;

		for ( ActionElement actionElement : actionList ) {

			time = actionElement.time * 1000;

			if ( actionElement.action.equals(EXITSIMULATION.toLowerCase()) ) {

				new ExitSimulationMessage(time).send();
				Engine.getSingleton().setTimeLimit(time);

			} else if ( actionElement.action.equals(STARTGNUPLOTRECORDING.toLowerCase()) ) {

				new StartGnuplotRecordingMessage(actionHandler, time).send();
				// the following produces a deadlock sometimes
				// Engine.getSingleton().setManualRecording(false);
				// Engine.getSingleton().getInfoOutput().write("Manual recording
				// disabled!\n");
				new StartGnuplotRecordingMessage(gnuplotMarkerWriter, time).send();

			} else if ( actionElement.action.equals(STOPGNUPLOTRECORDING.toLowerCase()) ) {

				new StopGnuplotRecordingMessage(actionHandler, time).send();
				new StopGnuplotRecordingMessage(gnuplotMarkerWriter, time).send();

			} else if ( actionElement.action.equals(SETSERVICETIMEFACTOR.toLowerCase()) ) {

				new SetServiceTimeFactorMessage(actionHandler, time, ((ActionElementFloatArg) actionElement).value).send();
				new SetServiceTimeFactorMessage(gnuplotMarkerWriter, time, ((ActionElementFloatArg) actionElement).value).send();

			} else if ( actionElement.action.equals(STARTCHURN.toLowerCase()) ) {

				churnHandler = new ChurnHandler();
				new StartChurnMessage(churnHandler, time, ((ActionElementChurn) actionElement).percent, ((ActionElementChurn) actionElement).timerange)
						.send();
				new StartChurnMessage(gnuplotMarkerWriter, time, ((ActionElementChurn) actionElement).percent,
						((ActionElementChurn) actionElement).timerange).send();

			} else if ( actionElement.action.equals(STOPCHURN.toLowerCase()) ) {

				if ( churnHandler == null )
					throw new Exception("ActionHandler: " + STOPCHURN + " without " + STARTCHURN + " in actionList!");

				new StopChurnMessage(churnHandler, time).send();
				new StopChurnMessage(gnuplotMarkerWriter, time).send();

			} else if ( actionElement.action.equals(BLOCKSBUSCRIBERS.toLowerCase()) ) {

				new BlockSubscribersMessage(actionHandler, time, ((ActionElementIntArg) actionElement).value).send();
				new BlockSubscribersMessage(gnuplotMarkerWriter, time, ((ActionElementIntArg) actionElement).value).send();

			} else if ( actionElement.action.equals(UNBLOCKSBUSCRIBERS.toLowerCase()) ) {

				new UnblockSubscribersMessage(actionHandler, time).send();
				new UnblockSubscribersMessage(gnuplotMarkerWriter, time).send();

			} else if ( actionElement.action.equals(SUBSCRIBERSLEAVE.toLowerCase()) ) {

				new SubscribersLeaveMessage(actionHandler, time, ((ActionElementIntArg) actionElement).value).send();
				new SubscribersLeaveMessage(gnuplotMarkerWriter, time, ((ActionElementIntArg) actionElement).value).send();

			} else if ( actionElement.action.equals(SUBSCRIBERSREJOIN.toLowerCase()) ) {

				new SubscribersRejoinMessage(actionHandler, time).send();
				new SubscribersRejoinMessage(gnuplotMarkerWriter, time).send();

			} else if ( actionElement.action.equals(SUBSCRIBERSJOIN.toLowerCase()) ) {

				joinHandler = new JoinHandler();

				new RetreatSubscribersMessage(joinHandler, 0, time, time + ((long) ((ActionElementIntArg) actionElement).value) * 1000).send();
				new JoinBeginMessage(gnuplotMarkerWriter, time).send();
				new JoinEndMessage(gnuplotMarkerWriter, time + ((long) ((ActionElementIntArg) actionElement).value) * 1000).send();

			} else
				throw new Exception("ActionHandler: internal Error: unsupported action \"" + actionElement.action + "\" in actionList!");

		}

	}

	protected void exitSimulation() {

		Engine.getSingleton().setExit(true);
		ProgressBarAccessor progresBarAccessor = new ProgressBarAccessor();
		progresBarAccessor.setValue(0);

	}

	protected void setServiceTimeFactor(float value) {

		// search the RSSServer and set its value
		for ( Node node : Engine.getSingleton().nodeList ) {

			if ( node instanceof RSSServerNode )
				((RSSServerNode) node).setServiceTimeFactor(value);

		}

	}

	protected void blockSubscribers(int percent) {

		LinkedList<PubSubType> pubsubs = new LinkedList<PubSubType>(Engine.getPubsubindizes().keySet());
		long numberOfBlockings = (percent * pubsubs.size()) / 100;

		for ( long i = 0; i < numberOfBlockings; i++ ) {

			int index = (int) (Engine.getSingleton().getRandom().nextFloat() * pubsubs.size());
			pubsubs.remove(index).callbackBlock(0);

		}

	}

	protected void unblockSubscribers() {

		LinkedList<PubSubType> pubsubs = new LinkedList<PubSubType>(Engine.getPubsubindizes().keySet());

		for ( PubSubType pubsub : pubsubs )
			pubsub.callbackUnblock(0);

	}

	protected void subscribersLeave(int percent) {

		LinkedList<PubSubType> pubsubs = new LinkedList<PubSubType>(Engine.getPubsubindizes().keySet());
		long numberOfBlockings = (percent * pubsubs.size()) / 100;
		PubSubType pubsub;
		HashSet<BrokerType> brokers;

		for ( long i = 0; i < numberOfBlockings; i++ ) {

			int index = (int) (Engine.getSingleton().getRandom().nextFloat() * pubsubs.size());
			pubsub = pubsubs.remove(index);
			brokers = pubsub.getBrokers();

			for ( BrokerType broker : brokers )
				pubsub.callbackUnregisterFromBroker(broker);

			oldPubsubConnections.put(pubsub, brokers);

			pubsub.reset();
			pubsub.callbackBlock(1);

		}

	}

	protected void subscribersRejoin() {

		Set<PubSubType> pubsubs = oldPubsubConnections.keySet();
		HashSet<BrokerType> brokers;

		for ( PubSubType pubsub : pubsubs ) {

			if ( pubsub.callbackIsBlocked() == true ) {

				pubsub.callbackUnblock(0);

				brokers = oldPubsubConnections.get(pubsub);
				if ( brokers != null )
					for ( BrokerType broker : brokers )
						pubsub.callbackRegisterAtBroker(broker);

				pubsub.init();
				pubsub.start();

			}

		}

		oldPubsubConnections.clear();

	}

	protected void setSupportedActions() {

		supportedActions.add(EXITSIMULATION);
		supportedActions.add(STARTGNUPLOTRECORDING);
		supportedActions.add(STOPGNUPLOTRECORDING);
		supportedActions.add(SETSERVICETIMEFACTOR);
		supportedActions.add(STARTCHURN);
		supportedActions.add(STOPCHURN);
		supportedActions.add(BLOCKSBUSCRIBERS);
		supportedActions.add(UNBLOCKSBUSCRIBERS);
		supportedActions.add(SUBSCRIBERSLEAVE);
		supportedActions.add(SUBSCRIBERSREJOIN);
		supportedActions.add(SUBSCRIBERSJOIN);

	}

	boolean isSupportedAction(String name) {

		for ( String action : supportedActions )
			if ( action.toLowerCase().equals(name.toLowerCase()) )
				return true;

		return false;
	}

	protected void showActionList() {

		for ( ActionElement actionElement : actionList )
			System.out.println(actionElement.time + " : " + actionElement.action);

	}

}
