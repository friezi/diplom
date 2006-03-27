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
 * Base class for all message implementations.
 */
public class Message extends DisplayableObject {
    // / Total number of messages created during simulation.
    private static int msgcount = 0;

    // / Source node of the message.
    private final Node src;

    // / Destination node of the message.
    private final Node dst;

    // / Simulation steps message needs to reach destination.
    private final int runtime;

    // / Simulation steps message is already living.
    private int pos = 0;

    /**
     * Create a message with specific runtime.
     * 
     * This constructor creates a new message with a specific runtime from srcp
     * to dstp.
     * 
     * @param srcp
     *            The node sending the message.
     * @param dstp
     *            The node the package is sent to.
     * @param rt
     *            Runtime of the message.
     */
    public Message(Node srcp, Node dstp, int rt) {
        src = srcp;
        dst = dstp;
        runtime = rt;
        Engine.getSingleton().addMessage(this);
        ++msgcount;
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
        if (++pos == runtime) {
            dst.receiveMessage(this);
            return true;
        }
        return false;
    }

    /**
     * Return the source node of the message.
     * 
     * Use this method when you receive a message to determine sender node.
     * 
     * @return Source node of the message.
     */
    public final Node getSrc() {
        return src;
    }

    /**
     * Wrapper method for draw() method.
     * 
     * This is an internal wrapper method that calls the draw() method with
     * actual position parameters.
     * 
     * @param g
     *            The Graphics object the object should be drawn on.
     */
    final void drawobj(java.awt.Graphics g) {
        draw(g, src.xPos() + (dst.xPos() - src.xPos()) * pos / runtime, src
                .yPos()
                + (dst.yPos() - src.yPos()) * pos / runtime);
    }

    /**
     * Return the size on display.
     * 
     * Overwrite this method to customize size.
     * 
     * @return Size.
     */
    protected int size() {
        return 30;
    }

	/**
	 * @return Returns the runtime.
	 */
	public int getRuntime() {
		return runtime;
	}
}
