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

/**
 * The Engine.
 * 
 * This is the main engine for the simulation system. Every simulation needs
 * such an object.
 */
final public class Engine extends java.util.TimerTask {
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

	/**
	 * List of all nodes in the engine.
	 * 
	 * This list holds all nodes that are registered within the engine.
	 */
	private final java.util.Set<Node> nodeList = new java.util.HashSet<Node>();

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
	 * Timer object to drive the simulation.
	 * 
	 * This objects drives the simulation forward step by step.
	 */
	private final java.util.Timer t = new java.util.Timer();

	/**
	 * Engine is active.
	 * 
	 * This value marks whether the engine does contain active messages.
	 */
	private boolean active = false;

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
	 * Get the main simulation engine object.
	 * 
	 * Returns the main simulation engine object.
	 * 
	 * @return Main simulation object.
	 */
	static Engine getSingleton() {
		return singleton;
	}

	/**
	 * Initialize the engine.
	 * 
	 * Creates the gui and starts the simulation.
	 */
	private Engine() {
		t.schedule(this, 1000, 100);
	}

	/**
	 * Adds a new node to the simulation engine.
	 * 
	 * This is an internal method. You cannot call this method directly
	 * 
	 * @param localNode
	 *            The node to add.
	 */
	final synchronized void addNode(Node localNode) {
		nodeList.add(localNode);
	}

	/**
	 * Adds a new edge to the simulation engine.
	 * 
	 * This is an internal method. You cannot call this method directly
	 * 
	 * @param localEdge
	 *            The edge to add.
	 */
	final synchronized void addEdge(Edge localEdge) {
		edgeList.add(localEdge);
	}

	/**
	 * Adds a new message to the simulation engine.
	 * 
	 * This is an internal method. You cannot call this method directly.
	 * 
	 * @param localMessage
	 *            The message to add.
	 */
	final synchronized void addMessage(Message localMessage) {
		newMessages.add(localMessage);
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
		int s = singleton.nodeList.size();
		int a[] = getRandomPermutation(s);
		Node n[] = new Node[s];
		n = singleton.nodeList.toArray(n);
		if ( s > num )
			s = num;
		while ( s > 0 )
			n[a[--s]].init();
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
	final synchronized private void internalSetUniqueIds() {
		int a[] = getRandomPermutation(nodeList.size());
		java.util.Iterator<Node> nl = nodeList.iterator();
		for ( int i = 0; i < nodeList.size(); ++i ) {
			nl.next().setId(a[i] + 1);
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
	final synchronized private void internalSetRandomIds(int max) {
		java.util.Iterator<Node> nl = nodeList.iterator();
		while ( nl.hasNext() ) {
			nl.next().setId((int) (Math.random() * max) + 1);
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
	final synchronized void draw(java.awt.Graphics g) {
		for ( Edge ce : edgeList )
			ce.drawobj(g);
		for ( Node cn : nodeList )
			cn.drawobj(g);
		for ( Message cm : messageList )
			cm.drawobj(g);
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

	/**
	 * Migrate new messages.
	 * 
	 * This method migrated all new messages to the standard message list.
	 */
	final synchronized void fixupMessageList() {
		messageList.addAll(newMessages);
		newMessages.clear();
	}

	/**
	 * Step simulation one step further.
	 * 
	 * It does make no sense to call this method manually. This method is public
	 * for pure technical reasons.
	 */
	final synchronized public void run() {
		fixupMessageList();
		java.util.Iterator<Message> ml = messageList.iterator();
		while ( ml.hasNext() ) {
			Message cm = ml.next();
			if ( cm.tick() )
				ml.remove();
		}
		fixupMessageList();
		final int numMessages = messageList.size();
		final java.util.Date d = new java.util.Date();
		if ( active ) {
			if ( numMessages == 0 ) {
				active = false;
				System.out.println("[" + d + "] ...simulation stopped");
				System.out.println("[" + d + "] simulation time  : " + simSteps / 10 + "." + simSteps
						% 10 + "s");
				System.out.println("[" + d + "] # of messages    : " + Message.getMessages());
				System.out.println("[" + d + "] max # of messages: " + maxMessages);
				System.out.println("[" + d + "] avg # of messages: " + (double) cumMessages / simSteps);
			} else {
				++simSteps;
				cumMessages += numMessages;
				if ( numMessages > maxMessages )
					maxMessages = numMessages;
			}
		} else {
			if ( numMessages > 0 ) {
				active = true;
				System.out.println("[" + d + "] simulation started...");
				++simSteps;
				cumMessages += numMessages;
				if ( numMessages > maxMessages )
					maxMessages = numMessages;
			}
		}
		db.repaint(0, 0, 0, db.getWidth(), db.getHeight());
	}
}
