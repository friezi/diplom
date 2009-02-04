package de.zintel.diplom.util;

import de.zintel.diplom.tools.AbstractProgressBar;
/**
 * 
 */

/**
 * A progressbar for a normal console.
 * 
 * @author Friedemann Zintel
 * 
 */
public class ConsoleProgressBar implements AbstractProgressBar {

	protected boolean isActive = false;

	protected static final int MAX_VALUE = 100;

	protected float factor = 1;

	protected int maximum = 0;

	protected int minimum = 0;

	protected int value = 0;

	/**
	 * 
	 */
	public ConsoleProgressBar() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractProgressBar#setMaximum(int)
	 */
	public void setMaximum(int value) {

		if ( value <= MAX_VALUE ) {

			this.maximum = value;
			factor = 1;

		} else {

			this.maximum = MAX_VALUE;
			factor = value / maximum;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractProgressBar#setMinimum(int)
	 */
	public void setMinimum(int value) {
		this.minimum = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractProgressBar#setValue(int)
	 */
	public void setValue(int value) {

		if ( value == 0 ) {

			isActive = false;
			this.value = 0;
			System.out.println();

		} else {

			if ( isActive == false ) {

				isActive = true;

				for ( int i = 0; i < maximum + 1; i++ )
					System.out.print(' ');

				System.out.print("|");
				System.out.print('\r');
				System.out.print('|');

			}

			if ( (int) (value / factor) > this.value )
				for ( int i = 0; i < (int) (value / factor - this.value); i++ )
					System.out.print('.');

			if ( (int) (value / factor) > this.value )
				this.value = (int) (value / factor);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractProgressBar#setStringPainted(boolean)
	 */
	public void setStringPainted(boolean value) {
	}

}
