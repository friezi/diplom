package de.zintel.diplom.tools;
/**
 * 
 */

/**
 * An abstraction of a ProgressBar with the most needed functions.
 * 
 * @author Friedemann Zintel
 * 
 */
public interface AbstractProgressBar {

	public void setMaximum(int value);

	public void setMinimum(int value);

	public void setValue(int value);

	public void setStringPainted(boolean value);

}
