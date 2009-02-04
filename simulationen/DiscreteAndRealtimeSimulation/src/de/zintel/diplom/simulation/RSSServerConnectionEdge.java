package de.zintel.diplom.simulation;
import java.awt.Color;


/**
 * 
 */

/**
 * For displaying connection to the rss-server.
 * 
 * @author Friedemann Zintel
 * 
 */
public class RSSServerConnectionEdge extends Edge {

	public static final Color DFLT_COLOR = Color.orange;

	/**
	 * @param ap
	 * @param bp
	 */
	public RSSServerConnectionEdge(Node ap, Node bp) {
		count++;
		id = count;
		a = ap;
		b = bp;

		setColor(DFLT_COLOR);

	}

	@Override
	public RSSServerConnectionEdge register() {
		Engine.getSingleton().addRSSServerConnectionEdge(this);
		return this;
	}

}
