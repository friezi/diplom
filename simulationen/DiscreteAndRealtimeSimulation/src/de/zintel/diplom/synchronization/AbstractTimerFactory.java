package de.zintel.diplom.synchronization;

import de.zintel.diplom.simulation.SimParameters;

/**
 * @author Friedemann Zintel
 * 
 */
public class AbstractTimerFactory {

	SimParameters params;

	public AbstractTimerFactory(SimParameters params) {
		this.params = params;
	}

	public AbstractTimer newTimer() {

		if ( params.isDiscreteSimulation() == false )
			return new ExtendedTimer();
		else
			return new VirtualTimer();
	}

}
