import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import rsspubsubframework.*;

/**
 */

/**
 * This Window for showing information about a Node. Add desired components to
 * its panel.
 * 
 * @author Friedemann Zintel
 * 
 */
public class InfoWindow extends JFrame implements ActionListener {

	protected class InfoWindowAdapter extends WindowAdapter {

		InfoWindow window;

		public InfoWindowAdapter(InfoWindow window) {
			this.window = window;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowClosed(WindowEvent arg0) {

			anchor.undisplayAnchor();
			Engine.getSingleton().getDb().removeWindow(window);
			super.windowClosed(arg0);
		}

		/* (non-Javadoc)
		 * @see java.awt.event.WindowAdapter#windowDeiconified(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowDeiconified(WindowEvent e) {
			anchor.grabAnchor();
			super.windowDeiconified(e);
		}

		/* (non-Javadoc)
		 * @see java.awt.event.WindowAdapter#windowIconified(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowIconified(WindowEvent e) {
			anchor.loseAnchor();
			super.windowIconified(e);
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

			anchor.adjustAnchor();
			super.componentMoved(arg0);
		}

	}

	private static String moreInfoTxt = "more Info";

	String title;

	Node node;

	VisualAnchor anchor;

	JPanel panel = new JPanel();

	protected InfoWindow(String title, Node node) {
		this(title, node, false);
	}

	protected InfoWindow(String title, Node node, boolean moreinfo) {

		super(title);

		this.title = title;

		this.node = node;

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.addWindowListener(new InfoWindowAdapter(this));

		this.addComponentListener(new InfoWindowComponentAdapter());

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		this.setContentPane(panel);

		this.setResizable(false);

		this.setLocation(node.xPos(), node.yPos());

		this.setAlwaysOnTop(true);

		JPanel buttonpanel = new JPanel(new BorderLayout());

		if ( moreinfo == true ) {

			JButton moreInfo = new JButton(moreInfoTxt);
			buttonpanel.add(moreInfo);
			buttonpanel.setBorder(new EmptyBorder(0, 40, 0, 40));

			moreInfo.addActionListener(this);

		}

		panel.add(buttonpanel);

		this.pack();

		this.setVisible(true);

		anchor = new VisualAnchor(node, this);

		Engine.getSingleton().getDb().addWindow(this);

	}

	/**
	 * @return Returns the node.
	 */
	public Node getNode() {
		return node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		if ( e.getActionCommand().equals(moreInfoTxt) ) {

			if ( node instanceof RSSServerNode )
				((RSSServerNode) node).showMoreInfo(new InfoWindow(title, node));
			else if ( node instanceof PubSubNode )
				((PubSubNode) node).showMoreInfo(new InfoWindow(title, node));
			else if ( node instanceof BrokerNode )
				((BrokerNode) node).showMoreInfo(new InfoWindow(title, node));
		}

	}

}
