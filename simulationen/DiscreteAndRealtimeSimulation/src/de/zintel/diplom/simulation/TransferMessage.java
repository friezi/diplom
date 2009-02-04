package de.zintel.diplom.simulation;
/**
 * 
 */


import java.awt.Graphics;
import java.util.*;

import de.zintel.diplom.messages.TMDispatcherMessage;


/**
 * A Message which has to be transferred through maybe many nodes before it
 * reaches the final destination. If set a routing-table will be used for
 * determining the next node on the way.
 * 
 * @author Friedemann Zintel
 * 
 */
public class TransferMessage extends Message {

	private Node origin;

	private Node destination;

	private int hop = 0;

	private int hops = 0;

	public TransferMessage(Node src, Node dst, int runtime) {

		origin = src;
		destination = dst;
		Node[][] routingtable = Engine.getRoutingtable();
		int[][] hoptable = Engine.getHoptable();

		if ( hoptable != null )
			hops = hoptable[origin.getId()][destination.getId()];

		if ( routingtable != null )
			init(src, routingtable[src.getId()][destination.getId()], runtime);
		else
			init(src, dst, runtime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Message#forward(Node, Node)
	 */
	@Override
	public void forward(Node src, Node dst) {

		Node[][] routingtable = Engine.getRoutingtable();
		this.src = src;
		destination = dst;
		this.dst = (routingtable == null ? dst : routingtable[src.getId()][destination.getId()]);
		hop++;

		resetVariingAttributes();
		send();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Message#send()
	 */
	@Override
	public Message send() {

		TransferMessageDispatcher transferMessageDispatcher = Engine.getSingleton().getTransferMessageDispatcher();

		if ( transferMessageDispatcher != null && Engine.getSingleton().isShowMessages() == true && Engine.getSingleton().isVisualization() == true ) {

			Set<Message> messageList = Engine.getSingleton().getMessageList();
			synchronized ( messageList ) {

//				System.err.println("\nbefore:");
//				for ( Message message : messageList )
//					System.err.println(message);

				if ( messageList.add(this) == false ) {
					System.err.println("WARNING: TransferMessage.send(): tried to add message multiple times:" + this);

				}

//				System.out.println("\nafter:");
//				for ( Message message : messageList )
//					System.err.println(message);
			}

			new TMDispatcherMessage(transferMessageDispatcher, transferMessageDispatcher, Engine.getSingleton().getTime(), this).send();

			return this;

		} else
			return sendWithoutDispatching();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Message#recall()
	 */
	@Override
	public Message recall() {

		// to be sure we remove the message from any possible list
		TransferMessageDispatcher transferMessageDispatcher = Engine.getSingleton().getTransferMessageDispatcher();

		if ( transferMessageDispatcher != null )
			transferMessageDispatcher.recallMessage(this);
		
//		System.err.println("TM recalled "+this);

		return super.recall();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Message#drawobj(java.awt.Graphics)
	 */
	@Override
	public void drawobj(Graphics g) {

		if ( Engine.getSingleton().isMessagesOnTopLayer() == true && Engine.getHoptable() != null ) {

			// draw messages on toplayer

			try {

				draw(g, origin.xPos() + (destination.xPos() - origin.xPos()) * hop / hops + (destination.xPos() - origin.xPos()) * pos / getRuntime()
						/ hops, origin.yPos() + ((destination.yPos() - origin.yPos()) * hop / hops)
						+ ((destination.yPos() - origin.yPos()) * pos / getRuntime()) / hops);

			} catch ( Exception e ) {

				System.err.println(e);
				System.err.println("drawobj(): Message-class: " + this.getClass());
				System.err.println("src: " + src + "    dst: " + dst);

			}

		} else
			// draw messages on sublayer
			super.drawobj(g);
	}

	/**
	 * the message will be sent to destination directly whithout any
	 * dispatching. Should only be used by a TransferMessageDispatcher.
	 * 
	 * @return the Message itself
	 */
	public Message sendWithoutDispatching() {
		return super.send();
	}

	public Node getDestination() {
		return destination;
	}

	public Node getOrigin() {
		return origin;
	}

}
