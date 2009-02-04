package de.zintel.diplom.rps.server;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.Message;

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
public class VirtualUploader implements AbstractUploader {

	protected long nextIdleTime = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractUploader#upload(Message)
	 */
	public void upload(Message m) {

		m.recall();

		m.setArrivaltime(Engine.getSingleton().getTime() + getDeltaBusyTime() + getUploadTime(m));

		m.send();

		adjustNextIdleTime(m);

	}

	/**
	 * Calculates the time, for which the Uploader will be busy.
	 * 
	 * @return the delta-busy-time
	 */
	protected long getDeltaBusyTime() {

		if ( nextIdleTime <= Engine.getSingleton().getTime() )
			return 0;
		else
			return nextIdleTime - Engine.getSingleton().getTime();

	}

	/**
	 * Adjusts the time, at which the uploader will be idle again.
	 * 
	 * @param m
	 *            the message to be uploaded
	 */
	protected void adjustNextIdleTime(Message m) {

		if ( nextIdleTime < Engine.getSingleton().getTime() )
			nextIdleTime = Engine.getSingleton().getTime();

		nextIdleTime += getUploadTime(m);

	}

	/**
	 * Calculates the time a message needs to be uploaded.
	 * 
	 * @param m
	 *            the message to be uploaded
	 * @return upload-time
	 */
	public long getUploadTime(Message m) {
		return Engine.getSingleton().getTimerPeriod() * m.getRuntime();
	}

	public void incNextIdleTime(long n) {
		nextIdleTime += n;
	}

}
