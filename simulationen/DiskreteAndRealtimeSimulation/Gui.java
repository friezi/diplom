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

		@Override
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

		@Override
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

		@Override
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

		@Override
		public void mouseClicked(MouseEvent event) {

			// Button3 makes the controlframe visible or invisible
			if ( event.getButton() == MouseEvent.BUTTON3 ) {

				// obsolete

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
	
	protected class DFWindowAdapter extends WindowAdapter{

		/* (non-Javadoc)
		 * @see java.awt.event.WindowAdapter#windowDeiconified(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowDeiconified(WindowEvent arg0) {
			super.windowDeiconified(arg0);
			deiconifyWindows();
		}

		/* (non-Javadoc)
		 * @see java.awt.event.WindowAdapter#windowIconified(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowIconified(WindowEvent arg0) {
			super.windowIconified(arg0);
			iconifyWindows();
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

	private int displayareaXPos;

	private int displayareaYPos;

	private int df_width;

	private int df_height;

	private int cf_xpos = 100;

	private int cf_ypos = 0;

	private static String startSimulationCmd = "Start";

	private static String stopSimulationCmd = "Stop";

	private static String continueSimulationCmd = "Continue";

	private static String runningTxt = "running ...";

	private static String statisticCmd = "Stat.";

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

	private static String iconifywindowsCmd = "IW";

	private static String deiconifywindowsCmd = "DW";

	private static String iconifywindowsTT = "Iconify windows";

	private static String deiconifywindowsTT = "Deiconify windows";

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

	private JButton showinfobutton;

	private JButton deleteconnectionbutton;

	private JButton addconnectionbutton;

	private JButton blocknodebutton;

	private JButton unblocknodebutton;

	private JButton blocknodeselectionbutton;

	private JButton unblocknodeselectionbutton;

	private JButton brokersfallapartbutton;

	private JButton iconifywindowsbutton;

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

	private HashSet<JFrame> windows = new HashSet<JFrame>();

	//
	// private boolean toolbar = true;
	//
	// private JFrame controlframe;

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

				JPanel mainpanel = new JPanel();
				mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.Y_AXIS));

				displayframe.setContentPane(mainpanel);

				// open window maximized
				df_width = displayframe.getGraphicsConfiguration().getDevice().getDisplayMode().getWidth();
				df_height = displayframe.getGraphicsConfiguration().getDevice().getDisplayMode().getHeight();
				// displayframe.setBounds(new Rectangle(df_width, df_height));
				displayframe.setSize(new Dimension(df_width, df_height));
				
				displayframe.addWindowListener(new DFWindowAdapter());

				guip.addMouseListener(mouseclick);
				guip.addMouseMotionListener(mouseclick);

				JPanel buttonrow1panel = new JPanel();
				buttonrow1panel.setLayout(new BoxLayout(buttonrow1panel, BoxLayout.X_AXIS));

				exitbutton = new JButton(exitCmd);
				// exitbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// exitbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				exitbutton.addActionListener(guip);
				buttonrow1panel.add(exitbutton);

				JToolBar.Separator buttonseparator1 = new JToolBar.Separator(new Dimension(100, 0));
				buttonseparator1.setOrientation(JSeparator.VERTICAL);
				buttonrow1panel.add(buttonseparator1);

				controlbutton = new JButton(startSimulationCmd);
				// controlbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// controlbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				controlbutton.setToolTipText("Start Simulation");
				controlbutton.addActionListener(guip);

				cancelbutton = new JButton(cancelCmd);
				// cancelbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// cancelbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				cancelbutton.addActionListener(guip);

				JPanel buttoncolumn1panel = new JPanel();
				buttoncolumn1panel.setLayout(new BoxLayout(buttoncolumn1panel, BoxLayout.Y_AXIS));

				buttoncolumn1panel.add(controlbutton);
				buttoncolumn1panel.add(cancelbutton);

				buttonrow1panel.add(buttoncolumn1panel);

				JToolBar.Separator buttonseparator2 = new JToolBar.Separator(new Dimension(60, 0));
				buttonseparator2.setOrientation(JSeparator.VERTICAL);
				buttonrow1panel.add(buttonseparator2);

				statisticbutton = new JButton(statisticCmd);
				// statisticbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// statisticbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				statisticbutton.setToolTipText("Statistics");
				statisticbutton.addActionListener(guip);

				showinfobutton = new JButton(showInfoCmd);
				showinfobutton.setToolTipText("Show node information");
				showinfobutton.addActionListener(guip);

				JPanel buttoncolumn2panel = new JPanel();
				buttoncolumn2panel.setLayout(new BoxLayout(buttoncolumn2panel, BoxLayout.Y_AXIS));

				buttoncolumn2panel.add(statisticbutton);
				buttoncolumn2panel.add(showinfobutton);

				buttonrow1panel.add(buttoncolumn2panel);

				deleteconnectionbutton = new JButton(deleteConnectionCmd);
				// deleteconnectionbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// deleteconnectionbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				deleteconnectionbutton.setToolTipText("Delete connection");
				deleteconnectionbutton.addActionListener(guip);

				addconnectionbutton = new JButton(addConnectionCmd);
				// addconnectionbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// addconnectionbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				addconnectionbutton.setToolTipText("Add connection");
				addconnectionbutton.addActionListener(guip);

				JPanel buttoncolumn3panel = new JPanel();
				buttoncolumn3panel.setLayout(new BoxLayout(buttoncolumn3panel, BoxLayout.Y_AXIS));

				buttoncolumn3panel.add(deleteconnectionbutton);
				buttoncolumn3panel.add(addconnectionbutton);

				buttonrow1panel.add(buttoncolumn3panel);

				blocknodebutton = new JButton(blockCmd);
				// blocknodebutton.setVerticalTextPosition(AbstractButton.CENTER);
				// blocknodebutton.setHorizontalTextPosition(AbstractButton.LEADING);
				blocknodebutton.setToolTipText("Block node");
				blocknodebutton.addActionListener(guip);

				unblocknodebutton = new JButton(unblockCmd);
				// unblocknodebutton.setVerticalTextPosition(AbstractButton.CENTER);
				// unblocknodebutton.setHorizontalTextPosition(AbstractButton.LEADING);
				unblocknodebutton.setToolTipText("Unblock node");
				unblocknodebutton.addActionListener(guip);

				JPanel buttoncolumn4panel = new JPanel();
				buttoncolumn4panel.setLayout(new BoxLayout(buttoncolumn4panel, BoxLayout.Y_AXIS));

				buttoncolumn4panel.add(blocknodebutton);
				buttoncolumn4panel.add(unblocknodebutton);

				buttonrow1panel.add(buttoncolumn4panel);

				blocknodeselectionbutton = new JButton(blockSelectionCmd);
				// blocknodeselectionbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// blocknodeselectionbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				blocknodeselectionbutton.setToolTipText("Block node-selection");
				blocknodeselectionbutton.addActionListener(guip);

				unblocknodeselectionbutton = new JButton(unblockSelectionCmd);
				// unblocknodeselectionbutton.setVerticalTextPosition(AbstractButton.CENTER);
				// unblocknodeselectionbutton.setHorizontalTextPosition(AbstractButton.LEADING);
				unblocknodeselectionbutton.setToolTipText("Unblock node-selection");
				unblocknodeselectionbutton.addActionListener(guip);

				JPanel buttoncolumn5panel = new JPanel();
				buttoncolumn5panel.setLayout(new BoxLayout(buttoncolumn5panel, BoxLayout.Y_AXIS));

				buttoncolumn5panel.add(blocknodeselectionbutton);
				buttoncolumn5panel.add(unblocknodeselectionbutton);

				buttonrow1panel.add(buttoncolumn5panel);

				brokersfallapartbutton = new JButton(brokersFallApartCmd);
				brokersfallapartbutton.setToolTipText("Brokers fall apart");
				brokersfallapartbutton.addActionListener(guip);

				iconifywindowsbutton = new JButton(iconifywindowsCmd);
				iconifywindowsbutton.setToolTipText(iconifywindowsTT);
				iconifywindowsbutton.addActionListener(guip);

				JPanel buttoncolumn6panel = new JPanel();
				buttoncolumn6panel.setLayout(new BoxLayout(buttoncolumn6panel, BoxLayout.Y_AXIS));

				buttoncolumn6panel.add(brokersfallapartbutton);
				buttoncolumn6panel.add(iconifywindowsbutton);

				buttonrow1panel.add(buttoncolumn6panel);

				// put buttons in a logical group
				buttongroup1.put(addconnectionbutton, addConnectionCmd);
				buttongroup1.put(deleteconnectionbutton, deleteConnectionCmd);
				buttongroup1.put(blocknodebutton, blockCmd);
				buttongroup1.put(unblocknodebutton, unblockCmd);
				buttongroup1.put(blocknodeselectionbutton, blockSelectionCmd);
				buttongroup1.put(unblocknodeselectionbutton, unblockSelectionCmd);
				buttongroup1.put(brokersfallapartbutton, brokersFallApartCmd);
				buttongroup1.put(showinfobutton, showInfoCmd);

				JPanel buttonpanel = new JPanel();
				buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.Y_AXIS));
				buttonpanel.setBorder(BorderFactory.createEtchedBorder());
				buttonpanel.add(buttonrow1panel);

				// for future use
				JPanel buttonrow2panel = new JPanel();
				buttonrow2panel.setLayout(new BoxLayout(buttonrow2panel, BoxLayout.X_AXIS));

				JPanel guipanel = new JPanel();
				guipanel.setLayout(new BoxLayout(guipanel, BoxLayout.X_AXIS));
				guipanel.add(guip);

				JSplitPane splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buttonpanel, guipanel);
				splitpane.setOneTouchExpandable(true);
				splitpane.setResizeWeight(0);

				displayframe.add(splitpane);

				// displayframe.getContentPane().add(buttonpanel);
				// // displayframe.getContentPane().add(new JSeparator());
				// // guip.setSize(new Dimension(df_width,df_height-50));
				// displayframe.getContentPane().add(guipanel);

				displayframe.setExtendedState(Frame.MAXIMIZED_HORIZ);
				displayframe.setVisible(true);
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
		return displayareaXPos;
	}

	public int guiYPos() {
		return displayareaYPos;
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
		displayareaXPos = getLocationOnScreen().x;
		displayareaYPos = getLocationOnScreen().y;
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

			if ( JOptionPane.showConfirmDialog(displayframe, "Really exit?", "Please confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION )
				System.exit(0);

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

		} else if ( e.getActionCommand().equals(iconifywindowsCmd) ) {

			iconifyWindows();
			iconifywindowsbutton.setText(deiconifywindowsCmd);
			iconifywindowsbutton.setToolTipText(deiconifywindowsTT);

		} else if ( e.getActionCommand().equals(deiconifywindowsCmd) ) {

			deiconifyWindows();
			iconifywindowsbutton.setText(iconifywindowsCmd);
			iconifywindowsbutton.setToolTipText(iconifywindowsTT);

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

	protected void iconifyWindows() {

		synchronized ( windows ) {
			Iterator<JFrame> it = windows.iterator();
			while ( it.hasNext() )
				it.next().setExtendedState(Frame.ICONIFIED);
		}

	}

	protected void deiconifyWindows() {

		synchronized ( windows ) {
			Iterator<JFrame> it = windows.iterator();
			while ( it.hasNext() )
				it.next().setExtendedState(Frame.NORMAL);
		}

	}

	public void addWindow(JFrame window) {
		synchronized ( windows ) {
			windows.add(window);
		}
	}

	public void removeWindow(JFrame window) {
		synchronized ( windows ) {
			windows.remove(window);
		}
	}
	//
}
