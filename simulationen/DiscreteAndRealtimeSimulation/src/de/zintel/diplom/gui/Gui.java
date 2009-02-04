package de.zintel.diplom.gui;
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.event.MouseInputAdapter;

import de.zintel.diplom.graphics.GORectangle;
import de.zintel.diplom.rps.broker.BrokerType;
import de.zintel.diplom.rps.pubsub.PubSubType;
import de.zintel.diplom.rps.server.RSSServerType;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;

/**
 * Gui object for the simulation.
 * 
 * This object handles actual drawing of the user interface.
 */
public class Gui extends javax.swing.JComponent implements ActionListener, ItemListener {

	protected class StartRecordingIcon implements Icon {

		@SuppressWarnings("serial")
		protected class Triangle extends Polygon {

			public Triangle(int x, int y) {

				addPoint(x, y);
				addPoint(x + 10, y + 4);
				addPoint(x, y + 8);
			}

		}

		public void paintIcon(Component component, Graphics g, int x, int y) {

			g.setColor(Color.black);
			g.fillPolygon(new Triangle(x, y));
			g.setColor(Color.red);
			g.fillOval(x + 12, y, 8, 8);

		}

		public int getIconWidth() {
			return 20;
		}

		public int getIconHeight() {
			return 10;
		}

	}

	protected class StopRecordingIcon implements Icon {

		public void paintIcon(Component component, Graphics g, int x, int y) {

			g.setColor(Color.black);
			g.fillRect(x + 6, y, 8, 8);

		}

		public int getIconWidth() {
			return 20;
		}

		public int getIconHeight() {
			return 10;
		}

	}

	protected class MouseClick extends MouseInputAdapter {

		Node firstnode = null;

		Node secondnode = null;

		Point initialPoint = null;

		GORectangle rectangle = new GORectangle(0, 0, 0, 0);

		@Override
		public void mouseDragged(MouseEvent event) {

			if ( choice_status == SELECTION ) {

				if ( choice == BLOCK_NODE_SELECTION_CMD || choice == UNBLOCK_NODE_SELECTION_CMD ) {

					int x = Engine.deScaleX(event.getPoint().x);
					int y = Engine.deScaleY(event.getPoint().y);

					if ( x > initialPoint.x )
						rectangle.setX2(x);
					else
						rectangle.setX1(x);

					if ( y > initialPoint.y )
						rectangle.setY2(y);
					else
						rectangle.setY1(y);

				}
			}

		}

		@Override
		public void mousePressed(MouseEvent event) {

			if ( choice_status == SELECTION ) {

				if ( choice == BLOCK_NODE_SELECTION_CMD || choice == UNBLOCK_NODE_SELECTION_CMD ) {

					initialPoint = new Point(Engine.deScaleX(event.getPoint().x), Engine.deScaleY(event.getPoint().y));

					rectangle.undisplay();
					(rectangle = new GORectangle(initialPoint)).display();
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

							registeratnodebutton.setText(selectSecondNodeTxt);

						} else if ( choice == DEL_CONN_CMD ) {

							unregisterfromnodebutton.setText(selectSecondNodeTxt);

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

							registeratnodebutton.setText(registerCmd);
							registeratnodebutton.setEnabled(true);

						} else if ( choice == DEL_CONN_CMD ) {

							unregisterfromnodebutton.setText(unregisterCmd);
							unregisterfromnodebutton.setEnabled(true);

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

	protected class DFWindowAdapter extends WindowAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.WindowAdapter#windowDeiconified(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowDeiconified(WindowEvent arg0) {
			super.windowDeiconified(arg0);
			deiconifyWindows();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.WindowAdapter#windowIconified(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowIconified(WindowEvent arg0) {
			iconifyWindows();
			super.windowIconified(arg0);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowClosing(WindowEvent arg0) {

			super.windowClosing(arg0);

			Engine.getSingleton().setPrompting(true);

			for ( JFrame window : Engine.getSingleton().getWindowsSet() )
				window.setAlwaysOnTop(false);

			if ( JOptionPane.showConfirmDialog(displayframe, "Really exit?", "Please confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ) {

				if ( Engine.getSingleton().isInteractiveMode() == true )
					Engine.getSingleton().setExit(true);
				else
					System.exit(0);
			}

			for ( JFrame window : Engine.getSingleton().getWindowsSet() )
				window.setAlwaysOnTop(true);

			Engine.getSingleton().setPrompting(false);
		}

	}

	private class ManualRecordingObserver implements Observer {

		public void update(Observable arg0, Object arg1) {

			Boolean value = (Boolean) arg1;
			// this produces somehow a deadlock
			// if ( value == false )
			// recordingbutton.setEnabled(false);
			// else
			// recordingbutton.setEnabled(true);

		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6099692865286022422L;

	SimParameters params;

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

	private static final String startSimulationCmd = "Start";

	private static final String startTTtext = "Start Simulation";

	private static final String stopSimulationCmd = "Stop";

	private static final String stopTTtext = "Stop simulation";

	private static final String continueSimulationCmd = "Continue";

	private static final String continueTTtext = "Continue simulation";

	private static final String runningTxt = "running ...";

	private static final String statisticCmd = "Stat.";

	private static final String statisticTTtext = "Show general statistic";

	private static final String hideEdgeIdCmd = "HE";

	private static final String hideEdgeIdTTtext = "Hide edge-ids";

	private static final String registerAtNodeCmd = "RN";

	private static final String registerCmd = "Register!";

	private static final String registerTTtext = "Register at node";

	private static final String unregisterFromNodeCmd = "URN";

	private static final String unregisterCmd = "Unregister!";

	private static final String unregisterTTtext = "Unregister from node";

	private static final String brokersFallApartCmd = "BF";

	private static final String brokersFallApartTTtext = "Brokers fall apart";

	private static final String showInfoCmd = "SI";

	private static final String showInfoTTtext = "Show node-information";

	private static final String selectNodeToShowCmd = "Select node to show info";

	private static final String blockAllCmd = "Block all!";

	private static final String unblockAllCmd = "Unblock all!";

	private static final String hidewindowsCmd = "HW";

	private static final String hidewindowsTTtext = "Hide windows";

	private static final String showwindowsCmd = "SW";

	private static final String showwindowsTTtext = "Show windows";

	private static final String cancelCmd = "Cancel";

	private static final String selectFirstNodeTxt = "Select first node ...";

	private static final String selectSecondNodeTxt = "Select second node ...";

	private static final String selectBlockNodeTxt = "Select Node to block ...";

	private static final String selectUnblockNodeTxt = "Select Node to unblock ...";

	private static final String selectBlockNodeSelectionTxt = "Select range to block";

	private static final String selectUnblockNodeSelectionTxt = "Select range to unblock";

	private static final String selectBrokerTxt = "Select Broker ...";

	private static final String blockCmd = "BN";

	private static final String blockTTtext = "Block node";

	private static final String unblockCmd = "UBN";

	private static final String unblockTTtext = "Unblock node";

	private static final String blockSelectionCmd = "BNS";

	private static final String blockSelectionTTtext = "Block node-selection";

	private static final String unblockSelectionCmd = "UNS";

	private static final String unblockSelectionTTtext = "Unblock node-selection";

	private static final String visualizationtxt = "VS";

	private static final String visualizationTTtext = "Visualization of simulation";

	private static final String sublayertxt = "SL";

	private static final String sublayerTTtext = "Show/hide sublayer";

	private static final String toplayertxt = "TL";

	private static final String toplayerTTtext = "Show/hide toplayer";

	private static final String rssconnectiontxt = "RC";

	private static final String rssconnectionTTtext = "Show/hide connections to RSS-Server";

	private static final String messagestxt = "DM";

	private static final String messagesTTtext = "Display/hide Messages";

	private static final String messagessubtxt = "MS";

	private static final String messagessubTTtext = "Messages on sublayer";

	private static final String messagestoptxt = "MT";

	private static final String messagestopTTtext = "Messages on toplayer";

	private static final String continuoustimetxt = "CT";

	private static final String continuoustimeTTtext = "Enable/disable continous time";

	private static final String startRecordingCmd = "StartRecording";

	private static final String startRecordingTTtext = "Start gnuplot-recording";

	private static final String stopRecordingCmd = "StopRecording";

	private static final String stopRecordingTTtext = "Stop gnuplot-recording";

	private JButton controlbutton;

	private JButton statisticbutton;

	private JButton showinfobutton;

	private JButton unregisterfromnodebutton;

	private JButton registeratnodebutton;

	private JButton blocknodebutton;

	private JButton unblocknodebutton;

	private JButton blocknodeselectionbutton;

	private JButton unblocknodeselectionbutton;

	private JButton brokersfallapartbutton;

	private JButton iconifywindowsbutton;

	private JButton cancelbutton;

	private JButton recordingbutton;

	private JButton movebutton;

	private JCheckBox visualizationcheckbox;

	private JCheckBox sublayercheckbox;

	private JCheckBox toplayercheckbox;

	private JCheckBox rssserverconnectioncheckbox;

	private JCheckBox messagescheckbox;

	private JCheckBox continuoustimecheckbox;

	private JRadioButton messagessubradiobutton;

	private JRadioButton messagestopradiobutton;

	private ButtonGroup messagesbuttongroup;

	private JProgressBar progressbar;

	private ScrollingTextArea textarea;

	private HashMap<JButton, String> buttongroup1 = new HashMap<JButton, String>();

	private static final int NO_CHOICE = 0;

	private static final int FIRST_CHOICE = 1;

	private static final int SECOND_CHOICE = 2;

	private static final int DO_IT = 3;

	private static final int SELECTION = 4;

	private int choice_status = NO_CHOICE;

	private static final int ADD_CONN_CMD = 0;

	private static final int DEL_CONN_CMD = 1;

	private static final int BLOCK_NODE_CMD = 2;

	private static final int UNBLOCK_NODE_CMD = 3;

	private static final int BLOCK_NODE_SELECTION_CMD = 4;

	private static final int UNBLOCK_NODE_SELECTION_CMD = 5;

	private static final int SHOW_INFO_CMD = 6;

	private int choice = ADD_CONN_CMD;

	//
	// private boolean toolbar = true;
	//
	// private JFrame controlframe;

	private StatisticWindow statisticframe;

	private JFrame displayframe;

	private MouseClick mouseclick = new MouseClick();

	private ManualRecordingObserver manualRecordingObserver = new ManualRecordingObserver();

	/**
	 * Create the gui window.
	 */
	public Gui(String windowtitle, SimParameters params) {

		this.params = params;
		final Gui guip = this;
		final String title = windowtitle;
		// javax.swing.SwingUtilities.invokeLater(new Runnable() {
		// public void run() {

		JFrame.setDefaultLookAndFeelDecorated(true);

		displayframe = new JFrame(title);
		displayframe.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

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

		JToolBar.Separator buttonseparator1 = new JToolBar.Separator(new Dimension(20, 0));
		buttonseparator1.setOrientation(JSeparator.VERTICAL);
		buttonrow1panel.add(buttonseparator1);

		controlbutton = new JButton(startSimulationCmd);
		// controlbutton.setVerticalTextPosition(AbstractButton.CENTER);
		// controlbutton.setHorizontalTextPosition(AbstractButton.LEADING);
		controlbutton.setToolTipText(startTTtext);
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

		JToolBar.Separator buttonseparatorrec = new JToolBar.Separator(new Dimension(10, 0));
		buttonseparatorrec.setOrientation(JSeparator.VERTICAL);
		buttonrow1panel.add(buttonseparatorrec);

		JPanel buttoncolumn8panel = new JPanel();
		buttoncolumn8panel.setLayout(new BoxLayout(buttoncolumn8panel, BoxLayout.Y_AXIS));

		recordingbutton = new JButton(new StartRecordingIcon());
		recordingbutton.setToolTipText(startRecordingTTtext);
		recordingbutton.setActionCommand(startRecordingCmd);
		recordingbutton.addActionListener(guip);
		Engine.getSingleton().addManualRecordingObserver(manualRecordingObserver);
		manualRecordingObserver.update(Engine.getSingleton().getManualRecordingNotifier(), Engine.getSingleton().isManualRecording());

		buttoncolumn8panel.add(recordingbutton);
		buttonrow1panel.add(buttoncolumn8panel);
		//		
		// movebutton=new JButton(new ImageIcon("move.png"));
		//		
		// buttonrow1panel.add(movebutton);

		JToolBar.Separator buttonseparator2 = new JToolBar.Separator(new Dimension(10, 0));
		buttonseparator2.setOrientation(JSeparator.VERTICAL);
		buttonrow1panel.add(buttonseparator2);

		statisticbutton = new JButton(statisticCmd);
		// statisticbutton.setVerticalTextPosition(AbstractButton.CENTER);
		// statisticbutton.setHorizontalTextPosition(AbstractButton.LEADING);
		statisticbutton.setToolTipText(statisticTTtext);
		statisticbutton.addActionListener(guip);

		showinfobutton = new JButton(showInfoCmd);
		showinfobutton.setToolTipText(showInfoTTtext);
		showinfobutton.addActionListener(guip);

		JPanel buttoncolumn2panel = new JPanel();
		buttoncolumn2panel.setLayout(new BoxLayout(buttoncolumn2panel, BoxLayout.Y_AXIS));

		buttoncolumn2panel.add(statisticbutton);
		buttoncolumn2panel.add(showinfobutton);

		buttonrow1panel.add(buttoncolumn2panel);

		registeratnodebutton = new JButton(registerAtNodeCmd);
		// registeratnodebutton.setVerticalTextPosition(AbstractButton.CENTER);
		// registeratnodebutton.setHorizontalTextPosition(AbstractButton.LEADING);
		registeratnodebutton.setToolTipText(registerTTtext);
		registeratnodebutton.addActionListener(guip);

		unregisterfromnodebutton = new JButton(unregisterFromNodeCmd);
		// unregisterfromnodebutton.setVerticalTextPosition(AbstractButton.CENTER);
		// unregisterfromnodebutton.setHorizontalTextPosition(AbstractButton.LEADING);
		unregisterfromnodebutton.setToolTipText(unregisterTTtext);
		unregisterfromnodebutton.addActionListener(guip);

		JPanel buttoncolumn3panel = new JPanel();
		buttoncolumn3panel.setLayout(new BoxLayout(buttoncolumn3panel, BoxLayout.Y_AXIS));

		buttoncolumn3panel.add(registeratnodebutton);
		buttoncolumn3panel.add(unregisterfromnodebutton);

		buttonrow1panel.add(buttoncolumn3panel);

		blocknodebutton = new JButton(blockCmd);
		// blocknodebutton.setVerticalTextPosition(AbstractButton.CENTER);
		// blocknodebutton.setHorizontalTextPosition(AbstractButton.LEADING);
		blocknodebutton.setToolTipText(blockTTtext);
		blocknodebutton.addActionListener(guip);

		unblocknodebutton = new JButton(unblockCmd);
		// unblocknodebutton.setVerticalTextPosition(AbstractButton.CENTER);
		// unblocknodebutton.setHorizontalTextPosition(AbstractButton.LEADING);
		unblocknodebutton.setToolTipText(unblockTTtext);
		unblocknodebutton.addActionListener(guip);

		JPanel buttoncolumn4panel = new JPanel();
		buttoncolumn4panel.setLayout(new BoxLayout(buttoncolumn4panel, BoxLayout.Y_AXIS));

		buttoncolumn4panel.add(blocknodebutton);
		buttoncolumn4panel.add(unblocknodebutton);

		buttonrow1panel.add(buttoncolumn4panel);

		blocknodeselectionbutton = new JButton(blockSelectionCmd);
		// blocknodeselectionbutton.setVerticalTextPosition(AbstractButton.CENTER);
		// blocknodeselectionbutton.setHorizontalTextPosition(AbstractButton.LEADING);
		blocknodeselectionbutton.setToolTipText(blockSelectionTTtext);
		blocknodeselectionbutton.addActionListener(guip);

		unblocknodeselectionbutton = new JButton(unblockSelectionCmd);
		// unblocknodeselectionbutton.setVerticalTextPosition(AbstractButton.CENTER);
		// unblocknodeselectionbutton.setHorizontalTextPosition(AbstractButton.LEADING);
		unblocknodeselectionbutton.setToolTipText(unblockSelectionTTtext);
		unblocknodeselectionbutton.addActionListener(guip);

		JPanel buttoncolumn5panel = new JPanel();
		buttoncolumn5panel.setLayout(new BoxLayout(buttoncolumn5panel, BoxLayout.Y_AXIS));

		buttoncolumn5panel.add(blocknodeselectionbutton);
		buttoncolumn5panel.add(unblocknodeselectionbutton);

		buttonrow1panel.add(buttoncolumn5panel);

		brokersfallapartbutton = new JButton(brokersFallApartCmd);
		brokersfallapartbutton.setToolTipText(brokersFallApartTTtext);
		brokersfallapartbutton.addActionListener(guip);

		iconifywindowsbutton = new JButton(hidewindowsCmd);
		iconifywindowsbutton.setToolTipText(hidewindowsTTtext);
		iconifywindowsbutton.addActionListener(guip);

		JPanel buttoncolumn6panel = new JPanel();
		buttoncolumn6panel.setLayout(new BoxLayout(buttoncolumn6panel, BoxLayout.Y_AXIS));

		buttoncolumn6panel.add(brokersfallapartbutton);
		buttoncolumn6panel.add(iconifywindowsbutton);

		buttonrow1panel.add(buttoncolumn6panel);

		JPanel rbuttonpanel1 = new JPanel();
		rbuttonpanel1.setLayout(new BoxLayout(rbuttonpanel1, BoxLayout.Y_AXIS));

		messagessubradiobutton = new JRadioButton(messagessubtxt);
		messagessubradiobutton.setSelected(!Engine.getSingleton().isMessagesOnTopLayer());
		messagessubradiobutton.setToolTipText(messagessubTTtext);
		messagessubradiobutton.addActionListener(guip);

		messagestopradiobutton = new JRadioButton(messagestoptxt);
		messagestopradiobutton.setSelected(Engine.getSingleton().isMessagesOnTopLayer());
		messagestopradiobutton.setToolTipText(messagestopTTtext);
		messagestopradiobutton.addActionListener(guip);

		messagescheckbox = new JCheckBox(messagestxt);
		messagescheckbox.setSelected(Engine.getSingleton().isShowMessages());
		messagescheckbox.setToolTipText(messagesTTtext);
		messagescheckbox.addItemListener(guip);

		continuoustimecheckbox = new JCheckBox(continuoustimetxt);
		continuoustimecheckbox.setSelected(Engine.getSingleton().isContinuousTime());
		continuoustimecheckbox.setToolTipText(continuoustimeTTtext);
		continuoustimecheckbox.addItemListener(guip);
		if ( params.isDiscreteSimulation() == false )
			continuoustimecheckbox.setEnabled(false);

		rbuttonpanel1.add(messagessubradiobutton);
		rbuttonpanel1.add(messagestopradiobutton);
		rbuttonpanel1.add(messagescheckbox);
		rbuttonpanel1.add(continuoustimecheckbox);

		messagesbuttongroup = new ButtonGroup();
		messagesbuttongroup.add(messagessubradiobutton);
		messagesbuttongroup.add(messagestopradiobutton);

		buttonrow1panel.add(rbuttonpanel1);

		JPanel buttoncolumn7panel = new JPanel();
		buttoncolumn7panel.setLayout(new BoxLayout(buttoncolumn7panel, BoxLayout.Y_AXIS));

		visualizationcheckbox = new JCheckBox(visualizationtxt);
		visualizationcheckbox.setSelected(Engine.getSingleton().isVisualization());
		visualizationcheckbox.setToolTipText(visualizationTTtext);
		visualizationcheckbox.addItemListener(guip);

		sublayercheckbox = new JCheckBox(sublayertxt);
		sublayercheckbox.setSelected(Engine.getSingleton().isShowSubLayer());
		sublayercheckbox.setToolTipText(sublayerTTtext);
		sublayercheckbox.addItemListener(guip);

		toplayercheckbox = new JCheckBox(toplayertxt);
		toplayercheckbox.setSelected(Engine.getSingleton().isShowTopLayer());
		toplayercheckbox.setToolTipText(toplayerTTtext);
		toplayercheckbox.addItemListener(guip);

		rssserverconnectioncheckbox = new JCheckBox(rssconnectiontxt);
		rssserverconnectioncheckbox.setSelected(Engine.getSingleton().isShowRSSConnection());
		rssserverconnectioncheckbox.setToolTipText(rssconnectionTTtext);
		rssserverconnectioncheckbox.addItemListener(guip);

		buttoncolumn7panel.add(visualizationcheckbox);
		buttoncolumn7panel.add(sublayercheckbox);
		buttoncolumn7panel.add(toplayercheckbox);
		buttoncolumn7panel.add(rssserverconnectioncheckbox);

		buttonrow1panel.add(buttoncolumn7panel);

		// put buttons in a logical group
		buttongroup1.put(registeratnodebutton, registerAtNodeCmd);
		buttongroup1.put(unregisterfromnodebutton, unregisterFromNodeCmd);
		buttongroup1.put(blocknodebutton, blockCmd);
		buttongroup1.put(unblocknodebutton, unblockCmd);
		buttongroup1.put(blocknodeselectionbutton, blockSelectionCmd);
		buttongroup1.put(unblocknodeselectionbutton, unblockSelectionCmd);
		buttongroup1.put(brokersfallapartbutton, brokersFallApartCmd);
		buttongroup1.put(showinfobutton, showInfoCmd);

		// text-output
		textarea = new ScrollingTextArea();
		textarea.setEditable(false);
		textarea.setBorder(BorderFactory.createLineBorder(Color.black));
		Engine.getSingleton().getInfoOutput().setOutput(textarea);

		buttonrow1panel.add(textarea.getScrollpane());

		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.Y_AXIS));
		buttonpanel.setBorder(BorderFactory.createEtchedBorder());
		buttonpanel.add(buttonrow1panel);

		JPanel buttonrow2panel = new JPanel();
		buttonrow2panel.setLayout(new BoxLayout(buttonrow2panel, BoxLayout.X_AXIS));

		progressbar = new JProgressBar(JProgressBar.HORIZONTAL);
		buttonrow2panel.add(progressbar);
		Engine.getSingleton().setProgressBar(new GUIProgressBar(progressbar));

		buttonpanel.add(buttonrow2panel);

		JPanel guipanel = new JPanel();
		guipanel.setLayout(new BoxLayout(guipanel, BoxLayout.X_AXIS));
		guipanel.setBorder(BorderFactory.createEtchedBorder());
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
		// }
		// });
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

		} else if ( e.getActionCommand().equals(startRecordingCmd) ) {

			recordingbutton.setIcon(new StopRecordingIcon());
			recordingbutton.setActionCommand(stopRecordingCmd);
			recordingbutton.setToolTipText(stopRecordingTTtext);

			Engine.getSingleton().getRpsStatistics().startGnuplotRecording();

		} else if ( e.getActionCommand().equals(stopRecordingCmd) ) {

			recordingbutton.setIcon(new StartRecordingIcon());
			recordingbutton.setActionCommand(startRecordingCmd);
			recordingbutton.setToolTipText(startRecordingTTtext);

			Engine.getSingleton().getRpsStatistics().stopGnuplotRecording();

		} else if ( e.getActionCommand().equals(statisticCmd) ) {

			statisticframe = new StatisticWindow("Statistics");
			statisticframe.setVisible(true);

		} else if ( e.getActionCommand().equals(showInfoCmd) ) {

			choice = SHOW_INFO_CMD;
			showinfobutton.setText(selectNodeToShowCmd);
			disableGroup(buttongroup1);
			choice_status = FIRST_CHOICE;

		} else if ( e.getActionCommand().equals(unregisterFromNodeCmd) ) {

			choice = DEL_CONN_CMD;
			unregisterfromnodebutton.setText(selectFirstNodeTxt);
			disableGroup(buttongroup1);
			choice_status = FIRST_CHOICE;

		} else if ( e.getActionCommand().equals(unregisterCmd) ) {

			choice = NO_CHOICE;
			mouseclick.firstnode.setDefaultColor();
			mouseclick.secondnode.setDefaultColor();
			enableGroup(buttongroup1);
			// Engine.getSingleton().removeEdgeFromNodes(mouseclick.firstnode,
			// mouseclick.secondnode);
			processUnregister(mouseclick.firstnode, mouseclick.secondnode);

		} else if ( e.getActionCommand().equals(registerAtNodeCmd) ) {

			choice = ADD_CONN_CMD;
			registeratnodebutton.setText(selectFirstNodeTxt);
			disableGroup(buttongroup1);
			choice_status = FIRST_CHOICE;

		} else if ( e.getActionCommand().equals(registerCmd) ) {

			choice = NO_CHOICE;
			mouseclick.firstnode.setDefaultColor();
			mouseclick.secondnode.setDefaultColor();
			enableGroup(buttongroup1);
			// Engine.getSingleton().addEdge(mouseclick.firstnode,
			// mouseclick.secondnode);
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

		} else if ( e.getActionCommand().equals(hidewindowsCmd) ) {

			iconifyWindows();
			iconifywindowsbutton.setText(showwindowsCmd);
			iconifywindowsbutton.setToolTipText(showwindowsTTtext);

		} else if ( e.getActionCommand().equals(showwindowsCmd) ) {

			deiconifyWindows();
			iconifywindowsbutton.setText(hidewindowsCmd);
			iconifywindowsbutton.setToolTipText(hidewindowsTTtext);

		} else if ( e.getActionCommand().equals(brokersFallApartCmd) ) {

			synchronized ( Engine.getSingleton().nodeList ) {
				for ( Node node : Engine.getSingleton().nodeList )
					if ( node instanceof BrokerType )
						((BrokerType) node).callbackUnregisterFromAllBrokers();
			}

		} else if ( e.getActionCommand().equals(messagessubtxt) ) {

			Engine.getSingleton().setMessagesOnTopLayer(false);

		} else if ( e.getActionCommand().equals(messagestoptxt) ) {

			Engine.getSingleton().setMessagesOnTopLayer(true);

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

		HashSet<JFrame> windows = Engine.getSingleton().getWindowsSet();

		synchronized ( windows ) {
			Iterator<JFrame> it = windows.iterator();
			while ( it.hasNext() )
				// it.next().setExtendedState(Frame.ICONIFIED);
				it.next().setVisible(false);
		}

	}

	protected void deiconifyWindows() {

		HashSet<JFrame> windows = Engine.getSingleton().getWindowsSet();

		synchronized ( windows ) {
			Iterator<JFrame> it = windows.iterator();
			while ( it.hasNext() )
				// it.next().setExtendedState(Frame.NORMAL);
				it.next().setVisible(true);
		}

	}

	public void itemStateChanged(ItemEvent event) {

		Object source = event.getItemSelectable();

		if ( source == visualizationcheckbox ) {

			if ( event.getStateChange() == ItemEvent.DESELECTED )
				Engine.getSingleton().setVisualization(false);
			else if ( event.getStateChange() == ItemEvent.SELECTED )
				Engine.getSingleton().setVisualization(true);

		} else if ( source == sublayercheckbox ) {

			if ( event.getStateChange() == ItemEvent.DESELECTED )
				Engine.getSingleton().setShowSubLayer(false);
			else if ( event.getStateChange() == ItemEvent.SELECTED )
				Engine.getSingleton().setShowSubLayer(true);

		} else if ( source == toplayercheckbox ) {

			if ( event.getStateChange() == ItemEvent.DESELECTED )
				Engine.getSingleton().setShowTopLayer(false);
			else if ( event.getStateChange() == ItemEvent.SELECTED )
				Engine.getSingleton().setShowTopLayer(true);

		} else if ( source == messagescheckbox ) {

			if ( event.getStateChange() == ItemEvent.DESELECTED )
				Engine.getSingleton().setShowMessages(false);
			else if ( event.getStateChange() == ItemEvent.SELECTED )
				Engine.getSingleton().setShowMessages(true);

		} else if ( source == continuoustimecheckbox ) {

			if ( event.getStateChange() == ItemEvent.DESELECTED )
				Engine.getSingleton().setContinuousTime(false);
			else if ( event.getStateChange() == ItemEvent.SELECTED )
				Engine.getSingleton().setContinuousTime(true);

		} else if ( source == rssserverconnectioncheckbox ) {

			if ( event.getStateChange() == ItemEvent.DESELECTED )
				Engine.getSingleton().setShowRSSConnection(false);
			else if ( event.getStateChange() == ItemEvent.SELECTED )
				Engine.getSingleton().setShowRSSConnection(true);

		}

	}

	public void setRecordingEnabled(boolean value) {
		recordingbutton.setEnabled(value);
	}
}
