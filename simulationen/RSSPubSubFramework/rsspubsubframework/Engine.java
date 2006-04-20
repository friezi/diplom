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

package rsspubsubframework;

import java.util.*;
import java.awt.*;

/**
 * The Engine.
 * 
 * This is the main engine for the simulation system. Every simulation needs
 * such an object.
 */
final public class Engine {

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
	private final Gui db = new Gui();

	private RPSStatistics rpsStatistics = new RPSStatistics();

	/**
	 * List of all nodes in the engine.
	 * 
	 * This list holds all nodes that are registered within the engine.
	 */
	final java.util.Set<Node> nodeList = new java.util.HashSet<Node>();

	/**
	 * List of all nodes which should be initialized by the engine when
	 * Simulation started.
	 */
	private final java.util.LinkedList<Node> initList = new LinkedList<Node>();

	/**
	 * List of all edges in the engine.
	 * 
	 * This list holds all edges that are registered within the engine.
	 */
	private final java.util.Set<Edge> edgeList = new java.util.HashSet<Edge>();

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

	private int timerDelay = 1000;

	private int timerPeriod = 20;

	private Date date = new Date();

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
	 */
	public void init() {
		t.schedule(new EngineTask(), getTimerDelay());
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
			nodeList.add(localNode);
		}
	}

	/**
	 * Adds a new edge to the simulation engine.
	 * 
	 * This is an internal method. You cannot call this method directly
	 * 
	 * @param localEdge
	 *            The edge to add.
	 */
	final void addEdge(Edge localEdge) {
		synchronized ( edgeList ) {
			edgeList.add(localEdge);
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
		synchronized ( edgeList ) {
			edgeList.add(new Edge(node1, node2));
		}
	}

	/**
	 * removes an edge from the list and disconnects the peers.
	 * 
	 * @param edge
	 *            the edge
	 */
	final void removeEdge(Edge edge) {
		synchronized ( edgeList ) {
			edgeList.remove(edge);
			edge.removeConnection();
		}
	}

	// modified by Friedemann Zintel
	public final void removeEdgeFromNodes(Node node1, Node node2) {

		boolean foundfirst = false;
		boolean foundsecond = false;

		synchronized ( edgeList ) {

			for ( Edge edge : edgeList ) {

				if ( foundfirst == true && foundsecond == true )
					break;

				if ( (edge.node1() == node1 && edge.node2() == node2) ) {

					removeEdge(edge);
					foundfirst = true;

				} else if ( (edge.node1() == node2 && edge.node2() == node1) ) {

					removeEdge(edge);
					foundsecond = true;

				}
			}

		}

	}

	/**
	 * Adds the node to the initList. The Engine calls init() for all nodes in
	 * initList when simulation is started.
	 * 
	 * @param node
	 *            the node to be added
	 */
	public final synchronized void addToInitList(Node node) {
		initList.add(node);
	}

	/**
	 * Adds a new message to the simulation engine.
	 * 
	 * This is an internal method. You cannot call this method directly.
	 * 
	 * @param localMessage
	 *            The message to add.
	 */
	final void addMessage(Message localMessage) {
		synchronized ( newMessages ) {
			newMessages.add(localMessage);
		}
	}

	final void addGraphicalObject(GraphicalObject gob) {
		synchronized ( graphicalObjects ) {
			graphicalObjects.add(gob);
		}
	}

	final void removeGraphicalObject(GraphicalObject gob) {
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
	 * Initialize random nodes.
	 * 
	 * Initializes a specified number of random nodes. If the specified number
	 * is larger than the number of existing nodes, all nodes are initialized.
	 * 
	 * @param num
	 *            Number of nodes to be initialized.
	 */
	public static void initRandomNodes(int num) {
		synchronized ( singleton.nodeList ) {
			int s = singleton.nodeList.size();
			int a[] = getRandomPermutation(s);
			Node n[] = new Node[s];
			n = singleton.nodeList.toArray(n);
			if ( s > num )
				s = num;
			while ( s > 0 )
				n[a[--s]].init();
		}
	}

	/**
	 * Set unique ids for all registered nodes.
	 * 
	 * Call this method if you want to set unique ids on all registered nodes.
	 */
	public static void setUniqueIds() {
		singleton.internalSetUniqueIds();
	}

	/**
	 * Set unique ids for all registered nodes.
	 * 
	 * This is the internal method called by the wrapper method setUniqueIds().
	 */
	final private void internalSetUniqueIds() {
		synchronized ( nodeList ) {
			int a[] = getRandomPermutation(nodeList.size());
			java.util.Iterator<Node> nl = nodeList.iterator();
			for ( int i = 0; i < nodeList.size(); ++i ) {
				nl.next().setId(a[i] + 1);
			}
		}
	}

	/**
	 * Set random ids for all registered nodes.
	 * 
	 * Call this method if you want to set random ids on all registered nodes in
	 * the range from 0 to less than a specified maximum.
	 * 
	 * @param max
	 *            Random ids should be in range from 1 to this value.
	 */
	public static void setRandomIds(int max) {
		singleton.internalSetRandomIds(max);
	}

	/**
	 * Set random ids for all registered nodes.
	 * 
	 * This is the internal method called by the wrapper method setRandomIds().
	 * 
	 * @param max
	 *            Random ids should be in range from 1 to this value.
	 */
	final private void internalSetRandomIds(int max) {
		synchronized ( nodeList ) {
			java.util.Iterator<Node> nl = nodeList.iterator();
			while ( nl.hasNext() ) {
				nl.next().setId((int) (Math.random() * max) + 1);
			}
		}
	}

	/**
	 * Draw all simulation objects.
	 * 
	 * This method draws all simulation objects registered within the engine.
	 * 
	 * @param g
	 *            The Graphics object to draw on.
	 */
	final void draw(java.awt.Graphics g) {

		try {

			synchronized ( edgeList ) {
				for ( Edge ce : edgeList )
					ce.drawobj(g);
			}

			synchronized ( nodeList ) {
				for ( Node cn : nodeList )
					cn.drawobj(g);
			}

			synchronized ( messageList ) {
				for ( Message cm : messageList )
					cm.drawobj(g);
			}

			synchronized ( graphicalObjects ) {
				for ( GraphicalObject go : graphicalObjects )
					go.drawobj(g);
			}

		} catch ( Exception e ) {
			System.out.println("Engine.draw(): " + e);
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
		return x * singleton.db.guiWidth() / 1000;
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
		return y * singleton.db.guiHeight() / 1000;
	}

	static public int deScaleX(int x) {
		return x * 1000 / singleton.db.guiWidth();
	}

	static public int deScaleY(int y) {
		return y * 1000 / singleton.db.guiHeight();
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

	private class EngineTask extends TimerTask {

		/**
		 * Step simulation one step further.
		 * 
		 * It does make no sense to call this method manually. This method is
		 * public for pure technical reasons.
		 */
		final public void run() {

			long starttime = date.getTime();

			while ( true ) {

				try {

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

							repaintGUI();

						}

					} else {

						if ( isStarted() ) {

							setStarted(false);
							active = true;

							// call init() for all nodes in initList
							for ( Node node : initList )
								node.init();

						}

						if ( isContinued() ) {

							setContinued(false);
							active = true;

						}
					}

					long stoptime = date.getTime();

					long diff = stoptime - starttime;

					long delay;

					if ( diff >= getTimerPeriod() )
						delay = 0;
					else
						delay = getTimerPeriod() - diff;

					Thread.sleep(delay);
				} catch ( Exception e ) {
					System.err.println("Engine.run(): " + e);
				}
				// t.schedule(new EngineTask(), delay);

			}
		}
	}

	public void repaintGUI() {
		db.repaint(0, 0, 0, db.getWidth(), db.getHeight());
	}

	/**
	 * 
	 * @return status of started
	 */
	synchronized boolean isStarted() {
		return started;
	}

	/**
	 * Only for internal use.
	 * 
	 * @param started
	 */
	synchronized void setStarted(boolean started) {
		this.started = started;
	}

	/**
	 * 
	 * @return status of stopped
	 */
	synchronized boolean isStopped() {
		return stopped;
	}

	/**
	 * Only for internal use.
	 * 
	 * @param stopped
	 */
	synchronized void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	/**
	 * @return Returns the continued.
	 */
	synchronized boolean isContinued() {
		return continued;
	}

	/**
	 * @param continued
	 *            The continued to set.
	 */
	synchronized void setContinued(boolean continued) {
		this.continued = continued;
	}

	Node findNode(Point point) {

		synchronized ( nodeList ) {

			for ( Node node : nodeList ) {
				if ( node.pointWhithin(point) == true )
					return node;
			}
			return null;

		}
	}

	LinkedList<Node> findNodes(Point point1, Point point2) {

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
	 * @return Returns the db.
	 */
	public synchronized Gui getDb() {
		return db;
	}
}
