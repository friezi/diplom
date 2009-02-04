package de.zintel.diplom.simulation;
/*
 * msgnet - a simulation framework for message passing networks.
 * Copyright (C) 2005  Robert Schiele <rschiele@uni-mannheim.de>
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

import java.awt.Point;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Set;
import java.util.TimerTask;
import java.util.TreeSet;

import javax.swing.JFrame;

import de.zintel.diplom.graphics.GraphicalObject;
import de.zintel.diplom.graphics.OverlayEdge;
import de.zintel.diplom.gui.Gui;
import de.zintel.diplom.gui.InfoOutput;
import de.zintel.diplom.handler.ActionHandler;
import de.zintel.diplom.messages.SyncBeatMessage;
import de.zintel.diplom.messages.TMDispatcherMessage;
import de.zintel.diplom.rps.broker.BrokerType;
import de.zintel.diplom.rps.pubsub.PubSubType;
import de.zintel.diplom.statistics.RPSStatistics;
import de.zintel.diplom.tools.AbstractProgressBar;
import de.zintel.diplom.util.ConsoleProgressBar;
import de.zintel.diplom.util.ProgressBarAccessor;

/**
 * The Engine.
 * 
 * This is the main engine for the simulation system. Every simulation needs
 * such an object.
 */
final public class Engine {

	private static class TimeNotifier extends Observable {

		public void notifyObservers(Long value) {
			setChanged();
			super.notifyObservers(value);
		}

	}

	private class ManualRecordingNotifier extends Observable {

		public void notifyObservers(Boolean value) {
			setChanged();
			super.notifyObservers(value);
		}

	}

	private TimeNotifier timeNotifier = new TimeNotifier();

	private ManualRecordingNotifier manualRecordingNotifier = new ManualRecordingNotifier();

	private InfoOutput infoOutput = new InfoOutput();

	private AbstractProgressBar progressBar = null;

	private ProgressBarAccessor progressBarAccessor = null;

	/**
	 * Singleton object of the engine.
	 * 
	 * This is the main simulation engine object.
	 */
	private static Engine singleton = new Engine();

	/**
	 * The gui object of the engine.
	 * 
	 * This object handles automatic drawing of the user interface.
	 */
	private Gui gui = null;

	private static final String windowtitle = "RSS-feed distribution";

	private SimParameters params;

	private RPSStatistics rpsStatistics = null;

	private HashSet<JFrame> windows = new HashSet<JFrame>();

	private ActionHandler actionHandler = null;

	private TransferMessageDispatcher transferMessageDispatcher = null;

	private boolean interactiveMode = false;

	private boolean manualRecording = true;

	private boolean continuousTime = false;

	/**
	 * Factor for continuouus time
	 * 
	 * 1 tick = ctFactor * 1 ms
	 */
	private float ctFactor = 1F;

	/**
	 * If not 0 it is the time-limit after which the simulation will exit
	 */
	private long timeLimit = 0;

	/**
	 * The random number generator. A seed can be set by Simulation-object.
	 */
	private Random random = new Random();

	/**
	 * List of all nodes in the engine.
	 * 
	 * This list holds all nodes that are registered within the engine.
	 */
	public final java.util.Set<Node> nodeList = new java.util.HashSet<Node>();

	final HashMap<Integer, Node> nodeIdMap = new HashMap<Integer, Node>();

	private static int pubsubcount = 0;

	private static int brokercount = 0;

	/**
	 * This stores all PubSubs with their indizes
	 */
	private static HashMap<PubSubType, Integer> pubsubindizes = new HashMap<PubSubType, Integer>();

	private static Set<BrokerType> brokers = new HashSet<BrokerType>();

	/**
	 * List of all nodes which should be initialized by the engine when
	 * Simulation started.
	 */
	private final java.util.LinkedList<Node> startList = new LinkedList<Node>();

	/**
	 * List of all edges in the engine.
	 * 
	 * This list holds all edges for the sublayer that are registered within the
	 * engine.
	 */
	private final java.util.Set<Edge> edges = new java.util.HashSet<Edge>();

	/**
	 * This list holds all edges for the overlay-network.
	 */
	private final Set<OverlayEdge> overlayEdges = new HashSet<OverlayEdge>();

	private final Set<RSSServerConnectionEdge> rssServerConnectionEdges = new HashSet<RSSServerConnectionEdge>();

	private boolean showMessages = false;

	private boolean showTopLayer = true;

	private boolean showSubLayer = true;

	private boolean showRSSConnection = false;

	private boolean visualization = true;

	private boolean messagesOnTopLayer = false;

	/**
	 * List of all messages in the engine.
	 * 
	 * This list holds all messages that are registered within the engine. New
	 * nodes cannot be added to this list directly because that list must not be
	 * manipulated asynchronously to prevent race conditions. New nodes are
	 * migrated automatically then the new messges list.
	 */
	private final java.util.Set<Message> messageList = new java.util.HashSet<Message>();

	/**
	 * List of new messages in the engine.
	 * 
	 * This list holds all new messages that are registered within the engine.
	 * New nodes cannot be added to the standard message list directly because
	 * that list must not be manipulated asynchronously to prevent race
	 * conditions. They are migrated to the standard message list automatically.
	 */
	private final java.util.Set<Message> newMessages = new java.util.HashSet<Message>();

	/**
	 * Priority-Queue of new messages: used in case of discrete event-machine
	 * 
	 * This queue holds all created messages. Ordering is arranged due to
	 * predefined arrival-timne of the message.
	 */
	public final TreeSet<Message> messagequeue = new TreeSet<Message>(new MessageComparator());

	/**
	 * A routing-table, doesn't need to be set.
	 */
	private static Node[][] routingtable = null;

	/**
	 * cost between nodes
	 */
	private static double[][] costtable = null;

	/**
	 * distance between nodes in hops
	 */
	private static int[][] hoptable = null;

	private static int numberOfNodes = 0;

	/**
	 * added by Friedemann Zintel
	 * 
	 * List of temporary graphical objects to be displayed on screen
	 * 
	 */
	private final java.util.Set<GraphicalObject> graphicalObjects = new java.util.HashSet<GraphicalObject>();

	/**
	 * Timer object to drive the simulation.
	 * 
	 * This objects drives the simulation forward step by step.
	 */
	private java.util.Timer t = new java.util.Timer();

	/**
	 * Engine is active.
	 * 
	 * This value marks whether the engine does contain active messages.
	 */
	private boolean active = false;

	private boolean started = false;

	private boolean stopped = false;

	private boolean continued = false;

	private boolean prompting = false;

	private boolean exit = false;

	private int timerDelay = 1000;

	private int timerPeriod = 20;

	private int idleTimerPeriod = 500;

	// discrete simulation: current time
	private Long discreteTime = new Long(0);

	/**
	 * start-time for realtime-simulation
	 */
	private Date inittime = new Date();

	/**
	 * Number of simulation steps.
	 * 
	 * This is the number of simulation steps processed so far.
	 */
	private int simSteps = 0;

	/**
	 * Number of messages sent during simulation.
	 * 
	 * This is the number of messages that were sent during simulation so far.
	 */
	private int cumMessages = 0;

	/**
	 * Maximum number of messages.
	 * 
	 * This is the maximum number of messages that existed in the system at the
	 * same point in time.
	 */
	private int maxMessages = 0;

	/**
	 * @return Returns the rpsStatistics.
	 */
	public RPSStatistics getRpsStatistics() {
		return rpsStatistics;
	}

	/**
	 * Get the main simulation engine object.
	 * 
	 * Returns the main simulation engine object.
	 * 
	 * @return Main simulation object.
	 */
	public static Engine getSingleton() {
		return singleton;
	}

	/**
	 * -- modified by Friedemann Zintel --
	 * 
	 */
	private Engine() {
	}

	/**
	 * Initialize the engine.
	 * 
	 * Creates the gui and starts the simulation. -- modified by Friedemann
	 * Zintel --
	 * 
	 * @throws Exception
	 */
	public void init(SimParameters params) throws Exception {

		this.params = params;

		if ( params.isDiscreteSimulation() == false ) {

			setShowMessages(true);

			if ( params.isGui() == true )
				gui = new Gui(windowtitle + ": Realtime-Simulation", params);
			else {
				infoOutput.write("Realtime-Simulation\n");
				setStarted(true);
			}

		} else {
			// discrete Simulation

			if ( params.isGui() == true )
				gui = new Gui(windowtitle + ": Discrete simulation", params);
			else {
				infoOutput.write("Discrete Simulation\n");
				setStarted(true);
			}

			// only in discrete mode interprete the actionlist
			if ( params.getActionFile().equals(SimParameters.NONE) == false )
				actionHandler = new ActionHandler(params);

			// only in discrete mode dispatch the transfer-messages
			if ( params.isGui() == true )
				transferMessageDispatcher = new TransferMessageDispatcher();

		}

		if ( params.isGui() == false )
			progressBar = new ConsoleProgressBar();

		progressBarAccessor = new ProgressBarAccessor();

		rpsStatistics = new RPSStatistics(params);

	}

	public void startInteractiveMode() {

		setInteractiveMode(true);

		if ( params.isDiscreteSimulation() == false ) {

			t.schedule(new RealtimeEngineTask(), getTimerDelay());

		} else {

			t.schedule(new DiscreteEngineTask(), getTimerDelay());

		}

	}

	/**
	 * Adds a new node to the simulation engine.
	 * 
	 * This is an internal method. You cannot call this method directly --
	 * modified by Friedemann Zintel --
	 * 
	 * @param localNode
	 *            The node to add.
	 */
	final void addNode(Node localNode) {
		synchronized ( nodeList ) {
			synchronized ( nodeIdMap ) {
				nodeList.add(localNode);
				nodeIdMap.put(localNode.getId(), localNode);
			}
		}
	}

	final void removeNode(Node localNode) {
		synchronized ( nodeList ) {
			synchronized ( nodeIdMap ) {
				nodeList.remove(localNode);
				nodeIdMap.remove(localNode.getId());
			}
		}
	}

	final Node getNode(int id) {

		synchronized ( nodeIdMap ) {
			return nodeIdMap.get(id);
		}

	}

	/**
	 * Adds a new edge to the simulation engine.
	 * 
	 * @param localEdge
	 *            The edge to add.
	 */
	final void addEdge(Edge localEdge) {
		synchronized ( edges ) {
			edges.add(localEdge);
		}
	}

	/**
	 * Creates a new edge and adds it to the simulation engine.
	 * 
	 * This is an internal method. You cannot call this method directly
	 * 
	 * @param node1
	 *            first node
	 * @param node2
	 *            second node
	 */
	final void addEdge(Node node1, Node node2) {
		synchronized ( edges ) {
			edges.add(new Edge(node1, node2));
		}
	}

	/**
	 * Adds a new overlayedge to the simulation engine.
	 * 
	 * @param overlayEdge
	 *            The edge to be added.
	 */
	public final void addOverlayEdge(OverlayEdge overlayEdge) {
		synchronized ( overlayEdges ) {
			overlayEdges.add(overlayEdge);
		}
	}

	/**
	 * @param rssServerConnectionEdge
	 */
	public final void addRSSServerConnectionEdge(RSSServerConnectionEdge rssServerConnectionEdge) {
		synchronized ( rssServerConnectionEdges ) {
			rssServerConnectionEdges.add(rssServerConnectionEdge);
		}
	}

	/**
	 * removes an edge from the list and disconnects the peers.
	 * 
	 * @param edge
	 *            the edge
	 */
	final void removeEdge(Edge edge) {
		synchronized ( edges ) {
			edges.remove(edge);
			edge.removeConnection();
		}
	}

	/**
	 * removes an edge from the overlay-edgelist and disconnects the peers.
	 * 
	 * @param edge
	 *            the edge
	 */
	final void removeOverlayEdge(OverlayEdge edge) {
		synchronized ( overlayEdges ) {
			overlayEdges.remove(edge);
		}
	}

	/**
	 * @param rssServerConnectionEdge
	 */
	final void removeRSSServerConnectionEdge(RSSServerConnectionEdge rssServerConnectionEdge) {
		synchronized ( rssServerConnectionEdges ) {
			rssServerConnectionEdges.remove(rssServerConnectionEdge);
		}
	}

	// modified by Friedemann Zintel
	public final void removeEdgeFromNodes(Node node1, Node node2) {

		Edge firstEdge = null;
		Edge secondEdge = null;

		synchronized ( edges ) {

			for ( Edge edge : edges ) {

				if ( firstEdge != null && secondEdge != null )
					break;

				if ( (edge.node1() == node1 && edge.node2() == node2) ) {

					firstEdge = edge;

				} else if ( (edge.node1() == node2 && edge.node2() == node1) ) {

					secondEdge = edge;

				}
			}

			if ( firstEdge != null )
				removeEdge(firstEdge);

			if ( secondEdge != null )
				removeEdge(secondEdge);

		}

	}

	public final void removeOverlayEdgeFromNodes(Node node1, Node node2) {

		OverlayEdge firstEdge = null;
		OverlayEdge secondEdge = null;

		synchronized ( overlayEdges ) {

			for ( OverlayEdge edge : overlayEdges ) {

				if ( firstEdge != null && secondEdge != null )
					break;

				if ( (edge.node1() == node1 && edge.node2() == node2) ) {

					firstEdge = edge;

				} else if ( (edge.node1() == node2 && edge.node2() == node1) ) {

					secondEdge = edge;

				}
			}

			if ( firstEdge != null )
				removeOverlayEdge(firstEdge);

			if ( secondEdge != null )
				removeOverlayEdge(secondEdge);

		}

	}

	public final void removeRSSServerConnectionEdgeFromNodes(Node node1, Node node2) {

		RSSServerConnectionEdge firstEdge = null;
		RSSServerConnectionEdge secondEdge = null;

		synchronized ( rssServerConnectionEdges ) {

			for ( RSSServerConnectionEdge edge : rssServerConnectionEdges ) {

				if ( firstEdge != null && secondEdge != null )
					break;

				if ( (edge.node1() == node1 && edge.node2() == node2) ) {

					firstEdge = edge;

				} else if ( (edge.node1() == node2 && edge.node2() == node1) ) {

					secondEdge = edge;

				}
			}

			if ( firstEdge != null )
				removeRSSServerConnectionEdge(firstEdge);

			if ( secondEdge != null )
				removeRSSServerConnectionEdge(secondEdge);

		}

	}

	/**
	 * Adds the node to the startList. The Engine calls start() for all nodes in
	 * startList when simulation is started.
	 * 
	 * @param node
	 *            the node to be added
	 */
	public final synchronized void addToInitList(Node node) {
		startList.add(node);
	}

	/**
	 * Adds a new message to the simulation engine.
	 * 
	 * This is an internal method. You should not call this method directly.
	 * 
	 * @param message
	 *            The message to add.
	 */
	final void addMessage(Message message) {

		if ( params.isDiscreteSimulation() == false ) {
			synchronized ( newMessages ) {
				newMessages.add(message);
			}
		} else {
			synchronized ( messagequeue ) {
				messagequeue.add(message);
			}
		}
	}

	/**
	 * Removes the message if it has not yet been processed.
	 * 
	 * @param message
	 *            the message to be removed
	 * @return true, if the set contained the specified element
	 */
	final boolean removeMessage(Message message) {

		if ( params.isDiscreteSimulation() == false ) {
			synchronized ( newMessages ) {
				return newMessages.remove(message);
			}
		} else {
			synchronized ( messagequeue ) {
				return messagequeue.remove(message);
			}
		}

	}

	final boolean removeTMDispatcherMessage(Message contentmessage) {

		synchronized ( messagequeue ) {

			for ( Message message : messagequeue )
				if ( message instanceof TMDispatcherMessage )
					if ( ((TMDispatcherMessage) message).message == contentmessage )
						return messagequeue.remove(message);

		}

		return false;

	}

	public final void addGraphicalObject(GraphicalObject gob) {
		synchronized ( graphicalObjects ) {
			graphicalObjects.add(gob);
		}
	}

	public final void removeGraphicalObject(GraphicalObject gob) {
		synchronized ( graphicalObjects ) {
			graphicalObjects.remove(gob);
		}
	}

	/**
	 * Get a random permutation.
	 * 
	 * This method returns a random permutation of numbers from 0 to one less
	 * than s in an array.
	 * 
	 * @param s
	 *            Number of integers to permute.
	 * @return Array holding the permuted integers.
	 */
	private static int[] getRandomPermutation(int s) {

		int a[] = new int[s];
		for ( int i = 0; i < s; ++i )
			a[i] = i;
		for ( int i = 0; i < s; ++i ) {
			int p = (int) (Math.random() * s);
			int v = a[i];
			a[i] = a[p];
			a[p] = v;
		}
		return a;
	}

	/**
	 * generates the routingtable. Precondition is that nextnodetable must be
	 * initialized.
	 */
	public synchronized void generateRoutingTable(int[][] nextnodetable) {

		if ( nextnodetable == null )
			return;

		ProgressBarAccessor progressbar = new ProgressBarAccessor();

		progressbar.setMaximum(getNumberOfNodes());
		progressbar.setMinimum(0);
		progressbar.setValue(0);
		progressbar.setStringPainted(true);

		Node node;
		int nodeid;
		Node[] nodes = new Node[getNumberOfNodes()];

		setRoutingtable(new Node[getNumberOfNodes()][getNumberOfNodes()]);

		for ( int x = 0; x < getNumberOfNodes(); x++ ) {

			progressbar.setValue(x);

			for ( int y = 0; y < getNumberOfNodes(); y++ ) {

				nodeid = nextnodetable[x][y];

				if ( nodeid == -1 )
					node = null;
				else if ( (node = nodes[nodeid]) == null ) {

					node = findNode(nextnodetable[x][y]);
					nodes[nodeid] = node;

				}

				routingtable[x][y] = node;
			}
		}

		progressbar.setValue(0);
		progressbar.setStringPainted(false);

	}

	public synchronized void generateHopTable(int[][] nextnodetable) {

		if ( nextnodetable == null )
			return;

		ProgressBarAccessor progressbar = new ProgressBarAccessor();

		progressbar.setMaximum(getNumberOfNodes() * 2);
		progressbar.setMinimum(0);
		progressbar.setValue(0);
		progressbar.setStringPainted(true);

		setHoptable(new int[getNumberOfNodes()][getNumberOfNodes()]);

		// clear hoptable
		for ( int x = 0; x < getNumberOfNodes(); x++ ) {

			progressbar.setValue(x);

			for ( int y = 0; y < getNumberOfNodes(); y++ )
				getHoptable()[x][y] = -1;
		}

		// calculate hops
		for ( int x = 0; x < getNumberOfNodes(); x++ ) {

			progressbar.setValue(x + getNumberOfNodes());

			for ( int y = 0; y < getNumberOfNodes(); y++ )
				calculateHops(x, y, nextnodetable);

		}

		progressbar.setValue(0);
		progressbar.setStringPainted(false);

	}

	private int calculateHops(int x, int y, int[][] nextnodetable) {

		if ( nextnodetable[x][y] == -1 )
			getHoptable()[x][y] = 0;

		else if ( nextnodetable[x][y] == y )
			getHoptable()[x][y] = 1;

		else {

			int remainingHops = getHoptable()[nextnodetable[x][y]][y];

			if ( remainingHops == -1 )
				remainingHops = calculateHops(nextnodetable[x][y], y, nextnodetable);

			getHoptable()[x][y] = 1 + remainingHops;
		}

		return getHoptable()[x][y];

	}

	/**
	 * Draw all simulation objects.
	 * 
	 * This method draws all simulation objects registered within the engine.
	 * 
	 * @param g
	 *            The Graphics object to draw on.
	 */
	public final void draw(java.awt.Graphics g) {

		if ( isVisualization() == true ) {

			try {

				if ( isShowSubLayer() == true ) {
					synchronized ( edges ) {
						for ( Edge ce : edges )
							ce.drawobj(g);
					}
				}

				if ( isShowRSSConnection() == true ) {
					synchronized ( rssServerConnectionEdges ) {
						for ( RSSServerConnectionEdge e : rssServerConnectionEdges )
							e.drawobj(g);
					}
				}

				if ( isShowTopLayer() == true ) {
					synchronized ( overlayEdges ) {
						for ( OverlayEdge e : overlayEdges )
							e.drawobj(g);
					}
				}

				synchronized ( nodeList ) {
					for ( Node cn : nodeList )
						cn.drawobj(g);
				}

				if ( isShowMessages() == true ) {
					synchronized ( messageList ) {
						for ( Message cm : messageList )
							cm.drawobj(g);

						// if ( messageList.size() > 1000 ){
						// for(Message m:messageList)
						// System.err.println(m+"->src:"+m.src+"->dst:"+m.dst);
						// System.err.print(messageList.size() + ":" + getTime()
						// + ":" + messagequeue.first() + ":"
						// + messagequeue.first().getArrivaltime() + "\n");
						// }

					}
				}

				synchronized ( graphicalObjects ) {
					for ( GraphicalObject go : graphicalObjects )
						go.drawobj(g);
				}

			} catch ( Exception e ) {
				System.out.println("Engine.draw(): " + e);
			}

		}

	}

	/**
	 * Scale virtual display X coordinates to actual display coordinates.
	 * 
	 * Position data for nodes and messages is stored in virtual coordinates
	 * ranging from (0,0) to (1000,1000). Call this method to scale a virtual X
	 * coordinate to the actual width of the display.
	 * 
	 * @param x
	 *            The virtual X coordinate.
	 * @return The physical X coordinate on the current display.
	 */
	static public int scaleX(int x) {
		return x * singleton.gui.guiWidth() / 1000;
	}

	/**
	 * Scale virtual display Y coordinates to actual display coordinates.
	 * 
	 * Position data for nodes and messages is stored in virtual coordinates
	 * ranging from (0,0) to (1000,1000). Call this method to scale a virtual Y
	 * coordinate to the actual height of the display.
	 * 
	 * @param y
	 *            The virtual Y coordinate.
	 * @return The physical Y coordinate on the current display.
	 */
	static public int scaleY(int y) {
		return y * singleton.gui.guiHeight() / 1000;
	}

	static public int deScaleX(int x) {
		return x * 1000 / singleton.gui.guiWidth();
	}

	static public int deScaleY(int y) {
		return y * 1000 / singleton.gui.guiHeight();
	}

	/**
	 * Migrate new messages.
	 * 
	 * This method migrated all new messages to the standard message list.
	 */
	final void fixupMessageList() {

		synchronized ( newMessages ) {
			synchronized ( messageList ) {
				messageList.addAll(newMessages);
			}
			newMessages.clear();
		}
	}

	private class MessageComparator implements Comparator<Message> {

		public int compare(Message m1, Message m2) {

			if ( m1.getArrivaltime() < m2.getArrivaltime() )
				return -1;
			else if ( m1.getArrivaltime() > m2.getArrivaltime() )
				return 1;
			else {

				if ( m1.getId() < m2.getId() )
					return -1;
				else if ( m1.getId() > m2.getId() )
					return 1;
				else
					return 0;

			}
		}

	}

	private class DiscreteEngineTask extends TimerTask {

		@Override
		public void run() {

			long timeAccumulation = 0;
			long oldTime = System.currentTimeMillis();

			setTime(0);

			for ( Node node : startList )
				node.start();

			Message message;

			if ( getTimeLimit() > 0 ) {

				progressBarAccessor.setMaximum((int) getTimeLimit());
				progressBarAccessor.setMinimum(0);
				progressBarAccessor.setValue(0);
				progressBarAccessor.setStringPainted(true);

			}

			//						
			// displayMessageQueue();
			//						
			// System.exit(0);

			while ( true ) {

				if ( isExit() == true )
					System.exit(0);

				if ( active == true ) {

					try {

						if ( isStopped() == true ) {

							setStopped(false);
							active = false;

						}

						repaintGUI();

						synchronized ( messagequeue ) {

							message = messagequeue.first();

							if ( message != null && messagequeue.remove(message) == false )
								throw new Exception("could not remove message!");

						}

						if ( message != null ) {

							if ( message.getArrivaltime() < getTime() )
								System.err.println("WARNING: time is decreasing: old value: " + getTime() + " new value: " + message.getArrivaltime()
										+ " occured at class: " + message.getClass());

							// -------------------------------------
							// this section is to realise continuous time, which
							// can be set up by the user
							if ( isContinuousTime() == true ) {

								long timediff = message.getArrivaltime() - getTime();

								timeAccumulation += System.currentTimeMillis() - oldTime;

								if ( timediff > 0 ) {

									long timedelay = (long) (timediff * ctFactor) - timeAccumulation;

									if ( timedelay > 0 )
										Thread.sleep(timedelay);

									timeAccumulation = 0;

								}

								oldTime = System.currentTimeMillis();

							}
							// -------------------------------------

							setTime(message.getArrivaltime());

							if ( getTimeLimit() > 0 )
								progressBarAccessor.setValue((int) getTime());

							if ( getTime() < 0 ) {
								System.err.println("ERROR: time<0!: " + getTime());
								System.err.println("Message was: " + message.getClass());
								System.exit(1);
							}

							message.getDst().receiveMessage(message);
							//						
							// System.out.println("Time: "+getTime());

							// Prompting-Dialog should reduce processing speed
							// for displaying accurately
							if ( isPrompting() == true ) {

								try {
									Thread.sleep(idleTimerPeriod);
								} catch ( Exception e ) {
									System.out.println("discrete engine run(): " + e);
									e.printStackTrace();
								}

							}

						} else
							System.out.println("event-queue empty! Should not happen!");
						// try {
						// Thread.sleep(50);
						// } catch ( Exception e ) {
						// }

						// System.out.println("Engine: processing, messagetype:
						// " +
						// message.getClass() + " id: "+message+" Src: " +
						// message.getSrc() + " Time:" + getTime());

					} catch ( NoSuchElementException e ) {

					} catch ( Exception e ) {
						System.out.println("" + e + e.getStackTrace());
						e.printStackTrace();
					}

				} else {

					if ( isStarted() == true ) {

						infoOutput.write("Simulation started\n");
						setStarted(false);
						active = true;

					}

					repaintGUI();

					try {
						Thread.sleep(idleTimerPeriod);
					} catch ( Exception e ) {
						System.out.println("discrete engine run(): " + e);
						e.printStackTrace();
					}

				}

			}
		}

	}

	private class RealtimeEngineTask extends TimerTask {

		/**
		 * Step simulation one step further.
		 * 
		 * It does make no sense to call this method manually. This method is
		 * public for pure technical reasons.
		 */
		final public void run() {

			int period = idleTimerPeriod;

			while ( true ) {

				try {

					if ( isExit() == true )
						System.exit(0);

					long starttime = getTime();

					if ( active ) {

						if ( isStopped() ) {

							setStopped(false);
							active = false;

							final java.util.Date d = new java.util.Date();

							System.out.println("[" + d + "] ...simulation stopped");
							System.out.println("[" + d + "] simulation time  : " + simSteps / 10 + "." + simSteps % 10 + "s");
							System.out.println("[" + d + "] # of messages    : " + Message.getMessages());
							System.out.println("[" + d + "] max # of messages: " + maxMessages);
							System.out.println("[" + d + "] avg # of messages: " + (double) cumMessages / simSteps);

						} else {

							final int numMessages;

							synchronized ( messageList ) {

								fixupMessageList();
								java.util.Iterator<Message> ml = messageList.iterator();
								while ( ml.hasNext() ) {
									Message cm = ml.next();
									if ( cm.tick() ) {
										ml.remove();
									}
								}
								fixupMessageList();
								numMessages = messageList.size();

							}

							++simSteps;
							cumMessages += numMessages;
							if ( numMessages > maxMessages )
								maxMessages = numMessages;

							getTimeNotifier().notifyObservers(getTime());

						}

					} else {

						if ( isStarted() ) {

							setStarted(false);
							active = true;

							// call start() for all nodes in startList
							for ( Node node : startList )
								node.start();

							period = getTimerPeriod();

						}

						if ( isContinued() ) {

							setContinued(false);
							active = true;

						}
					}

					if ( gui != null )
						if ( gui.isPaintingTile() == false )
							repaintGUI();

					long stoptime = getTime();

					long diff = stoptime - starttime;

					long delay;

					if ( diff >= period )
						delay = 0;
					else
						delay = period - diff;

					Thread.sleep(delay);

				} catch ( Exception e ) {
					System.err.println("Engine.run(): " + e);
					e.printStackTrace();
				}
				// t.schedule(new RealtimeEngineTask(), delay);

			}
		}
	}

	public void repaintGUI() {
		if ( gui != null )
			gui.repaint();
		// gui.repaint(0, 0, 0, gui.getWidth(), gui.getHeight());
	}

	/**
	 * 
	 * @return status of started
	 */
	public synchronized boolean isStarted() {
		return started;
	}

	/**
	 * Only for internal use.
	 * 
	 * @param started
	 */
	public synchronized void setStarted(boolean started) {
		this.started = started;
	}

	/**
	 * 
	 * @return status of stopped
	 */
	public synchronized boolean isStopped() {
		return stopped;
	}

	/**
	 * Only for internal use.
	 * 
	 * @param stopped
	 */
	public synchronized void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	/**
	 * @return Returns the continued.
	 */
	public synchronized boolean isContinued() {
		return continued;
	}

	/**
	 * @param continued
	 *            The continued to set.
	 */
	public synchronized void setContinued(boolean continued) {
		this.continued = continued;
	}

	public Node findNode(Point point) {

		synchronized ( nodeList ) {

			for ( Node node : nodeList ) {
				if ( node.pointWhithin(point) == true )
					return node;
			}
			return null;

		}
	}

	public LinkedList<Node> findNodes(Point point1, Point point2) {

		LinkedList<Node> nodes = new LinkedList<Node>();

		synchronized ( nodeList ) {

			for ( Node node : nodeList ) {
				if ( node.whithinRectangle(point1, point2) ) {
					nodes.add(node);
				}
			}

			return nodes;

		}
	}

	public Node findNode(int id) {

		synchronized ( nodeList ) {

			for ( Node node : nodeList ) {
				if ( node.getId() == id )
					return node;
			}

			return null;
		}

	}

	/**
	 * @return Returns the timerDelay.
	 */
	public int getTimerDelay() {
		return timerDelay;
	}

	/**
	 * @return Returns the timerPeriod.
	 */
	public int getTimerPeriod() {
		return timerPeriod;
	}

	/**
	 * @param timerPeriod
	 *            The timerPeriod to set.
	 */
	public void setTimerPeriod(int timerPeriod) {
		this.timerPeriod = timerPeriod;
	}

	/**
	 * @return Returns the gui.
	 */
	public synchronized Gui getGui() {
		return gui;
	}

	public void addWindow(JFrame window) {
		synchronized ( windows ) {
			windows.add(window);
		}
	}

	public void removeWindow(JFrame window) {

		synchronized ( windows ) {

			if ( windows.remove(window) == false )
				System.err.println("WARNING: Engine.removeWindow(): window not contained in list: " + window);

		}

	}

	public HashSet<JFrame> getWindowsSet() {
		return windows;
	}

	/**
	 * @return Returns the time.
	 */
	public long getTime() {

		if ( params.isDiscreteSimulation() == true ) {
			synchronized ( discreteTime ) {
				return discreteTime;
			}
		} else
			return System.currentTimeMillis() - getInittime().getTime();
	}

	/**
	 * @param time
	 *            The time to set.
	 */
	public void setTime(long time) {
		synchronized ( this.discreteTime ) {
			if ( this.discreteTime != time ) {
				this.discreteTime = time;
				getTimeNotifier().notifyObservers(time);
			}
		}
	}

	public void displayMessageQueue() {

		Iterator<Message> it = messagequeue.iterator();
		Message m;

		while ( it.hasNext() ) {

			m = it.next();
			System.out.println(m.getClass() + "  scheduled time: " + m.getArrivaltime());

		}

	}

	/**
	 * @return Returns the showSubLayer.
	 */
	public synchronized boolean isShowSubLayer() {
		return showSubLayer;
	}

	/**
	 * @param showSubLayer
	 *            The showSubLayer to set.
	 */
	public synchronized void setShowSubLayer(boolean showSubLayer) {
		this.showSubLayer = showSubLayer;
	}

	/**
	 * @return Returns the showTopLayer.
	 */
	public synchronized boolean isShowTopLayer() {
		return showTopLayer;
	}

	/**
	 * @param showTopLayer
	 *            The showTopLayer to set.
	 */
	public synchronized void setShowTopLayer(boolean showTopLayer) {
		this.showTopLayer = showTopLayer;
	}

	/**
	 * @return Returns the routingtable.
	 */
	public static synchronized Node[][] getRoutingtable() {
		return routingtable;
	}

	/**
	 * @param routingtable
	 *            The routingtable to set.
	 */
	public static synchronized void setRoutingtable(Node[][] routingtable) {
		Engine.routingtable = routingtable;
	}

	/**
	 * @return Returns the costtable.
	 */
	public static synchronized double[][] getCosttable() {
		return costtable;
	}

	/**
	 * @param costtable
	 *            The costtable to set.
	 */
	public static synchronized void setCosttable(double[][] costtable) {
		Engine.costtable = costtable;
	}

	/**
	 * @return Returns the hoptable.
	 */
	public static synchronized int[][] getHoptable() {
		return hoptable;
	}

	/**
	 * @param hoptable
	 *            The hoptable to set.
	 */
	public static synchronized void setHoptable(int[][] hoptable) {
		Engine.hoptable = hoptable;
	}

	/**
	 * @return Returns the numberOfNodes.
	 */
	public static synchronized int getNumberOfNodes() {
		return numberOfNodes;
	}

	/**
	 * @param numberOfNodes
	 *            The numberOfNodes to set.
	 */
	public static synchronized void setNumberOfNodes(int numberOfNodes) {
		Engine.numberOfNodes = numberOfNodes;
	}

	/**
	 * @return Returns the exit.
	 */
	public synchronized boolean isExit() {
		return exit;
	}

	/**
	 * @param exit
	 *            The exit to set.
	 */
	public synchronized void setExit(boolean exit) {
		this.exit = exit;
	}

	/**
	 * @return Returns the timeNotifier.
	 */
	public synchronized TimeNotifier getTimeNotifier() {
		return timeNotifier;
	}

	public synchronized void addTimeObserver(Observer observer) {
		getTimeNotifier().addObserver(observer);
	}

	public void deleteTimeObserver(Observer observer) {
		getTimeNotifier().deleteObserver(observer);
	}

	/**
	 * @return Returns the infoOutput.
	 */
	public InfoOutput getInfoOutput() {
		return infoOutput;
	}

	/**
	 * @return Returns the progressBar.
	 */
	public synchronized AbstractProgressBar getProgressBar() {
		return progressBar;
	}

	/**
	 * @param progressBar
	 *            The progressBar to set.
	 */
	public synchronized void setProgressBar(AbstractProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	/**
	 * @return Returns the startTime.
	 */
	protected synchronized Date getInittime() {
		return inittime;
	}

	/**
	 * @param startTime
	 *            The startTime to set.
	 */
	protected synchronized void setInittime(Date startTime) {
		this.inittime = startTime;
	}

	public synchronized void addPubSub(PubSubType pubsub) {
		pubsubcount++;
		pubsubindizes.put(pubsub, pubsubcount - 1);
		rpsStatistics.getCurrPollPeriods().add(new Long(0));
	}

	public synchronized void addBroker(BrokerType broker) {

		brokercount++;
		brokers.add(broker);

	}

	/**
	 * @return Returns the pubsubindizes.
	 */
	public static HashMap<PubSubType, Integer> getPubsubindizes() {
		return pubsubindizes;
	}

	/**
	 * @return Returns the visualization.
	 */
	public synchronized boolean isVisualization() {
		return visualization;
	}

	/**
	 * @param visualization
	 *            The visualization to set.
	 */
	public synchronized void setVisualization(boolean visualization) {
		this.visualization = visualization;
	}

	/**
	 * @return Returns the interactiveMode.
	 */
	public synchronized boolean isInteractiveMode() {
		return interactiveMode;
	}

	/**
	 * @param interactiveMode
	 *            The interactiveMode to set.
	 */
	public synchronized void setInteractiveMode(boolean interactiveMode) {
		this.interactiveMode = interactiveMode;
	}

	/**
	 * @return Returns the transferMessageDispatcher.
	 */
	public synchronized TransferMessageDispatcher getTransferMessageDispatcher() {
		return transferMessageDispatcher;
	}

	/**
	 * @return Returns the messageList.
	 */
	public synchronized java.util.Set<Message> getMessageList() {
		return messageList;
	}

	/**
	 * @return Returns the showMessages.
	 */
	public synchronized boolean isShowMessages() {
		return showMessages;
	}

	/**
	 * @param showMessages
	 *            The showMessages to set.
	 */
	public synchronized void setShowMessages(boolean showMessages) {
		this.showMessages = showMessages;
	}

	/**
	 * @return Returns the timeLimit in ticks.
	 */
	public synchronized long getTimeLimit() {
		return timeLimit;
	}

	/**
	 * @param timeLimit
	 *            The timeLimit in ticks to set.
	 */
	public synchronized void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
	}

	/**
	 * @return Returns the random.
	 */
	public synchronized Random getRandom() {
		return random;
	}

	/**
	 * @return Returns the messagesOnTopLayer.
	 */
	public synchronized boolean isMessagesOnTopLayer() {
		return messagesOnTopLayer;
	}

	/**
	 * @param messagesOnTopLayer
	 *            The messagesOnTopLayer to set.
	 */
	public synchronized void setMessagesOnTopLayer(boolean messagesOnTopLayer) {
		this.messagesOnTopLayer = messagesOnTopLayer;
	}

	/**
	 * @return Returns the manualRecording.
	 */
	public synchronized boolean isManualRecording() {
		return manualRecording;
	}

	/**
	 * @param manualRecording
	 *            The manualRecording to set.
	 */
	public synchronized void setManualRecording(boolean manualRecording) {
		this.manualRecording = manualRecording;
		manualRecordingNotifier.notifyObservers(manualRecording);
		// if (getGui()!=null)
		// getGui().setRecordingEnabled(manualRecording);
	}

	public synchronized void addManualRecordingObserver(Observer observer) {
		manualRecordingNotifier.addObserver(observer);
	}

	public synchronized void deleteManualRecordingObserver(Observer observer) {
		manualRecordingNotifier.deleteObserver(observer);
	}

	/**
	 * @return Returns the manualRecordingNotifier.
	 */
	public synchronized ManualRecordingNotifier getManualRecordingNotifier() {
		return manualRecordingNotifier;
	}

	/**
	 * @return Returns the showRSSConnection.
	 */
	public synchronized boolean isShowRSSConnection() {
		return showRSSConnection;
	}

	/**
	 * @param showRSSConnection
	 *            The showRSSConnection to set.
	 */
	public synchronized void setShowRSSConnection(boolean showRSSConnection) {
		this.showRSSConnection = showRSSConnection;
	}

	/**
	 * @return Returns the brokers.
	 */
	public static synchronized Set<BrokerType> getBrokers() {
		return brokers;
	}

	/**
	 * @return Returns the prompting.
	 */
	public synchronized boolean isPrompting() {
		return prompting;
	}

	/**
	 * @param prompting
	 *            The prompting to set.
	 */
	public synchronized void setPrompting(boolean prompting) {
		this.prompting = prompting;
	}

	/**
	 * @param continuousTime
	 *            The continuousTime to set.
	 */
	public synchronized void setContinuousTime(boolean continuousTime) {

		this.continuousTime = continuousTime;

		if ( continuousTime == true )
			new SyncBeatMessage().send();

	}

	/**
	 * @return Returns the continuousTime.
	 */
	public synchronized boolean isContinuousTime() {
		return continuousTime;
	}

	/**
	 * @return Returns the params.
	 */
	public SimParameters getParams() {
		return params;
	}

}
