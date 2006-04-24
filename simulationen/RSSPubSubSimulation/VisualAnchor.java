import java.awt.Color;

import rsspubsubframework.*;

import javax.swing.*;

/**
 * 
 */

/**
 * It will display the connection between a window and the corresponding node.
 * 
 * @author Friedemann Zintel
 *
 */
public class VisualAnchor {

	int nhradius = 2;

	Node node;

	GOFilledCircle hook;

	GOHyperLine rope;

	JFrame window;

	public VisualAnchor(Node node, JFrame window) {

		this.window = window;

		this.node = node;

		throwAnchor();

	}

	private void throwAnchor() {

		Color anchorcolor = Color.red;

		// draw a dot (the hook) on the node and a line (the rope) to the
		// window with a dot (windowhook)
		hook = new GOFilledCircle(node.getX(), node.getY(), nhradius);
		rope = new GOHyperLine(node.getX(), node.getY(), window.getX(), window.getY());

		hook.setColor(anchorcolor);
		rope.setColor(anchorcolor);

		hook.display();
		rope.display();

	}

	public void adjustAnchor() {

		rope.setX2(window.getX());
		rope.setY2(window.getY());

	}

	public void undisplayAnchor() {

		hook.undisplay();
		rope.undisplay();

	}

}
