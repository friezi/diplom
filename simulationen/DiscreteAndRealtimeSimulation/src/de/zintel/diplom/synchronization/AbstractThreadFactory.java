package de.zintel.diplom.synchronization;

import de.zintel.diplom.simulation.SimParameters;

/**
 * @author Friedemann Zintel
 *
 */
public class AbstractThreadFactory {

	SimParameters params;

	public AbstractThreadFactory(SimParameters params) {
		this.params = params;
	}

	public AbstractThread newThread(Runnable task) {

		if ( params.isDiscreteSimulation() == false )
			return new ExtendedThread(task);
		else
			return new VirtualThread(task);

	}

}
