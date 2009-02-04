package de.zintel.diplom.util;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;

/**
 * 
 */

/**
 * @author Friedemann Zintel
 * 
 */
public abstract class ObserverJComponent extends JComponent implements Observer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

	public abstract void startObserve();

	public abstract void stopObserve();

}
