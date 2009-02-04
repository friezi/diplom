package de.zintel.diplom.gnuplot;

import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.SimParameters;

/**
 * @author Friedemann Zintel
 * 
 */
public class GnuplotFloatProvider extends AbstractGnuplotDataProvider {

	private float floatsum = 0;

	/**
	 * @param params
	 * @throws Exception
	 */
	public GnuplotFloatProvider(SimParameters params) throws Exception {
		super(params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractGnuplotDataProvider#incData(java.lang.Object)
	 */
	@Override
	protected void incData(Object value) {
		floatsum += (Float) value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractGnuplotDataProvider#notifyObservers()
	 */
	@Override
	protected synchronized void notifyObservers() {
		dataNotifier.notifyObservers(new GnuplotData(Engine.getSingleton().getTime() / 1000, floatsum / getEventcounter()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractGnuplotDataProvider#resetData()
	 */
	@Override
	protected void resetData() {
		floatsum = 0;
	}

}
