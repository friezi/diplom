import java.util.TimerTask;

/**
 * 
 */

/**
 * This TimerTask can provide the nextExecutionTime;
 * 
 * @author friezi
 * 
 */
public class ExtendedTimerTask extends TimerTask {

	private long delay = 0;

	private long period = 0;

	private long nextExecutionTime = 0;

	/**
	 * 
	 */
	public ExtendedTimerTask() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		setNextExecutionTime(System.currentTimeMillis() + period);
	}

	/**
	 * @return Returns the delay.
	 */
	public synchronized long getDelay() {
		return delay;
	}

	/**
	 * @return Returns the period.
	 */
	public synchronized long getPeriod() {
		return period;
	}

	/**
	 * @param delay
	 *            The delay to set.
	 */
	public synchronized void setDelay(long delay) {
		this.delay = delay;
	}

	/**
	 * @param period
	 *            The period to set.
	 */
	public synchronized void setPeriod(long period) {
		this.period = period;
	}

	/**
	 * @return Returns the nextExecutionTime.
	 */
	public synchronized long getNextExecutionTime() {
		return nextExecutionTime;
	}

	/**
	 * @param nextExecutionTime
	 *            The nextExecutionTime to set.
	 */
	public synchronized void setNextExecutionTime(long nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime;
	}

}
