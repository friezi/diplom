package de.zintel.diplom.rps.server;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.Message;

/**
 * 
 */

/**
 * Used for Realtime-simulation only
 * 
 * @author Friedemann Zintel
 * 
 */
public class DelayingUploader implements AbstractUploader {

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractUploader#upload(Message)
	 */

	/**
	 * It simulates an upload of a message: it just waits the amount of
	 * time the message will take to arrive
	 * 
	 * @param m
	 *            the message
	 */
	public void upload(Message m) {
		try {
			Thread.sleep(Engine.getSingleton().getTimerPeriod() * m.getRuntime());
		} catch ( Exception e ) {
			System.out.println("Exception: " + e);
		}
	}

}
