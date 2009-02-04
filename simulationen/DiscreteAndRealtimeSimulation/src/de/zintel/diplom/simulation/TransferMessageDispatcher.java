package de.zintel.diplom.simulation;
import java.util.*;

import de.zintel.diplom.messages.TMDispatcherMessage;

/**
 * 
 */

/**
 * This Class is only used for discrete simulation. The
 * TranssferMessageDispatcher handles TransferMessages and empowers the Engine to display them on
 * their way to the destination.
 * 
 * @author Friedemann Zintel
 * 
 */
public class TransferMessageDispatcher extends VirtualNode {

	/**
	 * 
	 */
	public TransferMessageDispatcher() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Node#receiveMessage(Message)
	 */
	@Override
	protected void receiveMessage(Message m) {

		if ( m instanceof TMDispatcherMessage ) {

			TMDispatcherMessage tmdm = (TMDispatcherMessage) m;

			if ( tmdm.message.pos >= tmdm.message.getRuntime() || Engine.getSingleton().isShowMessages() == false
					|| Engine.getSingleton().isVisualization() == false ) {

				Set<Message> messageList = Engine.getSingleton().getMessageList();
				synchronized ( messageList ) {
//
//					System.err.println("");
//					for ( Message message : messageList )
//						System.err.println(message);

					if ( messageList.remove(tmdm.message) == false ) {

						System.err.println("WARNING: TransferMessageDispatcher: could not remove message:" + tmdm.message + " from:"
								+ tmdm.message.src.getClass() + " to:" + tmdm.message.dst.getClass());

//						for ( Message message : messageList )
//							System.err.println(message);
//						System.exit(1);

					} else
						/*System.err.println("removed " + tmdm.message)*/;
				}

				tmdm.message.sendWithoutDispatching();

				if ( tmdm.message.pos > tmdm.message.getRuntime() )
					System.err.println("WARNING: messages' pos is greater than messages' runtime!!!");

			} else {

				tmdm.message.pos++;

				tmdm.setArrivaltime(Engine.getSingleton().getTime() + Engine.getSingleton().getTimerPeriod());
				tmdm.send();

			}

		} else {
			System.err.println("WARNING: Wrong message-type for TransferMessageDispatcher: " + m.getClass());
		}

	}
	
	/**
	 * Recalls a message, if not yet delivered.
	 * 
	 * @param m the message
	 */
	public void recallMessage(Message m) {

		Engine.getSingleton().removeTMDispatcherMessage(m);
		Set<Message> messageList = Engine.getSingleton().getMessageList();

		synchronized ( messageList ) {
			messageList.remove(m);
		}

	}

}
