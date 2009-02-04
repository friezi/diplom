package de.zintel.diplom.simulation;
import de.zintel.diplom.graphics.DisplayableObject;

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

/**
 * Base class for all message implementations.
 */
public class Message extends DisplayableObject {
	// / Total number of messages created during simulation.
	private static int msgcount = 0;

	// / Source node of the message.
	protected Node src;

	// / Destination node of the message.
	protected Node dst;

	// / Simulation steps message needs to reach destination.
	private int runtime;

	// / Simulation steps message is already living.
	public int pos = 0;

	// discrete simulation: the time at which this message should arrive at
	// destination
	private long arrivaltime;

	private static long idcount = Long.MIN_VALUE;

	private long id = 0;

	protected Message() {
	}

	/**
	 * Create a message with specific runtime.
	 * 
	 * This constructor creates a new message with a specific runtime from srcp
	 * to dstp. Message ist not yet send to destination: for this purpose use
	 * send() -- modified by Friedemann Zintel --
	 * 
	 * @param srcp
	 *            The node sending the message.
	 * @param dstp
	 *            The node the package is sent to.
	 * @param rt
	 *            Runtime of the message.
	 */
	public Message(Node srcp, Node dstp, int rt) {
		init(srcp, dstp, rt);
	}

	protected void init(Node srcp, Node dstp, int rt) {

		src = srcp;
		dst = dstp;
		runtime = rt;
		id = nextId();
		resetArrivalTime();

	}

	/**
	 * sends the message to destination (practically: adds the message to the
	 * list newMessages of Engine so that it will be processed -- added by
	 * Friedemann Zintel --
	 * 
	 * @return the message itself
	 */
	public Message send() {

		Engine.getSingleton().addMessage(this);
		++msgcount;
		return this;

	}

	public void forward(Node src, Node dst) {

		this.src = src;
		this.dst = dst;
		resetVariingAttributes();
		send();

	}

	/**
	 * Tries to recall a message, i.e. stop it from being delivered.
	 * 
	 * @return the message itself
	 */
	public Message recall() {

		if ( Engine.getSingleton().removeMessage(this) == true )
			--msgcount;
		return this;

	}

	/**
	 * Returns total number of messages.
	 * 
	 * This method returns the total number of messages that were created during
	 * simulation.
	 * 
	 * @return Total number of messages.
	 */
	static int getMessages() {
		return msgcount;
	}

	/**
	 * Create a message.
	 * 
	 * Use this constructor to create a new default message from srcp to dstp.
	 * 
	 * @param srcp
	 *            The node sending the message.
	 * @param dstp
	 *            The node the package is sent to.
	 */
	public Message(Node srcp, Node dstp) {
		this(srcp, dstp, 10 + (int) (Math.random() * 50));
	}

	/**
	 * Run simulation one step further.
	 * 
	 * This method is called by the engine for each step of the simulation. If
	 * message has reached end of life, it returns true.
	 * 
	 * @return Message has ended lifetime.
	 */
	boolean tick() {
		// try {
		if ( ++pos == runtime ) {
			dst.receiveMessage(this);
			return true;
		}
		// } catch ( Exception e ) {
		// System.err.println(e);
		// System.err.println("tick(): Message-class: " + this.getClass());
		// return true;
		// }
		return false;
	}

	/**
	 * Return the immediate sender-node of the message.
	 * 
	 * Use this method when you receive a message to determine sender-node.
	 * 
	 * @return Source node of the message.
	 */
	public Node getSrc() {
		return src;
	}

	/**
	 * Wrapper method for draw() method.
	 * 
	 * This is an internal wrapper method that calls the draw() method with
	 * actual position parameters.
	 * 
	 * Must be public to allow overriding for special messages --> added
	 * Friedemann Zintel
	 * 
	 * @param g
	 *            The Graphics object the object should be drawn on.
	 */
	public void drawobj(java.awt.Graphics g) {
		try {
			draw(g, src.xPos() + (dst.xPos() - src.xPos()) * pos / runtime, src.yPos() + (dst.yPos() - src.yPos()) * pos / runtime);
		} catch ( Exception e ) {
			System.err.println(e);
			System.err.println("drawobj(): Message-class: " + this.getClass());
			System.err.println("src: " + src + "    dst: " + dst);
		}
	}

	/**
	 * Return the size on display.
	 * 
	 * Overwrite this method to customize size.
	 * 
	 * @return Size.
	 */
	public int size() {
		return 30;
	}

	protected long nextId() {

		if ( idcount == Long.MAX_VALUE )
			idcount = Long.MIN_VALUE;
		else
			idcount++;

		return idcount;

	}

	/**
	 * @return Returns the runtime.
	 */
	public int getRuntime() {
		return runtime;
	}

	/**
	 * @return Returns the arrivaltime.
	 */
	public long getArrivaltime() {
		return arrivaltime;
	}

	/**
	 * Sets the time at which a message should arrive at the recipient.
	 * 
	 * Only evaluated in discrete event-simulation.
	 * 
	 * @param arrivaltime
	 *            The arrivaltime to set.
	 */
	public void setArrivaltime(long arrivaltime) {
		this.arrivaltime = arrivaltime;
	}

	/**
	 * Resets the arrival-time to default-value.
	 * 
	 */
	public void resetArrivalTime() {
		setArrivaltime(Engine.getSingleton().getTime() + Engine.getSingleton().getTimerPeriod() * runtime);
	}

	/**
	 * @return Returns the final destination of message.
	 */
	public Node getDst() {
		return dst;
	}

	/**
	 * @return Returns the id.
	 */
	public synchronized long getId() {
		return id;
	}

	protected synchronized void resetPos() {
		pos = 0;
	}

	protected synchronized void resetVariingAttributes() {
		resetArrivalTime();
		resetPos();
	}
}
