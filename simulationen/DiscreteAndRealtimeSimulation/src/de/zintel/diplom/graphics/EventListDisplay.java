package de.zintel.diplom.graphics;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;

import de.zintel.diplom.event.Event;
import de.zintel.diplom.util.ObserverJComponent;

/**
 * For displaying Events contained in a list. If the list c
 * hanges, the component
 * will be notified and displays the new list.
 * 
 * @author Friedemann Zintel
 * 
 */
@SuppressWarnings("serial")
public class EventListDisplay extends ObserverJComponent {

	private Observable notifier;

	private int min_width = 145;

	private int min_height = 10;

	private class DOEvent extends DisplayableObject {

		/*
		 * (non-Javadoc)
		 * 
		 * @see DisplayableObject#size()
		 */
		@Override
		public int size() {
			return 5;
		}

	}

	/**
	 * 
	 * @param arg1
	 *            must be of type LinkedList &lt Event &gt
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 * 
	 */
	public void update(Observable arg0, Object arg1) {

		localevents = (LinkedList<Event>) arg1;
		repaint();

	}

	private LinkedList<Event> localevents;

	/**
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {

		DOEvent d_event;
		Event event;
		Iterator<Event> it = localevents.iterator();
		int x_min = 10;
		int x_maxevents = 10;
		int x_step = 10;
		int y_min = 5;
		int y_step = 10;
		int x = x_min;
		int y = y_min;

		while ( it.hasNext() ) {
			event = it.next();
			d_event = new DOEvent();
			event.represent(d_event);

			d_event.draw(g, x, y);

			if ( x >= x_maxevents * x_min ) {

				x = x_min;
				y += y_step;

			} else
				x += x_step;

		}

	}

	public EventListDisplay(Observable notifier) {

		setPreferredSize(new Dimension(min_width, min_height));
		this.notifier = notifier;

	}

	public void startObserve() {
		notifier.addObserver(this);
	}

	public void stopObserve() {
		notifier.deleteObserver(this);
	}

}
