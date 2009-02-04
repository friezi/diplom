package de.zintel.diplom.event;

/**
 * 
 */
import de.zintel.diplom.graphics.DisplayableObject;

/**
 * Just a base-class for events.
 *
 * @author Friedemann Zintel
 * 
 */
public interface Event extends Cloneable{
	
	public boolean equals(Event event);
	
	public EventGeneralContent getGeneralContend();
	
	public void represent(DisplayableObject dObj);
		

}
