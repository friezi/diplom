/*
 * msgnet - a simulation framework for message passing networks.
 * Copyright (C) 2005  Robert Schiele <rschiele@uni-mannheim.de>
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package rsspubsubframework;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Gui object for the simulation.
 * 
 * This object handles actual drawing of the user interface.
 */
class Gui extends javax.swing.JComponent implements ActionListener {

	public static int WINDOW_TOP_BORDER_SIZE = 28;

	public static int WINDOW_LEFT_BORDER_SIZE = 6;

	protected class MouseClick extends MouseInputAdapter {

		Node firstnode = null;

		Node secondnode = null;

		public void mouseClicked(MouseEvent event) {

			// Button3 makes the controlframe visible or invisible
			if ( event.getButton() == MouseEvent.BUTTON3 ) {

				if ( toolbar == true ) {
					toolbar = false;
					controlframe.setVisible(false);
				} else {
					toolbar = true;
					controlframe.setVisible(true);
				}

			} else {

				Point point = new Point(event.getPoint().x - WINDOW_LEFT_BORDER_SIZE, event.getPoint().y - WINDOW_TOP_BORDER_SIZE);

				if ( choice_status == FIRST_CHOICE ) {
					// check if we hit a Node
					if ( (firstnode = Engine.getSingleton().findNode(point)) != null ) {
						if ( choice == ADD_CONN_CMD ) {

							addconnectionbutton.setText(selectSecondNodeTxt);

						} else if ( choice == DEL_CONN_CMD ) {

							deleteconnectionbutton.setText(selectSecondNodeTxt);

						} else if ( choice == BLOCK_NODE_CMD ) {

							firstnode.block();
							choice_status = NO_CHOICE;
							firstnode.setColor(new Color((float) 0.5, 0, 0));
							enableGroup(buttongroup1);
							return;

						} else if ( choice == UNBLOCK_NODE_CMD ) {

							firstnode.unblock();
							choice_status = NO_CHOICE;
							firstnode.setDefaultColor();
							enableGroup(buttongroup1);
							return;
						}

						choice_status = SECOND_CHOICE;
						firstnode.setColor(Color.red);
					} else {
						// no node hit

						choice_status = NO_CHOICE;
						enableGroup(buttongroup1);

					}

				} else if ( choice_status == SECOND_CHOICE ) {

					if ( (secondnode = Engine.getSingleton().findNode(point)) != null ) {
						if ( choice == ADD_CONN_CMD ) {

							addconnectionbutton.setText(addCmd);
							addconnectionbutton.setEnabled(true);

						} else if ( choice == DEL_CONN_CMD ) {

							deleteconnectionbutton.setText(deleteCmd);
							deleteconnectionbutton.setEnabled(true);

						}
						choice_status = DO_IT;
						secondnode.setColor(Color.red);
					} else {
						// no node hit
						choice_status = NO_CHOICE;
						enableGroup(buttongroup1);
						firstnode.setDefaultColor();
					}
				}
			}
		}
	}

	protected class StatisticFrame extends JFrame {

		int width = 230;

		int height = 100;

		long receivedRSSRequests = 0;

		long omittedRSSRequests = 0;

		long serverFeeds = 0;

		long brokerFeeds = 0;

		int reOmRatio = 0;

		int srvBrRatio = 0;

		JLabel receivedLabel = new JLabel();

		JLabel omittedLabel = new JLabel();

		JLabel reOmRatioLabel = new JLabel();

		JLabel serverFeedsLabel = new JLabel();

		JLabel brokerFeedsLabel = new JLabel();

		JLabel srvBrRatioLabel = new JLabel();

		protected class LPanel extends JPanel {
			public LPanel() {
				super(new GridLayout(3, 2));
			}
		}

		LPanel panel = new LPanel();

		protected class ReceivedRSSRequestsObserver implements Observer {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Observer#update(java.util.Observable,
			 *      java.lang.Object)
			 */
			public void update(Observable observable, Object object) {
				// TODO Auto-generated method stub

				receivedRSSRequests = (Long) object;
				updateReOmRatio();
				receivedLabel.setText("received requests: " + receivedRSSRequests);
				showReOmRatio();

			}

		}

		protected class OmittedRSSRequestsObserver implements Observer {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Observer#update(java.util.Observable,
			 *      java.lang.Object)
			 */
			public void update(Observable observable, Object object) {
				// TODO Auto-generated method stub

				omittedRSSRequests = (Long) object;
				updateReOmRatio();
				omittedLabel.setText("omitted requests: " + omittedRSSRequests);
				showReOmRatio();

			}

		}

		protected class ServerFeedsObserver implements Observer {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Observer#update(java.util.Observable,
			 *      java.lang.Object)
			 */
			public void update(Observable observable, Object object) {
				// TODO Auto-generated method stub

				serverFeeds = (Long) object;
				updateSrvBrRatio();
				serverFeedsLabel.setText("feeds from server: " + serverFeeds);
				showSrvBrRatio();

			}

		}

		protected class BrokerFeedsObserver implements Observer {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Observer#update(java.util.Observable,
			 *      java.lang.Object)
			 */
			public void update(Observable observable, Object object) {
				// TODO Auto-generated method stub

				brokerFeeds = (Long) object;
				updateSrvBrRatio();
				brokerFeedsLabel.setText("feeds from broker: " + brokerFeeds);
				showSrvBrRatio();

			}

		}

		private ReceivedRSSRequestsObserver receivedRSSRequestsObserver = new ReceivedRSSRequestsObserver();

		private OmittedRSSRequestsObserver omittedRSSRequestsObserver = new OmittedRSSRequestsObserver();

		private ServerFeedsObserver serverFeedsObserver = new ServerFeedsObserver();

		private BrokerFeedsObserver brokerFeedsObserver = new BrokerFeedsObserver();

		protected class CloseWindowListener extends WindowAdapter {

			/* (non-Javadoc)
			 * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				Engine.getSingleton().getRpsStatistics().getReceivedRSSFeedRequestObserver().deleteObserver(getOmittedRSSRequestsObserver());
				Engine.getSingleton().getRpsStatistics().getOmittedRSSFeedRequestObserver().deleteObserver(getReceivedRSSRequestsObserver());
				Engine.getSingleton().getRpsStatistics().getServerFeedObserver().deleteObserver(getServerFeedsObserver());
				Engine.getSingleton().getRpsStatistics().getBrokerFeedObserver().deleteObserver(getBrokerFeedsObserver());
				super.windowClosed(arg0);
			}

		}

		public StatisticFrame(String title) {

			super(title);

			Engine.getSingleton().getRpsStatistics().getReceivedRSSFeedRequestObserver().addObserver(getReceivedRSSRequestsObserver());
			Engine.getSingleton().getRpsStatistics().getOmittedRSSFeedRequestObserver().addObserver(getOmittedRSSRequestsObserver());
			Engine.getSingleton().getRpsStatistics().getServerFeedObserver().addObserver(getServerFeedsObserver());
			Engine.getSingleton().getRpsStatistics().getBrokerFeedObserver().addObserver(getBrokerFeedsObserver());

			this.addWindowListener(new CloseWindowListener());

			this.setSize(new Dimension(width, height));

			panel.add(receivedLabel);
			panel.add(serverFeedsLabel);
			panel.add(omittedLabel);
			panel.add(brokerFeedsLabel);
			panel.add(reOmRatioLabel);
			panel.add(srvBrRatioLabel);

			panel.setOpaque(true);
			this.setContentPane(panel);

			this.pack();
		}

		void updateReOmRatio() {

			long divisor = (receivedRSSRequests + omittedRSSRequests);

			if ( divisor != 0 )
				reOmRatio = (int) (((100 * omittedRSSRequests)) / divisor);
			else
				reOmRatio = 0;
		}

		void showReOmRatio() {
			reOmRatioLabel.setText("ratio: " + reOmRatio + "% saved requests");
		}

		void updateSrvBrRatio() {

			long divisor = (serverFeeds + brokerFeeds);

			if ( divisor != 0 )
				srvBrRatio = (int) (((100 * brokerFeeds)) / divisor);
			else
				srvBrRatio = 0;

		}

		void showSrvBrRatio() {
			srvBrRatioLabel.setText("ratio: " + srvBrRatio + "% feeds from network");
		}

		/**
		 * @return Returns the omittedRSSRequestsObserver.
		 */
		public OmittedRSSRequestsObserver getOmittedRSSRequestsObserver() {
			return omittedRSSRequestsObserver;
		}

		/**
		 * @return Returns the receivedRSSRequestsObserver.
		 */
		public ReceivedRSSRequestsObserver getReceivedRSSRequestsObserver() {
			return receivedRSSRequestsObserver;
		}

		/**
		 * @return Returns the brokerFeedsObserver.
		 */
		public BrokerFeedsObserver getBrokerFeedsObserver() {
			return brokerFeedsObserver;
		}

		/**
		 * @return Returns the serverFeedsObserver.
		 */
		public ServerFeedsObserver getServerFeedsObserver() {
			return serverFeedsObserver;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6099692865286022422L;

	private static final String windowtitle = "RSS-feed distribution";

	// / Width of the drawing pane.
	private int currentWidth;

	// / Height of the drawing pane.
	private int currentHeight;

	private int df_width;

	private int df_height;

	private int cf_xpos = 100;

	private int cf_ypos = 0;

	private static String startSimulationCmd = "Start Simulation";

	private static String stopSimulationCmd = "Stop Simulation";

	private static String continueSimulationCmd = "Continue Simulation";

	private static String runningTxt = "running ...";

	private static String statisticCmd = "Statistics";

	private static String showEdgeIdCmd = "Show edge-ids";

	private static String hideEdgeIdCmd = "Hide edge-ids";

	private static String deleteConnectionCmd = "Delete connection";

	private static String deleteCmd = "Delete!";

	private static String addConnectionCmd = "Add connection";

	private static String addCmd = "Add!";

	private static String cancelCmd = "Cancel";

	private static String selectFirstNodeTxt = "Select first node ...";

	private static String selectSecondNodeTxt = "Select second node ...";

	private static String selectBlockNodeTxt = "Select Node to block ...";

	private static String selectUnblockNodeTxt = "Select Node to unblock ...";

	private static String selectBrokerTxt = "Select Broker ...";

	private static String blockCmd = "Block Node";

	private static String unblockCmd = "Unblock Node";

	private static String exitCmd = "Exit";

	private JButton exitbutton;

	private JButton controlbutton;

	private JButton statisticbutton;

	private JButton edgeidbutton;

	private JButton deleteconnectionbutton;

	private JButton addconnectionbutton;

	private JButton blocknodebutton;

	private JButton unblocknodebutton;

	private JButton cancelbutton;

	private HashMap<JButton, String> buttongroup1 = new HashMap<JButton, String>();

	private static int NO_CHOICE = 0;

	private static int FIRST_CHOICE = 1;

	private static int SECOND_CHOICE = 2;

	private static int DO_IT = 3;

	private int choice_status = NO_CHOICE;

	private static int ADD_CONN_CMD = 0;

	private static int DEL_CONN_CMD = 1;

	private static int BLOCK_NODE_CMD = 2;

	private static int UNBLOCK_NODE_CMD = 3;

	private int choice = ADD_CONN_CMD;

	private boolean toolbar = true;

	private JFrame controlframe;

	private StatisticFrame statisticframe;

	private MouseClick mouseclick = new MouseClick();

	/**
	 * Create the gui window.
	 */
	public Gui() {
		final Gui guip = this;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				JFrame.setDefaultLookAndFeelDecorated(true);

				JFrame displayframe = new JFrame(windowtitle);
				displayframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				displayframe.getContentPane().add(guip);

				controlframe = new JFrame("Controls");

				// open window maximized
				df_width = displayframe.getGraphicsConfiguration().getDevice().getDisplayMode().getWidth();
				df_height = displayframe.getGraphicsConfiguration().getDevice().getDisplayMode().getHeight();
				// displayframe.setBounds(new Rectangle(df_width, df_height));
				displayframe.setSize(new Dimension(df_width, df_height));

				displayframe.addMouseListener(mouseclick);

				JPanel buttonpanel = new JPanel();

				exitbutton = new JButton(exitCmd);
				exitbutton.setVerticalTextPosition(AbstractButton.CENTER);
				exitbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				exitbutton.addActionListener(guip);
				buttonpanel.add(exitbutton);

				controlbutton = new JButton(startSimulationCmd);
				controlbutton.setVerticalTextPosition(AbstractButton.CENTER);
				controlbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				controlbutton.addActionListener(guip);
				buttonpanel.add(controlbutton);

				statisticbutton = new JButton(statisticCmd);
				statisticbutton.setVerticalTextPosition(AbstractButton.CENTER);
				statisticbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				statisticbutton.addActionListener(guip);
				buttonpanel.add(statisticbutton);

				edgeidbutton = new JButton(showEdgeIdCmd);
				edgeidbutton.setVerticalTextPosition(AbstractButton.CENTER);
				edgeidbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				edgeidbutton.addActionListener(guip);
				buttonpanel.add(edgeidbutton);

				deleteconnectionbutton = new JButton(deleteConnectionCmd);
				deleteconnectionbutton.setVerticalTextPosition(AbstractButton.CENTER);
				deleteconnectionbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				deleteconnectionbutton.addActionListener(guip);
				buttonpanel.add(deleteconnectionbutton);

				addconnectionbutton = new JButton(addConnectionCmd);
				addconnectionbutton.setVerticalTextPosition(AbstractButton.CENTER);
				addconnectionbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				addconnectionbutton.addActionListener(guip);
				buttonpanel.add(addconnectionbutton);

				blocknodebutton = new JButton(blockCmd);
				blocknodebutton.setVerticalTextPosition(AbstractButton.CENTER);
				blocknodebutton.setHorizontalTextPosition(AbstractButton.LEADING);
				blocknodebutton.addActionListener(guip);
				buttonpanel.add(blocknodebutton);

				unblocknodebutton = new JButton(unblockCmd);
				unblocknodebutton.setVerticalTextPosition(AbstractButton.CENTER);
				unblocknodebutton.setHorizontalTextPosition(AbstractButton.LEADING);
				unblocknodebutton.addActionListener(guip);
				buttonpanel.add(unblocknodebutton);

				cancelbutton = new JButton(cancelCmd);
				cancelbutton.setVerticalTextPosition(AbstractButton.CENTER);
				cancelbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				cancelbutton.addActionListener(guip);
				buttonpanel.add(cancelbutton);

				// put buttons in a logical group
				buttongroup1.put(addconnectionbutton, addConnectionCmd);
				buttongroup1.put(deleteconnectionbutton, deleteConnectionCmd);
				buttongroup1.put(blocknodebutton, blockCmd);
				buttongroup1.put(unblocknodebutton, unblockCmd);

				// displayframe.getContentPane().add(buttonpanel);
				controlframe.getContentPane().add(buttonpanel);
				controlframe.pack();
				// int cf_width = (int) controlframe.getBounds().getWidth();
				// int cf_maximized = (int)
				// controlframe.getMaximizedBounds().getWidth();
				controlframe.setLocation(cf_xpos /* cf_maximized / 2 *//*- cf_width / 2*/, cf_ypos);
				controlframe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				controlframe.setAlwaysOnTop(true);

				// displayframe.setExtendedState(Frame.MAXIMIZED_HORIZ);
				displayframe.setVisible(true);
				controlframe.setVisible(true);
			}
		});
	}

	/**
	 * Return the width of the drawing pane.
	 * 
	 * @return Width of the drawing pane.
	 */
	int guiWidth() {
		return currentWidth;
	}

	/**
	 * Return the height of the drawing pane.
	 * 
	 * @return Height of the drawing pane.
	 */
	int guiHeight() {
		return currentHeight;
	}

	/**
	 * Draw the user interface.
	 * 
	 * Calculates the drawing pane size and triggers the engine to draw all
	 * objects.
	 * 
	 * @param g
	 *            The Graphics object to draw on.
	 */
	protected void paintComponent(java.awt.Graphics g) {
		java.awt.Insets insets = getInsets();
		currentWidth = getWidth() - insets.left - insets.right;
		currentHeight = getHeight() - insets.top - insets.bottom;
		Engine.getSingleton().draw(g);
	}

	/**
	 * Disables a set (group) of buttons
	 * 
	 * @param group
	 *            the group of buttons to be disabled
	 */
	protected void disableGroup(HashMap<JButton, String> group) {

		Set<Map.Entry<JButton, String>> entries = group.entrySet();

		for ( Map.Entry<JButton, String> entry : entries )
			entry.getKey().setEnabled(false); // disable
	}

	/**
	 * Enables a set (group) of buttons and sets the default-command
	 * 
	 * @param group
	 *            the group of buttons to be enabled
	 */
	protected void enableGroup(HashMap<JButton, String> group) {

		Set<Map.Entry<JButton, String>> entries = group.entrySet();

		for ( Map.Entry<JButton, String> entry : entries ) {
			// set default-operation
			entry.getKey().setText(entry.getValue());
			// enable
			entry.getKey().setEnabled(true);
		}
	}

	public void actionPerformed(ActionEvent e) {

		if ( e.getActionCommand().equals(startSimulationCmd) ) {

			System.out.println("Simulation started ...");
			controlbutton.setText(runningTxt);
			controlbutton.setEnabled(false);
			Engine.getSingleton().setStarted(true);

		} else if ( e.getActionCommand().equals(exitCmd) ) {

			controlframe.setAlwaysOnTop(false);

			if ( JOptionPane.showConfirmDialog(controlframe, "Really exit?", "Please confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION )
				System.exit(0);

			controlframe.setAlwaysOnTop(true);

		} else if ( e.getActionCommand().equals(stopSimulationCmd) ) {
			// NEVER REACHED SO FAR
			System.out.println("Simulation stopped!");
			controlbutton.setText(continueSimulationCmd);
			Engine.getSingleton().setStopped(true);

		} else if ( e.getActionCommand().equals(continueSimulationCmd) ) {
			// NEVER REACHED SO FAR
			System.out.println("Simulation continued!");
			controlbutton.setText(stopSimulationCmd);
			Engine.getSingleton().setContinued(true);

		} else if ( e.getActionCommand().equals(statisticCmd) ) {

			statisticframe = new StatisticFrame("Statistics");
			statisticframe.setVisible(true);

		} else if ( e.getActionCommand().equals(showEdgeIdCmd) ) {

			Edge.setIdOn(true);
			edgeidbutton.setText(hideEdgeIdCmd);

		} else if ( e.getActionCommand().equals(hideEdgeIdCmd) ) {

			Edge.setIdOn(false);
			edgeidbutton.setText(showEdgeIdCmd);

		} else if ( e.getActionCommand().equals(deleteConnectionCmd) ) {

			choice = DEL_CONN_CMD;
			deleteconnectionbutton.setText(selectFirstNodeTxt);
			disableGroup(buttongroup1);
			choice_status = FIRST_CHOICE;

		} else if ( e.getActionCommand().equals(addConnectionCmd) ) {

			choice = ADD_CONN_CMD;
			addconnectionbutton.setText(selectFirstNodeTxt);
			disableGroup(buttongroup1);
			choice_status = FIRST_CHOICE;

		} else if ( e.getActionCommand().equals(blockCmd) ) {

			choice = BLOCK_NODE_CMD;
			blocknodebutton.setText(selectBlockNodeTxt);
			disableGroup(buttongroup1);
			choice_status = FIRST_CHOICE;

		} else if ( e.getActionCommand().equals(unblockCmd) ) {

			choice = UNBLOCK_NODE_CMD;
			unblocknodebutton.setText(selectUnblockNodeTxt);
			disableGroup(buttongroup1);
			choice_status = FIRST_CHOICE;

		} else if ( e.getActionCommand().equals(deleteCmd) ) {

			choice = NO_CHOICE;
			mouseclick.firstnode.setDefaultColor();
			mouseclick.secondnode.setDefaultColor();
			enableGroup(buttongroup1);
			// Engine.getSingleton().removeEdgeFromNodes(mouseclick.firstnode,
			// mouseclick.secondnode);
			processUnregister(mouseclick.firstnode, mouseclick.secondnode);

		} else if ( e.getActionCommand().equals(addCmd) ) {

			choice = NO_CHOICE;
			mouseclick.firstnode.setDefaultColor();
			mouseclick.secondnode.setDefaultColor();
			enableGroup(buttongroup1);
			Engine.getSingleton().addEdge(mouseclick.firstnode, mouseclick.secondnode);
			processRegister(mouseclick.firstnode, mouseclick.secondnode);

			// } else if ( e.getActionCommand().equals(blockCmd) ) {
			// choice = NO_CHOICE;
			// mouseclick.firstnode.setDefaultColor();
			// mouseclick.secondnode.setDefaultColor();
			// enableGroup(buttongroup1);
			// Engine.getSingleton().removeEdgeFromNodes(mouseclick.firstnode,
			// mouseclick.secondnode);
			// ((PubSubType) mouseclick.firstnode).unregister((BrokerType)
			// mouseclick.secondnode);
			// // PUT IN ACTION!!!!!!!!!!!!!

		} else if ( e.getActionCommand().equals(cancelCmd) ) {

			choice = NO_CHOICE;

			if ( mouseclick.firstnode != null ) {
				mouseclick.firstnode.setDefaultColor();
				mouseclick.firstnode = null;
			}
			if ( mouseclick.secondnode != null ) {
				mouseclick.secondnode.setDefaultColor();
				mouseclick.firstnode = null;
			}
			enableGroup(buttongroup1);
		}
	}

	protected boolean twoBrokers(Node node1, Node node2) {
		if ( node1 instanceof BrokerType )
			if ( node2 instanceof BrokerType )
				return true;
		return false;
	}

	protected boolean subscriberToBroker(Node node1, Node node2) {
		if ( node1 instanceof PubSubType )
			if ( node2 instanceof BrokerType )
				return true;
		return false;
	}

	protected void processRegister(Node node1, Node node2) {

		// A broker should only register at a broker
		if ( twoBrokers(node1, node2) == true ) {

			((BrokerType) node1).callbackRegisterAtBroker((BrokerType) node2);
			// ((BrokerType) node2).register((BrokerType) node1);

		} else if ( subscriberToBroker(node1, node2) == true ) {
			// a subscriber should only register at a broker
			((PubSubType) node1).callbackRegisterAtBroker((BrokerType) node2);
		} else if ( subscriberToBroker(node2, node1) == true ) {
			// or vice-versa?
			((PubSubType) node2).callbackRegisterAtBroker((BrokerType) node1);
		}
	}

	protected void processUnregister(Node node1, Node node2) {

		// A broker should only register at a broker
		if ( twoBrokers(node1, node2) == true ) {

			((BrokerType) node1).callbackUnregisterFromBroker((BrokerType) node2);
			// ((BrokerType) node2).register((BrokerType) node1);

		} else if ( subscriberToBroker(node1, node2) == true ) {
			// a subscriber should only register at a broker
			((PubSubType) node1).callbackUnregisterFromBroker((BrokerType) node2);
		} else if ( subscriberToBroker(node2, node1) == true ) {
			// or vice-versa?
			((PubSubType) node2).callbackUnregisterFromBroker((BrokerType) node1);
		}
	}
	//
}
