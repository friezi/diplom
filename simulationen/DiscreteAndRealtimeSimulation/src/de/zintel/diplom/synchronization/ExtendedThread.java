package de.zintel.diplom.synchronization;
/**
 * 
 */

/**
 * Just to make a new standard-thread with the AbstractThreadFactory.
 * 
 * @author Friedemann Zintel
 *
 */
public class ExtendedThread extends Thread implements AbstractThread{

	/**
	 * @param arg0
	 */
	public ExtendedThread(Runnable arg0) {
		super(arg0);
	}


}
