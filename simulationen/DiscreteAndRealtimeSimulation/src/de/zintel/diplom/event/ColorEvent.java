package de.zintel.diplom.event;
/**
 * 
 */

import java.awt.Color;

import de.zintel.diplom.graphics.DisplayableObject;

/**
 * An Event-dervied class, which just stores an identifier and a color
 * 
 * @author Friedemann Zintel
 * 
 */
public class ColorEvent implements Event {

	public int id;

	public Color color;

	EventGeneralContent generalContent;

	public ColorEvent(int id, Color color, EventGeneralContent generalContent) {

		super();
		this.id = id;
		this.color = color;
		this.generalContent = generalContent;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Event#equals(Event)
	 */
	public boolean equals(Event event) {
		return this.id == ((ColorEvent) event).id;
	}

	public boolean equals(Object event) {
		return equals((Event) event);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {

		return super.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Event#getGeneralContend()
	 */
	public EventGeneralContent getGeneralContend() {
		return generalContent;
	}

	public void represent(DisplayableObject dObj) {
		dObj.setColor(color);
	}

}
