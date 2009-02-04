package de.zintel.diplom.synchronization;
/**
 * 
 */

import java.util.*;

import de.zintel.diplom.simulation.Engine;

/**
 * @author Friedemann Zintel
 * 
 */
public class VirtualTimer implements AbstractTimer {

	LinkedList<ExtendedTimerTask> tasklist = new LinkedList<ExtendedTimerTask>();

	//
	// /* (non-Javadoc)
	// * @see AbstractTimer#schedule(ExtendedTimerTask, long, long)
	// */
	// public void schedule(ExtendedTimerTask task, long delay, long period) {
	//
	// task.setDelay(delay);
	// task.setPeriod(period);
	// task.setNextExecutionTime(Engine.getSingleton().getTime() + delay);
	// task.run();
	//
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractTimer#schedule(ExtendedTimerTask, long)
	 */
	public void schedule(ExtendedTimerTask task, long delay) {

		tasklist.add(task);
		task.setDelay(delay);
		if ( Long.MAX_VALUE - delay < Engine.getSingleton().getTime() )
			task.setNextExecutionTime(Long.MAX_VALUE);
		else
			task.setNextExecutionTime(Engine.getSingleton().getTime() + delay);
		task.run();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractTimer#purge()
	 */
	public int purge() {

		int count = 0;

		LinkedList<ExtendedTimerTask> templist = new LinkedList<ExtendedTimerTask>(tasklist);

		for ( ExtendedTimerTask task : templist ) {
			if ( task.isCanceled() == true ) {

				tasklist.remove(task);
				count++;

			} else if ( task.getNextExecutionTime() < Engine.getSingleton().getTime() ) {

				task.setCanceled(true);
				tasklist.remove(task);
				count++;

			}
		}

		templist.clear();

		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractTimer#cancel()
	 */
	public void cancel() {

		for ( ExtendedTimerTask task : tasklist )
			task.cancel();

		tasklist.clear();

	}

}
