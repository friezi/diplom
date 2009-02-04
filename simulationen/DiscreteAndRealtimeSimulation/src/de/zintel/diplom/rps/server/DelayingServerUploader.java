package de.zintel.diplom.rps.server;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.Message;

/**
 * 
 */

/**
 * An Uploader used by a RSSServer in a realtime-simulation.
 * 
 * @author Friedemann Zintel
 * 
 */
public class DelayingServerUploader implements AbstractUploader {

	RSSServerNode server;

	DelayingServerUploader(RSSServerNode server) {
		this.server = server;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractUploader#upload(Message)
	 */
	public void upload(Message m) {
		try {
			Thread.sleep((int) (Engine.getSingleton().getTimerPeriod() * m.getRuntime() * server.getServiceTimeFactor()));
		} catch ( Exception e ) {
			System.out.println("Exception: " + e);
		}
	}

}
