import java.awt.Color;

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

	Color anchorcolor = Color.red;

	GOFilledCircle hook = null;

	GOHyperLine rope = null;

	JFrame window;

	public VisualAnchor(Node node, JFrame window) {

		this.window = window;

		this.node = node;

		throwAnchor();

	}

	private void throwAnchor() {

		// draw a dot (the hook) on the node and a line (the rope) to the
		// window with a dot (windowhook)
		rope = newRope();
		hook = newHook();

	}

	protected GOHyperLine newRope() {

		rope = new GOHyperLine(node.getX(), node.getY(), window.getX(), window.getY());
		rope.setColor(anchorcolor);
		rope.display();

		return rope;

	}

	protected GOFilledCircle newHook() {

		hook = new GOFilledCircle(node.getX(), node.getY(), nhradius);
		hook.setColor(anchorcolor);
		hook.display();

		return hook;

	}

	public void adjustAnchor() {

		if ( rope != null ) {
			rope.setX2(window.getX());
			rope.setY2(window.getY());
		}

	}

	public void undisplayAnchor() {

		hook.undisplay();
		if ( rope != null )
			rope.undisplay();

	}

	public void loseAnchor() {
		rope.undisplay();
		rope = null;
	}

	public void grabAnchor() {

		if ( rope != null )
			rope.undisplay();
		rope = newRope();
	}

}
