package de.zintel.diplom.gnuplot;
import java.util.Observable;
import java.util.Observer;

import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.InternalMessage;
import de.zintel.diplom.simulation.Message;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.simulation.VirtualNode;

/**
 * 
 */

/**
 * @author Friedemann Zintel
 * 
 */
public abstract class AbstractGnuplotDataProvider extends VirtualNode implements Observer {

	/**
	 * @author Friedemann Zintel
	 * 
	 */
	public static class GnuplotData {

		public long x;

		public float y;

		/**
		 * @param x
		 * @param y
		 */
		public GnuplotData(long x, float y) {

			this.x = x;
			this.y = y;

		}

	}

	/**
	 * @author Friedemann Zintel
	 * 
	 */
	protected class DataNotifier extends Observable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observable#notifyObservers(java.lang.Object)
		 */
		@Override
		public void notifyObservers(Object object) {

			setChanged();
			super.notifyObservers(object);

		}

	}

	protected class ProvideGnuplotDataMessage extends InternalMessage {

		ProvideGnuplotDataMessage(long arrivalTime) {
			super(gnuplotDataProvider, gnuplotDataProvider, arrivalTime);
		}

	}

	protected SimParameters params;

	private long eventcounter = 1;

	protected boolean active = false;

	protected boolean notified = true;

	protected Node gnuplotDataProvider = this;

	protected DataNotifier dataNotifier = new DataNotifier();

	public AbstractGnuplotDataProvider(SimParameters params) throws Exception {

		this.params = params;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {

		active = false;
		super.finalize();

	}

	public synchronized void update(Observable arg0, Object value) {

		if ( notified == true ) {

			resetData();
			setEventcounter(0);
			notified = false;

		}

		incData(value);
		setEventcounter(getEventcounter() + 1);

	}

	@Override
	protected void receiveMessage(Message m) {

		if ( m instanceof ProvideGnuplotDataMessage ) {

			if ( active == true ) {

				this.notifyObservers();
				notified = true;
				new ProvideGnuplotDataMessage(Engine.getSingleton().getTime() + params.getGnuplotTimeStepSecs() * 1000).send();

			}

		} else {
			System.err.println("AbstractGnuplotDataProvider: warning! got unexpected messagetype: " + m.getClass().getName());
		}

	}

	protected abstract void notifyObservers();

	protected abstract void resetData();

	protected abstract void incData(Object value);

	public void addObserver(Observer observer) {

		dataNotifier.addObserver(observer);

		if ( dataNotifier.countObservers() == 1 ) {

			active = true;

			new ProvideGnuplotDataMessage(Engine.getSingleton().getTime() + params.getGnuplotTimeStepSecs() * 1000).send();

		}

	}

	public void deleteObserver(Observer observer) {

		dataNotifier.deleteObserver(observer);

		if ( dataNotifier.countObservers() == 0 )
			active = false;

	}

	public int countObservers() {
		return dataNotifier.countObservers();
	}

	/**
	 * @param eventcounter
	 *            The eventcounter to set.
	 */
	private synchronized void setEventcounter(long eventcounter) {
		this.eventcounter = eventcounter;
	}

	/**
	 * Eventcounter represents the occured events since the last write.
	 * 
	 * @return Returns the eventcounter.
	 */
	protected synchronized long getEventcounter() {
		return eventcounter;
	}

}
