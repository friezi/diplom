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
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Gui object for the simulation.
 * 
 * This object handles actual drawing of the user interface.
 */
public class Gui extends javax.swing.JComponent implements ActionListener {

	protected class MouseClick extends MouseInputAdapter {

		Node firstnode = null;

		Node secondnode = null;

		GORectangle rectangle = new GORectangle(0, 0, 0, 0);

		public void mouseDragged(MouseEvent event) {

			if ( choice_status == SELECTION ) {

				if ( choice == BLOCK_NODE_SELECTION_CMD ) {

					Point ds_point = new Point(Engine.deScaleX(event.getPoint().x), Engine.deScaleY(event.getPoint().y));

					rectangle.resetX2Y2(ds_point);

				}

				if ( choice == UNBLOCK_NODE_SELECTION_CMD ) {

					Point ds_point = new Point(Engine.deScaleX(event.getPoint().x), Engine.deScaleY(event.getPoint().y));

					rectangle.resetX2Y2(ds_point);

				}
			}

		}

		public void mousePressed(MouseEvent event) {

			if ( choice_status == SELECTION ) {

				if ( choice == BLOCK_NODE_SELECTION_CMD ) {

					Point ds_point = new Point(Engine.deScaleX(event.getPoint().x), Engine.deScaleY(event.getPoint().y));

					rectangle.undisplay();
					(rectangle = new GORectangle(ds_point)).display();
					return;
				}

				if ( choice == UNBLOCK_NODE_SELECTION_CMD ) {

					Point ds_point = new Point(Engine.deScaleX(event.getPoint().x), Engine.deScaleY(event.getPoint().y));

					rectangle.undisplay();
					(rectangle = new GORectangle(ds_point)).display();
					return;
				}

			}

		}

		public void mouseReleased(MouseEvent event) {

			if ( choice_status == SELECTION ) {

				if ( choice == BLOCK_NODE_SELECTION_CMD ) {

					blocknodeselectionbutton.setText(blockAllCmd);
					blocknodeselectionbutton.setEnabled(true);
					return;
				}

				if ( choice == UNBLOCK_NODE_SELECTION_CMD ) {

					unblocknodeselectionbutton.setText(unblockAllCmd);
					unblocknodeselectionbutton.setEnabled(true);
					return;
				}

			}
		}

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

				Point point = new Point(event.getPoint().x, event.getPoint().y);

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
							enableGroup(buttongroup1);
							return;

						} else if ( choice == UNBLOCK_NODE_CMD ) {

							firstnode.unblock();
							choice_status = NO_CHOICE;
							enableGroup(buttongroup1);
							return;
						} else if ( choice == SHOW_INFO_CMD ) {

							showInfo(firstnode);
							choice_status = NO_CHOICE;
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

	/**
	 * 
	 */
	private static final long serialVersionUID = -6099692865286022422L;

	private static final String windowtitle = "RSS-feed distribution";

	// / Width of the drawing pane.
	private int currentWidth;

	// / Height of the drawing pane.
	private int currentHeight;

	private int currentXPos;

	private int currentYPos;

	private int df_width;

	private int df_height;

	private int cf_xpos = 100;

	private int cf_ypos = 0;

	private static String startSimulationCmd = "Start";

	private static String stopSimulationCmd = "Stop";

	private static String continueSimulationCmd = "Continue";

	private static String runningTxt = "running ...";

	private static String statisticCmd = "Stat.";

	private static String showEdgeIdCmd = "SE";

	private static String hideEdgeIdCmd = "HE";

	private static String deleteConnectionCmd = "DC";

	private static String deleteCmd = "Delete!";

	private static String addConnectionCmd = "AC";

	private static String addCmd = "Add!";

	private static String brokersFallApartCmd = "BF";

	private static String showInfoCmd = "SI";

	private static String selectNodeToShowCmd = "Select node to show info";

	private static String blockAllCmd = "Block all!";

	private static String unblockAllCmd = "Unblock all!";

	private static String cancelCmd = "Cancel";

	private static String selectFirstNodeTxt = "Select first node ...";

	private static String selectSecondNodeTxt = "Select second node ...";

	private static String selectBlockNodeTxt = "Select Node to block ...";

	private static String selectUnblockNodeTxt = "Select Node to unblock ...";

	private static String selectBlockNodeSelectionTxt = "Select range to block";

	private static String selectUnblockNodeSelectionTxt = "Select range to unblock";

	private static String selectBrokerTxt = "Select Broker ...";

	private static String blockCmd = "BN";

	private static String unblockCmd = "UN";

	private static String blockSelectionCmd = "BNS";

	private static String unblockSelectionCmd = "UNS";

	private static String exitCmd = "Exit";

	private JButton exitbutton;

	private JButton controlbutton;

	private JButton statisticbutton;

	private JButton edgeidbutton;

	private JButton showinfobutton;

	private JButton deleteconnectionbutton;

	private JButton addconnectionbutton;

	private JButton blocknodebutton;

	private JButton unblocknodebutton;

	private JButton blocknodeselectionbutton;

	private JButton unblocknodeselectionbutton;

	private JButton brokersfallapartbutton;

	private JButton cancelbutton;

	private HashMap<JButton, String> buttongroup1 = new HashMap<JButton, String>();

	private static int NO_CHOICE = 0;

	private static int FIRST_CHOICE = 1;

	private static int SECOND_CHOICE = 2;

	private static int DO_IT = 3;

	private static int SELECTION = 4;

	private int choice_status = NO_CHOICE;

	private static int ADD_CONN_CMD = 0;

	private static int DEL_CONN_CMD = 1;

	private static int BLOCK_NODE_CMD = 2;

	private static int UNBLOCK_NODE_CMD = 3;

	private static int BLOCK_NODE_SELECTION_CMD = 4;

	private static int UNBLOCK_NODE_SELECTION_CMD = 5;

	private static int SHOW_INFO_CMD = 6;

	private int choice = ADD_CONN_CMD;

	private boolean toolbar = true;

	private JFrame controlframe;

	private StatisticWindow statisticframe;
	
	private JFrame displayframe;

	private MouseClick mouseclick = new MouseClick();

	/**
	 * Create the gui window.
	 */
	public Gui() {
		final Gui guip = this;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				JFrame.setDefaultLookAndFeelDecorated(true);

				displayframe = new JFrame(windowtitle);
				displayframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				displayframe.getContentPane().add(guip);

				controlframe = new JFrame("Controls");

				// open window maximized
				df_width = displayframe.getGraphicsConfiguration().getDevice().getDisplayMode().getWidth();
				df_height = displayframe.getGraphicsConfiguration().getDevice().getDisplayMode().getHeight();
				// displayframe.setBounds(new Rectangle(df_width, df_height));
				displayframe.setSize(new Dimension(df_width, df_height));

				guip.addMouseListener(mouseclick);
				guip.addMouseMotionListener(mouseclick);

				JPanel buttonpanel = new JPanel(new GridLayout(13, 1));
				buttonpanel.setBorder(new EmptyBorder(10, 30, 10, 30));

				exitbutton = new JButton(exitCmd);
				// exitbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// exitbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				exitbutton.addActionListener(guip);
				buttonpanel.add(exitbutton);

				controlbutton = new JButton(startSimulationCmd);
				// controlbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// controlbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				controlbutton.setToolTipText("Start Simulation");
				controlbutton.addActionListener(guip);
				buttonpanel.add(controlbutton);

				statisticbutton = new JButton(statisticCmd);
				// statisticbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// statisticbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				statisticbutton.setToolTipText("Statistics");
				statisticbutton.addActionListener(guip);
				buttonpanel.add(statisticbutton);

				edgeidbutton = new JButton(showEdgeIdCmd);
				// edgeidbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// edgeidbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				edgeidbutton.setToolTipText("Show edge-ids");
				edgeidbutton.addActionListener(guip);
				buttonpanel.add(edgeidbutton);

				showinfobutton = new JButton(showInfoCmd);
				showinfobutton.setToolTipText("Show node information");
				showinfobutton.addActionListener(guip);
				buttonpanel.add(showinfobutton);

				deleteconnectionbutton = new JButton(deleteConnectionCmd);
				// deleteconnectionbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// deleteconnectionbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				deleteconnectionbutton.setToolTipText("Delete connection");
				deleteconnectionbutton.addActionListener(guip);
				buttonpanel.add(deleteconnectionbutton);

				addconnectionbutton = new JButton(addConnectionCmd);
				// addconnectionbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// addconnectionbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				addconnectionbutton.setToolTipText("Add connection");
				addconnectionbutton.addActionListener(guip);
				buttonpanel.add(addconnectionbutton);

				blocknodebutton = new JButton(blockCmd);
				// blocknodebutton.setVerticalTextPosition(AbstractButton.CENTER);
				// blocknodebutton.setHorizontalTextPosition(AbstractButton.LEADING);
				blocknodebutton.setToolTipText("Block node");
				blocknodebutton.addActionListener(guip);
				buttonpanel.add(blocknodebutton);

				unblocknodebutton = new JButton(unblockCmd);
				// unblocknodebutton.setVerticalTextPosition(AbstractButton.CENTER);
				// unblocknodebutton.setHorizontalTextPosition(AbstractButton.LEADING);
				unblocknodebutton.setToolTipText("Unblock node");
				unblocknodebutton.addActionListener(guip);
				buttonpanel.add(unblocknodebutton);

				blocknodeselectionbutton = new JButton(blockSelectionCmd);
				// blocknodeselectionbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// blocknodeselectionbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				blocknodeselectionbutton.setToolTipText("Block node-selection");
				blocknodeselectionbutton.addActionListener(guip);
				buttonpanel.add(blocknodeselectionbutton);

				unblocknodeselectionbutton = new JButton(unblockSelectionCmd);
				// unblocknodeselectionbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// unblocknodeselectionbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				unblocknodeselectionbutton.setToolTipText("Unblock node-selection");
				unblocknodeselectionbutton.addActionListener(guip);
				buttonpanel.add(unblocknodeselectionbutton);

				brokersfallapartbutton = new JButton(brokersFallApartCmd);
				brokersfallapartbutton.setToolTipText("Brokers fall apart");
				brokersfallapartbutton.addActionListener(guip);
				buttonpanel.add(brokersfallapartbutton);

				cancelbutton = new JButton(cancelCmd);
				// cancelbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// cancelbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				cancelbutton.addActionListener(guip);
				buttonpanel.add(cancelbutton);

				// put buttons in a logical group
				buttongroup1.put(addconnectionbutton, addConnectionCmd);
				buttongroup1.put(deleteconnectionbutton, deleteConnectionCmd);
				buttongroup1.put(blocknodebutton, blockCmd);
				buttongroup1.put(unblocknodebutton, unblockCmd);
				buttongroup1.put(blocknodeselectionbutton, blockSelectionCmd);
				buttongroup1.put(unblocknodeselectionbutton, unblockSelectionCmd);
				buttongroup1.put(brokersfallapartbutton, brokersFallApartCmd);
				buttongroup1.put(showinfobutton, showInfoCmd);

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
	public int guiWidth() {
		return currentWidth;
	}

	/**
	 * Return the height of the drawing pane.
	 * 
	 * @return Height of the drawing pane.
	 */
	public int guiHeight() {
		return currentHeight;
	}

	public int guiXPos() {
		return currentXPos;
	}

	public int guiYPos() {
		return currentYPos;
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
		currentXPos = displayframe.getX();
		currentYPos = displayframe.getY();
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

			statisticframe = new StatisticWindow("Statistics");
			statisticframe.setVisible(true);

		} else if ( e.getActionCommand().equals(showEdgeIdCmd) ) {

			Edge.setIdOn(true);
			edgeidbutton.setText(hideEdgeIdCmd);
			edgeidbutton.setToolTipText("Hide edge-ids");

		} else if ( e.getActionCommand().equals(hideEdgeIdCmd) ) {

			Edge.setIdOn(false);
			edgeidbutton.setText(showEdgeIdCmd);
			edgeidbutton.setToolTipText("Show edge-ids");

		} else if ( e.getActionCommand().equals(showInfoCmd) ) {

			choice = SHOW_INFO_CMD;
			showinfobutton.setText(selectNodeToShowCmd);
			disableGroup(buttongroup1);
			choice_status = FIRST_CHOICE;

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

		} else if ( e.getActionCommand().equals(blockSelectionCmd) ) {

			choice = BLOCK_NODE_SELECTION_CMD;
			blocknodeselectionbutton.setText(selectBlockNodeSelectionTxt);
			disableGroup(buttongroup1);
			choice_status = SELECTION;

		} else if ( e.getActionCommand().equals(blockAllCmd) ) {

			// block all nodes
			Point point1 = new Point(Engine.scaleX(mouseclick.rectangle.getX1()), Engine.scaleY(mouseclick.rectangle.getY1()));
			Point point2 = new Point(Engine.scaleX(mouseclick.rectangle.getX2()), Engine.scaleY(mouseclick.rectangle.getY2()));
			LinkedList<Node> nodes = Engine.getSingleton().findNodes(point1, point2);
			for ( Node node : nodes )
				node.block();

			Engine.getSingleton().removeGraphicalObject(mouseclick.rectangle);

			blocknodeselectionbutton.setText(blockSelectionCmd);
			enableGroup(buttongroup1);
			choice_status = NO_CHOICE;

		} else if ( e.getActionCommand().equals(unblockSelectionCmd) ) {

			choice = UNBLOCK_NODE_SELECTION_CMD;
			unblocknodeselectionbutton.setText(selectUnblockNodeSelectionTxt);
			disableGroup(buttongroup1);
			choice_status = SELECTION;

		} else if ( e.getActionCommand().equals(unblockAllCmd) ) {

			// block all nodes
			Point point1 = new Point(Engine.scaleX(mouseclick.rectangle.getX1()), Engine.scaleY(mouseclick.rectangle.getY1()));
			Point point2 = new Point(Engine.scaleX(mouseclick.rectangle.getX2()), Engine.scaleY(mouseclick.rectangle.getY2()));
			LinkedList<Node> nodes = Engine.getSingleton().findNodes(point1, point2);
			for ( Node node : nodes )
				node.unblock();

			Engine.getSingleton().removeGraphicalObject(mouseclick.rectangle);

			unblocknodeselectionbutton.setText(unblockSelectionCmd);
			enableGroup(buttongroup1);
			choice_status = NO_CHOICE;

		} else if ( e.getActionCommand().equals(brokersFallApartCmd) ) {

			synchronized ( Engine.getSingleton().nodeList ) {
				for ( Node node : Engine.getSingleton().nodeList )
					if ( node instanceof BrokerType )
						((BrokerType) node).callbackUnregisterFromAllBrokers();
			}

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
			Engine.getSingleton().removeGraphicalObject(mouseclick.rectangle);
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

	protected void showInfo(Node node) {

		if ( node instanceof RSSServerType ) {
			((RSSServerType) node).showInfo();
		} else if ( node instanceof BrokerType ) {
			((BrokerType) node).showInfo();
		} else if ( node instanceof PubSubType ) {
			((PubSubType) node).showInfo();
		}

	}
	//
}
