package de.zintel.diplom.messages;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.InternalMessage;
import de.zintel.diplom.simulation.Message;
import de.zintel.diplom.simulation.VirtualNode;

/**
 * 
 */

/**
 * With this type of message one can realize a message of periodic occurence for synchronization or clock-simulating purpose. It will handle itself and it
 * will generate new messages all kticks (=1000 ticks), aligned to kticks, as long as Engine.getSingleton().isContinuousTime() evaluates to true,
 * so only one initiating message is necessary. 
 * 
 * @author Friedemann Zintel
 *
 */
public class SyncBeatMessage extends InternalMessage {

	protected class Handler extends VirtualNode {

		@Override
		protected void receiveMessage(Message m) {

			if ( Engine.getSingleton().isContinuousTime() ) {

				SyncBeatMessage sbm = new SyncBeatMessage();
				sbm.send();

			}

		}

	}

	protected Handler handler = new Handler();

	public SyncBeatMessage() {

		super(null, null, 0);

		src = handler;
		dst = handler;

		setArrivaltime(calculateArrivalTime());

	}

	protected long calculateArrivalTime() {

		int kticks = (int) (Engine.getSingleton().getTime() / 1000);
		return (1000 * (kticks + 1));

	}

}
