import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import rsspubsubframework.*;

/**
 * This Window for showing information about a Node. Add desired components to its panel.
 */

/**
 * @author Friedemann Zintel
 * 
 */
public class InfoWindow extends JFrame {

	protected class InfoWindowAdapter extends WindowAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowClosed(WindowEvent arg0) {

			undisplayAnchor();
			super.windowClosed(arg0);
		}

	}

	protected class InfoWindowComponentAdapter extends ComponentAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ComponentAdapter#componentMoved(java.awt.event.ComponentEvent)
		 */
		@Override
		public void componentMoved(ComponentEvent arg0) {

			adjustAnchor();
			super.componentMoved(arg0);
		}

	}

	InfoWindow window;

	int nhradius = 2;

	Node node;

	GOFilledCircle hook;

	GOHyperLine rope;

	JPanel panel = new JPanel();

	protected InfoWindow(String titel, Node node) {

		super(titel);

		this.window = this;

		this.node = node;

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.addWindowListener(new InfoWindowAdapter());

		this.addComponentListener(new InfoWindowComponentAdapter());

		this.setContentPane(panel);

		this.setResizable(false);

		this.setLocation(node.xPos(), node.yPos());

		this.setAlwaysOnTop(true);

		this.pack();

		this.setVisible(true);

		throwAnchor(node,this);

	}
	
	private void throwAnchor(Node node, JFrame window){

		Color anchorcolor = Color.red;

		// draw a dot (the hook) on the node and a line (the rope) to the
		// window with a dot (windowhook)
		hook = new GOFilledCircle(node.getX(), node.getY(), nhradius);
		rope = new GOHyperLine(node.getX(), node.getY(), this.getX(), this.getY());

		hook.setColor(anchorcolor);
		rope.setColor(anchorcolor);

		hook.display();
		rope.display();
		
	}
	
	private void adjustAnchor(){
		
		rope.setX2(window.getX());
		rope.setY2(window.getY());

	}
	
	private void undisplayAnchor(){

		hook.undisplay();
		rope.undisplay();
		
	}

	/**
	 * @return Returns the node.
	 */
	public Node getNode() {
		return node;
	}

}
