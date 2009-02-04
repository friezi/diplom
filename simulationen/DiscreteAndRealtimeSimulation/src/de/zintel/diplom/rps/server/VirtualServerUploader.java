package de.zintel.diplom.rps.server;
import java.util.LinkedList;

import de.zintel.diplom.messages.RSSFeedMessage;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.Message;
import de.zintel.diplom.simulation.SimParameters;

/**
 * 
 */

/**
 * 
 * Used for discrete event-simulation only.
 * 
 * @author Friedemann Zintel
 * 
 */
public class VirtualServerUploader extends VirtualUploader {

	protected SimParameters params;

	protected RSSServerNode server;

	public VirtualServerUploader(RSSServerNode server, SimParameters params) {

		this.params = params;
		this.server = server;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see VirtualUploader#upload(Message)
	 */
	@Override
	public void upload(Message m) {

		m.recall();

		new QueueingRSSServer.DispatchQueueMessage(server, server, (RSSFeedMessage) m, Engine.getSingleton().getTime() + getDeltaBusyTime()).send();

		adjustNextIdleTime(m);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see VirtualUploader#adjustNextIdleTime(Message)
	 */
	@Override
	protected void adjustNextIdleTime(Message m) {

		if ( nextIdleTime < Engine.getSingleton().getTime() )
			nextIdleTime = Engine.getSingleton().getTime();

		nextIdleTime += (int) (getUploadTime(m) * server.getServiceTimeFactor());
	}

	/**
	 * In case of a change of the serviceTimeFactor, all the
	 * DispatchQueueMessages must be rescheduled.
	 * 
	 * @param newServiceTimeFactor
	 *            the new value
	 */
	protected void rescheduleCurrentUploads(float newServiceTimeFactor, long processTimeUnrepliedRequests) {

		LinkedList<QueueingRSSServer.DispatchQueueMessage> list = new LinkedList<QueueingRSSServer.DispatchQueueMessage>();

		long time = Engine.getSingleton().getTime();
		long deltaArrivalTime = 0;
		long newDeltaArrivalTime = 0;
		float ratio = newServiceTimeFactor / server.getServiceTimeFactor();
		long newNextIdleTime = time;
		long deltaOldNextIdleTime = 0;

		synchronized ( Engine.getSingleton().messagequeue ) {

			for ( Message message : Engine.getSingleton().messagequeue )
				if ( message instanceof QueueingRSSServer.DispatchQueueMessage )
					list.add((QueueingRSSServer.DispatchQueueMessage) message);

			for ( QueueingRSSServer.DispatchQueueMessage message : list ) {

				message.recall();
				/*
				 * deltaArrivalTime = message.getArrivaltime() - time +
				 * deltaOldNextIdleTime; // in case there were some unreplied
				 * requests if ( deltaArrivalTime > getUploadTime(message) *
				 * server.getServiceTimeFactor() ) {
				 * 
				 * long deltaUnrepliedProcessingTime = (long) (deltaArrivalTime -
				 * getUploadTime(message) * server.getServiceTimeFactor());
				 * deltaArrivalTime -= deltaUnrepliedProcessingTime;
				 * deltaOldNextIdleTime += deltaUnrepliedProcessingTime;
				 * newNextIdleTime += deltaUnrepliedProcessingTime; }
				 * 
				 * deltaOldNextIdleTime += deltaArrivalTime; newDeltaArrivalTime =
				 * (long) (deltaArrivalTime * ratio);
				 * 
				 * newNextIdleTime += newDeltaArrivalTime;
				 * message.setArrivaltime(newNextIdleTime);
				 *///
				message.setArrivaltime((long) (time + (message.getArrivaltime() - time) * ratio));
				nextIdleTime = message.getArrivaltime();

				message.send();

			}

			// nextIdleTime = newNextIdleTime;

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see VirtualUploader#getUploadTime(Message)
	 */
	@Override
	public long getUploadTime(Message m) {
		return params.getProcessingTimeFeedRequest();
	}

}
