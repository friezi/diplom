package de.zintel.diplom.gui;
/**
 * 
 */

import javax.swing.*;

import de.zintel.diplom.tools.AbstractProgressBar;

/**
 * A JProgressBar-implementation.
 * 
 * @author Friedemann Zintel
 * 
 */
public class GUIProgressBar implements AbstractProgressBar {

	protected JProgressBar progressbar = null;

	/**
	 * 
	 */
	public GUIProgressBar(JProgressBar progressbar) {
		super();
		this.progressbar = progressbar;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractProgressBar#setMaximum(int)
	 */
	public void setMaximum(int value) {
		progressbar.setMaximum(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractProgressBar#setMinimum(int)
	 */
	public void setMinimum(int value) {
		progressbar.setMinimum(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractProgressBar#setValue(int)
	 */
	public void setValue(int value) {
		progressbar.setValue(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractProgressBar#setStringPainted(boolean)
	 */
	public void setStringPainted(boolean value) {
		progressbar.setStringPainted(value);
	}

}
