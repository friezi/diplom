package de.zintel.diplom.synchronization;
import java.util.TimerTask;

import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.Message;
import de.zintel.diplom.simulation.SimParameters;

/**
 * 
 */

/**
 * This TimerTask can provide the nextExecutionTime;
 * 
 * @author Friedemann Zintel
 * 
 */
public class ExtendedTimerTask extends TimerTask {

	SimParameters params;

	private long delay = 0;

	private long period = 0;

	private long nextExecutionTime = 0;

	private Message message = null;

	private boolean canceled = false;

	/**
	 * 
	 */
	public ExtendedTimerTask(SimParameters params) {
		super();
		this.params = params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {

		// in case of discrete-simulation nextExecutionTime will be already defined by the VirtualTimer
		if ( params.isDiscreteSimulation() == false )
			setNextExecutionTime(Engine.getSingleton().getTime() + period);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#cancel()
	 */
	@Override
	public boolean cancel() {

		canceled = true;

		if ( params.isDiscreteSimulation() == true ) {

			if ( getMessage() != null ) {

				getMessage().recall();
				return true;

			} else
				return false;
		} else
			return super.cancel();
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

	/**
	 * @return Returns the message.
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            The message to set.
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	/**
	 * @return Returns the canceled.
	 */
	public synchronized boolean isCanceled() {
		return canceled;
	}

	/**
	 * @param canceled The canceled to set.
	 */
	public synchronized void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

}
