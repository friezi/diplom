package de.zintel.diplom.util;
/**
 * 
 */

import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.tools.AbstractProgressBar;

/**
 * Gives access to the progressbar of the Engine.
 * 
 * @author Friedemann Zintel
 * 
 */
public class ProgressBarAccessor implements AbstractProgressBar{

	private AbstractProgressBar progressbar = null;

	public ProgressBarAccessor() {

		progressbar = Engine.getSingleton().getProgressBar();

	}

	public void setMaximum(int value) {
		if ( progressbar != null )
			progressbar.setMaximum(value);
	}

	public void setMinimum(int value) {
		if ( progressbar != null )
			progressbar.setMinimum(value);
	}

	public void setValue(int value) {
		if ( progressbar != null )
			progressbar.setValue(value);
	}

	public void setStringPainted(boolean value) {
		if ( progressbar != null )
			progressbar.setStringPainted(value);
	}

}
