import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 */

/**
 * This class manages an ExtendedTimerTask.
 * 
 * @author friezi
 * 
 */
public class ExtendedTimer extends Timer {

	/**
	 * 
	 */
	public ExtendedTimer() {
		super();
	}

	/**
	 * @param isDaemon
	 */
	public ExtendedTimer(boolean isDaemon) {
		super(isDaemon);
	}

	/**
	 * @param name
	 */
	public ExtendedTimer(String name) {
		super(name);
	}

	/**
	 * @param name
	 * @param isDaemon
	 */
	public ExtendedTimer(String name, boolean isDaemon) {
		super(name, isDaemon);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Timer#schedule(java.util.TimerTask, long, long)
	 */
	public void schedule(ExtendedTimerTask task, long delay, long period) {

		task.setDelay(delay);
		task.setPeriod(period);
		task.setNextExecutionTime(System.currentTimeMillis() + delay);
		super.schedule(task, delay, period);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Timer#schedule(java.util.TimerTask, long)
	 */
	public void schedule(ExtendedTimerTask task, long delay) {

		task.setDelay(delay);
		task.setNextExecutionTime(System.currentTimeMillis() + delay);
		super.schedule(task, delay);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Timer#schedule(java.util.TimerTask, long, long)
	 */
	@Override
	public void schedule(TimerTask task, long delay, long period) throws IllegalArgumentException {
		throw new IllegalArgumentException("ExtendedTimer.schedule(TimerTask, long, long): NOT YET SUPPORTET!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Timer#schedule(java.util.TimerTask, long)
	 */
	@Override
	public void schedule(TimerTask task, long delay) throws IllegalArgumentException {
		throw new IllegalArgumentException("ExtendedTimer.schedule(TimerTask, long): NOT YET SUPPORTET!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Timer#schedule(java.util.TimerTask, java.util.Date, long)
	 */
	@Override
	public void schedule(TimerTask task, Date firstTime, long period) throws IllegalArgumentException {
		throw new IllegalArgumentException("ExtendedTimer.schedule(TimerTask, Date, long): NOT YET SUPPORTET!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Timer#schedule(java.util.TimerTask, java.util.Date)
	 */
	@Override
	public void schedule(TimerTask task, Date time) throws IllegalArgumentException {
		throw new IllegalArgumentException("ExtendedTimer.schedule(TimerTask): NOT YET SUPPORTET!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Timer#scheduleAtFixedRate(java.util.TimerTask,
	 *      java.util.Date, long)
	 */
	@Override
	public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) throws IllegalArgumentException {
		throw new IllegalArgumentException("ExtendedTimer.scheduleAtFixedRate(TimerTask, Date, long): NOT YET SUPPORTET!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Timer#scheduleAtFixedRate(java.util.TimerTask, long, long)
	 */
	@Override
	public void scheduleAtFixedRate(TimerTask task, long delay, long period) throws IllegalArgumentException {
		throw new IllegalArgumentException("ExtendedTimer.scheduleAtFixedRate(TimerTask, long, long): NOT YET SUPPORTET!");
	}

}
