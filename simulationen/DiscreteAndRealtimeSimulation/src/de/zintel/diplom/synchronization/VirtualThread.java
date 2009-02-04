package de.zintel.diplom.synchronization;
/**
 * 
 */

/**
 * This is not a real thread. It pretends thread-behaviour in cases where no
 * threads should be invoked. Very important for the discrete event-simulation.
 * 
 * @author Friedemann Zintel
 * 
 */
public class VirtualThread implements AbstractThread {

	Runnable task;

	VirtualThread(Runnable task) {
		this.task = task;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractThread#start()
	 */
	public void start() {
		task.run();
	}

}
