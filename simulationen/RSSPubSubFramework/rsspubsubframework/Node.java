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
 * Base class for all node implementations.
 * 
 * This class is abstract because it does not make sense to have any generic
 * initialization or receive message triggered code. Thus you must implement a
 * subtype of this class for real applications.
 */
abstract public class Node extends DisplayableObject {
    // / Serial number counter.
    private static int currentSerial = 0;

    // / Serial number of current object.
    private final int serial = currentSerial++;

    // / Node has id.
    private boolean idSet = false;

    // / Node id.
    private int id;

    // / X coordinate of the node.
    private final int x;

    // / Y coordinate of the node.
    private final int y;

    // / List of peers.
    private final java.util.Set<Node> peers = new java.util.HashSet<Node>();

    /**
     * Create new node.
     * 
     * Does generic initialization of the base class. You cannot call this
     * constructor directly because the class is abstract.
     * 
     * @param xp
     *            Horizontal position of the node on display.
     * @param yp
     *            Vertical position of the node on display.
     */
    protected Node(int xp, int yp) {
        x = xp;
        y = yp;
        Engine.getSingleton().addNode(this);
    }

    /**
     * Register a peer node.
     * 
     * This method is called by an edge constructor.
     * 
     * @param p
     *            Peer node.
     */
    void addpeer(Node p) {
        peers.add(p);
    }

    /**
     * Send a message to a specific node.
     * 
     * Overwrite this method if you want to send a non-generic type of message.
     * You cannot hand down specific information by parameter to this method.
     * Instead just store the required information into a member variable and
     * use it within send().
     * 
     * @param dst
     *            Node the package should be sent to.
     */
    protected void send(Node dst) {
        new Message(this, dst);
    }

    /**
     * Send a message to all peer nodes.
     * 
     * This method finally calls send() for each peer node. You cannot overwrite
     * this method to hand down additional information to send() for message
     * creation. Instead just store the required information into a member
     * variable and use it within send().
     */
    final protected void sendAll() {
        sendAllBut(null);
    }

    /**
     * Send a message to all peer nodes but the one specified.
     * 
     * This method finally calls send() for each peer node but the specified
     * one. You cannot overwrite this method to hand down additional information
     * to send() for message creation. Instead just store the required
     * information into a member variable and use it within send().
     * 
     * @param notSend
     *            Node the package should not be sent to.
     */
    final protected void sendAllBut(Node notSend) {
        for (Node cn : peers)
            if (cn != notSend)
                send(cn);
    }

    /**
     * Send a message to the peer located one further clockwise.
     * 
     * This method should not be called on any other topology than a ring
     * topology. When called on any other topology the behaviour is undefined.
     * This method finally calls send(). You cannot overwrite this method to
     * hand down additional information to send() for message creation. Instead
     * just store the required information into a member variable and use it
     * within send().
     */
    final protected void sendClock() {
        Node cn = peers.iterator().next();
        if (cn.serial - serial == 1
                || (serial - cn.serial != 1 && cn.serial < serial))
            send(cn);
        else
            sendAllBut(cn);
    }

    /**
     * Send a message to the peer located one further counter clockwise.
     * 
     * This method should not be called on any other topology than a ring
     * topology. When called on any other topology the behaviour is undefined.
     * This method finally calls send(). You cannot overwrite this method to
     * hand down additional information to send() for message creation. Instead
     * just store the required information into a member variable and use it
     * within send().
     */
    final protected void sendCounterClock() {
        Node cn = peers.iterator().next();
        if (serial - cn.serial == 1
                || (cn.serial - serial != 1 && cn.serial > serial))
            send(cn);
        else
            sendAllBut(cn);
    }
    
    /**
     * Adds the node to initList of Engine.
     * init() will be called automatically by the Engine when simulation is started
     */
    public final void addToInitList(){
    	Engine.getSingleton().addToInitList(this);
    }

    /**
     * Called to initialize the node.
     * 
     * Implement this method with the code to be executed on node
     * initialization.
     */
    abstract public void init();

    /**
     * Called when node receives a message.
     * 
     * Implement this method with the code to be executed when a node receives a
     * message.
     * 
     * @param m
     *            Message that was sent to the node.
     */
    abstract protected void receiveMessage(Message m);

    /**
     * Return X coordinate of the node.
     */
    int xPos() {
        return Engine.scaleX(x);
    }

    /**
     * Return Y coordinate of the node.
     */
    int yPos() {
        return Engine.scaleY(y);
    }

    /**
     * Returns the number of peers this node is connected to.
     * 
     * @return Number of peers.
     */
    protected int numPeers() {
        return peers.size();
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
        draw(g, Engine.scaleX(x), Engine.scaleY(y));
    }

    /**
     * Return the text to display.
     * 
     * Overwrite this method to have custom text.
     * 
     * @return Text.
     */
    protected String text() {
        return idSet ? String.valueOf(id) : "";
    }

    /**
     * Set the individual id of a node.
     * 
     * Call this method to give a specific id to a node. This id is not
     * necessarily unique. If you want unique ids, you may call setUniqueIds()
     * on the Engine object. If you did not overwrite the text() method, the id
     * is displayed when set.
     * 
     * @param idp
     *            The id to be set on the node.
     */
    final public void setId(int idp) {
        id = idp;
        idSet = true;
    }

    /**
     * Get the individual id of a node.
     * 
     * This method returns the specific id of a node.
     * 
     * @return The id of the node.
     */
    final protected int getId() {
        return id;
    }

    /**
     * Get list of peer nodes.
     * 
     * This method returns a list of all peer nodes of this node.
     * 
     * @return List of all peers.
     */
    protected java.util.Set<Node> getPeers() {
        final java.util.Set<Node> peerList = new java.util.HashSet<Node>();
        peerList.addAll(peers);
        return peerList;
    }
}
