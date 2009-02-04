package de.zintel.diplom.handler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

import de.zintel.diplom.messages.RegisterSubscriberMessage;
import de.zintel.diplom.messages.TMDispatcherMessage;
import de.zintel.diplom.rps.broker.BrokerType;
import de.zintel.diplom.rps.pubsub.PubSubType;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.InternalMessage;
import de.zintel.diplom.simulation.Message;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.TransferMessage;
import de.zintel.diplom.simulation.VirtualNode;

/**
 * 
 */

/**
 * Handles time-distributed joining of subscribers to the network
 * 
 * @author Friedemann Zintel
 * 
 */
public class JoinHandler extends VirtualNode {

	protected class ConnectAndInitSubscriberMessage extends InternalMessage {

		PubSubType pubsub;

		HashSet<BrokerType> brokers;

		ConnectAndInitSubscriberMessage(Node dst, long arrivalTime, PubSubType pubsub, HashSet<BrokerType> brokers) {

			super(joinHandler, dst, arrivalTime);
			this.pubsub = pubsub;
			this.brokers = brokers;

		}

	}

	protected JoinHandler joinHandler = this;

	protected HashMap<PubSubType, HashSet<BrokerType>> assignedPubSubsBrokers = new HashMap<PubSubType, HashSet<BrokerType>>();

	/**
	 * 
	 */
	public JoinHandler() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Node#receiveMessage(Message)
	 */
	@Override
	protected void receiveMessage(Message m) {

		if ( m instanceof ActionHandler.RetreatSubscribersMessage ) {

			withdrawAndDelaySubscribers((ActionHandler.RetreatSubscribersMessage) m);

		} else if ( m instanceof ConnectAndInitSubscriberMessage ) {

			joinSubscriber((ConnectAndInitSubscriberMessage) m);

		}

	}

	/**
	 * Removes all sent messages by subscribers and resets the subscribers. It
	 * will then join the subscribers to the overlay-network at a later time.
	 * 
	 * @param rsm
	 */
	protected void withdrawAndDelaySubscribers(ActionHandler.RetreatSubscribersMessage rsm) {

		TreeSet<Message> messagequeue;
		LinkedList<PubSubType> pubsubs = new LinkedList<PubSubType>(Engine.getPubsubindizes().keySet());
		HashSet<BrokerType> assignedBrokers;
		long timerange = rsm.stoptime - rsm.starttime + 1;
		long newtime;

		for ( PubSubType pubsub : pubsubs ) {

			messagequeue = new TreeSet<Message>(Engine.getSingleton().messagequeue);
			assignedBrokers = new HashSet<BrokerType>();
			assignedPubSubsBrokers.put(pubsub, assignedBrokers);

			for ( Message message : messagequeue ) {

				if ( message instanceof TMDispatcherMessage ) {

					if ( ((TMDispatcherMessage) message).message.getOrigin() == pubsub ) {

						if ( ((TMDispatcherMessage) message).message instanceof RegisterSubscriberMessage )
							assignedBrokers.add((BrokerType) (((TMDispatcherMessage) message).message).getDestination());

						Engine.getSingleton().messagequeue.remove(message);

					}

				} else {

					if ( message instanceof InternalMessage ) {

						if ( message.getSrc() == pubsub ) {

							if ( message instanceof RegisterSubscriberMessage )
								assignedBrokers.add((BrokerType) (message.getDst()));

							Engine.getSingleton().messagequeue.remove(message);

						}

					} else if ( message instanceof TransferMessage ) {

						if ( ((TransferMessage) message).getOrigin() == pubsub ) {

							if ( message instanceof RegisterSubscriberMessage )
								assignedBrokers.add((BrokerType) (((TransferMessage) message).getDestination()));

							Engine.getSingleton().messagequeue.remove(message);

						}

					}

				}

			}

			pubsub.reset();

			newtime = rsm.starttime + (long) (Engine.getSingleton().getRandom().nextFloat() * (timerange + 1));

			new ConnectAndInitSubscriberMessage(joinHandler, newtime, pubsub, assignedBrokers).send();

		}

	}

	/**
	 * Joins a subscriber to the overlay-network.
	 * 
	 * @param cism
	 */
	protected void joinSubscriber(ConnectAndInitSubscriberMessage cism) {

		for ( BrokerType broker : cism.brokers )
			cism.pubsub.callbackRegisterAtBroker(broker);
		cism.pubsub.init();
		cism.pubsub.start();

	}

}
