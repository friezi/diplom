package de.zintel.diplom.graphics;
import java.awt.Color;

import de.zintel.diplom.rps.broker.BrokerNode;
import de.zintel.diplom.simulation.Edge;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.Node;

/**
 * 
 */

/**
 * For displaying the overlay-network
 * 
 * @author Friedemann Zintel
 * 
 */
public class OverlayEdge extends Edge {

	public OverlayEdge(Node ap, Node bp) {
		count++;
		id = count;
		a = ap;
		b = bp;

		if ( ap instanceof BrokerNode && bp instanceof BrokerNode )
			setColor(BrokerNode.DFLT_COLOR);
		else
			setColor(Color.blue);

	}

	@Override
	public OverlayEdge register() {
		Engine.getSingleton().addOverlayEdge(this);
		return this;
	}

}
