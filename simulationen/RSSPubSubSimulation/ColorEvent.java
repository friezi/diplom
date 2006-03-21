/**
 * 
 */

import java.awt.Color;

/**
 * @author Friedemann Zintel
 * 
 * An Event-dervied class, which just stores an identifier and a color
 * 
 */
public class ColorEvent extends Event implements Cloneable{

	int id;

	Color color;

	public ColorEvent(int id, Color color) {

		super();
		this.id = id;
		this.color = color;

	}

	@Override
	public Object clone() throws CloneNotSupportedException {

		return super.clone();
	}

}
