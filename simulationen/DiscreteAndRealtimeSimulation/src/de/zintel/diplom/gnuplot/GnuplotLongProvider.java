package de.zintel.diplom.gnuplot;

import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.SimParameters;

/**
 * @author Friedemann Zintel
 * 
 */
public class GnuplotLongProvider extends AbstractGnuplotDataProvider {

	private long sum = 0;

	/**
	 * @param params
	 * @throws Exception
	 */
	public GnuplotLongProvider(SimParameters params) throws Exception {
		super(params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractGnuplotDataProvider#incData(java.lang.Object)
	 */
	@Override
	protected void incData(Object value) {
		sum += (Long) value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractGnuplotDataProvider#notifyObservers()
	 */
	@Override
	protected synchronized void notifyObservers() {
		dataNotifier.notifyObservers(new GnuplotData(Engine.getSingleton().getTime() / 1000, sum / getEventcounter()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractGnuplotDataProvider#resetData()
	 */
	@Override
	protected void resetData() {
		sum = 0;
	}

}
