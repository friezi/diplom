/**
 * 
 */

import java.awt.Color;

/**
 * An Event-dervied class, which just stores an identifier and a color
 * 
 * @author Friedemann Zintel
 * 
 */
public class ColorEvent implements Event {

	int id;

	Color color;

	public ColorEvent(int id, Color color) {

		super();
		this.id = id;
		this.color = color;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Event#equals(Event)
	 */
	public boolean equals(Event event) {
		// TODO Auto-generated method stub
		return this.id == ((ColorEvent) event).id;
	}
	
	public boolean equals(Object event){
		return equals((Event)event);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {

		return super.clone();
	}

}
