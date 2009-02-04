package de.zintel.diplom.gui;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import de.zintel.diplom.graphics.VisualAnchor;
import de.zintel.diplom.rps.broker.BrokerNode;
import de.zintel.diplom.rps.pubsub.PubSubNode;
import de.zintel.diplom.rps.server.RSSServerNode;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.Node;

/**
 */

/**
 * This Window for showing information about a Node. Add desired components to
 * its panel.
 * 
 * @author Friedemann Zintel
 * 
 */
@SuppressWarnings("serial")
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
		public void windowClosed(WindowEvent event) {

			if ( anchor != null )
				anchor.undisplayAnchor();
			Engine.getSingleton().removeWindow(window);
			super.windowClosed(event);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.WindowAdapter#windowDeiconified(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowDeiconified(WindowEvent event) {

			if ( event.getWindow().isShowing() == true )
				if ( anchor != null )
					anchor.grabAnchor();
			super.windowDeiconified(event);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.WindowAdapter#windowIconified(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowIconified(WindowEvent event) {

			if ( anchor != null )
				anchor.loseAnchor();
			super.windowIconified(event);

		}

	}

	protected class InfoWindowComponentAdapter extends ComponentAdapter {

		private JFrame window;

		public InfoWindowComponentAdapter(JFrame window) {
			this.window = window;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ComponentAdapter#componentMoved(java.awt.event.ComponentEvent)
		 */
		@Override
		public void componentMoved(ComponentEvent event) {

			if ( anchor != null )
				anchor.adjustAnchor();
			super.componentMoved(event);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ComponentAdapter#componentHidden(java.awt.event.ComponentEvent)
		 */
		@Override
		public void componentHidden(ComponentEvent event) {

			if ( anchor != null )
				anchor.loseAnchor();
			super.componentHidden(event);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ComponentAdapter#componentShown(java.awt.event.ComponentEvent)
		 */
		@Override
		public void componentShown(ComponentEvent event) {

			if ( window.getExtendedState() != Frame.ICONIFIED )
				if ( anchor != null )
					anchor.grabAnchor();
			super.componentShown(event);

		}

	}

	private static String moreInfoTxt = "more Info";

	String title;

	Node node;

	VisualAnchor anchor = null;

	JPanel panel = new JPanel();

	public InfoWindow(String title) {
		this(title, null);
	}

	public InfoWindow(String title, Node node) {
		this(title, node, false);
	}

	public InfoWindow(String title, Node node, boolean moreinfo) {

		super(title);

		this.title = title;

		this.node = node;

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.setResizable(true);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		this.setContentPane(panel);

		this.setAlwaysOnTop(true);

		if ( moreinfo == true ) {

			JPanel buttonpanel = new JPanel(new BorderLayout());

			JButton moreInfo = new JButton(moreInfoTxt);
			buttonpanel.add(moreInfo);
			buttonpanel.setBorder(new EmptyBorder(0, 40, 0, 40));

			moreInfo.addActionListener(this);

			panel.add(buttonpanel);

		}

		if ( Engine.getSingleton().getGui() != null ) {

			if ( node != null )
				this.setLocation(node.xPos(), node.yPos());

			this.addWindowListener(new InfoWindowAdapter(this));

			this.addComponentListener(new InfoWindowComponentAdapter(this));

			if ( node != null )
				anchor = new VisualAnchor(node, this);

		}

		this.pack();

		this.setVisible(true);

		Engine.getSingleton().addWindow(this);

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
