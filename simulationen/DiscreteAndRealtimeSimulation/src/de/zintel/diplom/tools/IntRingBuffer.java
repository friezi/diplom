package de.zintel.diplom.tools;
/**
 * 
 */

import java.lang.ArrayIndexOutOfBoundsException;

/**
 * 
 * A ringbuffer. Stores a fixed number of integers. If max number of elements
 * are exceeded, the oldest values will be overwritten.
 * 
 * @author Friedemann Zintel
 * 
 */
public class IntRingBuffer {

	protected int[] buffer = null;

	int elements = 0;

	int first = 0;

	int last = -1;

	/**
	 * 
	 */
	public IntRingBuffer(int size) {

		buffer = new int[size];

	}

	public void enqueue(int element) {

		if ( elements < buffer.length )
			elements++;

		else {

			if ( first == buffer.length - 1 )
				first = 0;

			else
				first++;
		}

		if ( last == buffer.length - 1 )
			last = 0;

		else
			last++;

		buffer[last] = element;

	}

	/**
	 * Return the value at the given index. Index-counting starts with 0.
	 * 
	 * @param index
	 * @return value at index
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public int get(int index) throws ArrayIndexOutOfBoundsException {

		if ( index >= elements || index < 0 )
			throw new ArrayIndexOutOfBoundsException();

		if ( (buffer.length - 1) - index >= first )
			return buffer[first + index];
		else
			return buffer[index - (buffer.length - first)];

	}

	public void skip(int number) throws ArrayIndexOutOfBoundsException {

		if ( number > elements || number < 0 )
			throw new ArrayIndexOutOfBoundsException();

		else if ( number == elements ) {

			first = 0;
			last = -1;

		} else {

			if ( (buffer.length - 1) - number >= first )
				first += number;

			else
				first = (number - (buffer.length - first));

		}

		elements -= number;

	}

	/**
	 * @return Returns the elements.
	 */
	public int getElements() {
		return elements;
	}

}
