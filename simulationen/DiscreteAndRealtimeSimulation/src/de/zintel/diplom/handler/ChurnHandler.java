package de.zintel.diplom.handler;
import java.util.*;

import de.zintel.diplom.rps.broker.BrokerType;
import de.zintel.diplom.rps.pubsub.PubSubType;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.InternalMessage;
import de.zintel.diplom.simulation.Message;
import de.zintel.diplom.simulation.VirtualNode;

/**
 * 
 */

/**
 * Controls churning of PubSubs. Can be controlled via the ActionHandler.
 * 
 * @author Friedemann Zintel
 * 
 */
public class ChurnHandler extends VirtualNode {

	protected class NextChurnMessage extends InternalMessage {

		int sequencenmb;

		NextChurnMessage(long arrivalTime, int sequencenmb) {

			super(churnHandler, churnHandler, arrivalTime);
			this.sequencenmb = sequencenmb;

		}

	}

	protected ChurnHandler churnHandler = this;

	protected boolean isActive = false;

	protected int percent;

	protected long timerange;

	protected long interChurningTime = 0;

	protected int sequencenmb = 0;

	protected LinkedList<PubSubType> churningPubSubs = new LinkedList<PubSubType>();

	/**
	 * 
	 */
	public ChurnHandler() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Node#receiveMessage(Message)
	 */
	@Override
	protected void receiveMessage(Message m) {

		if ( m instanceof ActionHandler.StartChurnMessage ) {

			ActionHandler.StartChurnMessage scm = (ActionHandler.StartChurnMessage) m;
			percent = scm.percent;
			timerange = scm.timerange;
			isActive = true;
			sequencenmb++;

			startChurn();

		} else if ( m instanceof ActionHandler.StopChurnMessage ) {

			isActive = false;

		} else if ( m instanceof NextChurnMessage ) {

			NextChurnMessage ncm = (NextChurnMessage) m;

			// if there are still messages from a former setting the
			// sequencenumber will prevent confusion
			if ( isActive == true && ncm.sequencenmb == sequencenmb )
				nextChurn();

		}

	}

	protected void startChurn() {

		churningPubSubs = new LinkedList<PubSubType>();

		long numberOfChurns = (percent * Engine.getPubsubindizes().size()) / 100;
		interChurningTime = (timerange * 1000) / numberOfChurns;
		LinkedList<PubSubType> pubsubs = new LinkedList<PubSubType>(Engine.getPubsubindizes().keySet());

		for ( long i = 0; i < numberOfChurns; i++ ) {

			int index = (int) (Engine.getSingleton().getRandom().nextFloat() * pubsubs.size());
			churningPubSubs.add(pubsubs.remove(index));

		}

		new NextChurnMessage(Engine.getSingleton().getTime() + interChurningTime, sequencenmb).send();

	}

	protected void nextChurn() {

		if ( churningPubSubs.isEmpty() == false ) {

			PubSubType pubsub = churningPubSubs.remove();

			renewPubsub(pubsub);

		}

		if ( churningPubSubs.isEmpty() == true ) {

			startChurn();

		} else {

			new NextChurnMessage(Engine.getSingleton().getTime() + interChurningTime, sequencenmb).send();

		}

	}

	protected void renewPubsub(PubSubType pubsub) {

		if ( pubsub.isRegistering() == false ) {

			if ( pubsub.getNumberOfBrokers() > 0 ) {

				LinkedList<BrokerType> brokers = new LinkedList<BrokerType>(Engine.getBrokers());

				if ( brokers.size() > 1 ) {

					Random random = Engine.getSingleton().getRandom();

					// chose a random new broker for pubsub
					BrokerType newbroker = brokers.get((int) (random.nextFloat() * brokers.size()));

					pubsub.renew();
					pubsub.callbackRegisterAtBroker(newbroker);
					pubsub.start();

				}

			}

		}

	}

}
