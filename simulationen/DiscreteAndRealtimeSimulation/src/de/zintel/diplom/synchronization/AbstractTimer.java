package de.zintel.diplom.synchronization;
/**
 * 
 */

/**
 * @author Friedemann Zintel
 * 
 */
public interface AbstractTimer {

	public void schedule(ExtendedTimerTask task, long delay);
	
	public int purge();
	
	public void cancel();

}
