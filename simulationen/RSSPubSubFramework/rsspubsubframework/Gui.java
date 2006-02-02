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

			Point point = new Point(event.getPoint().x - WINDOW_LEFT_BORDER_SIZE, event.getPoint().y
					- WINDOW_TOP_BORDER_SIZE);

			if ( choice_status == FIRST_CHOICE ) {
				// check if we hit a Node
				if ( (firstnode = Engine.getSingleton().findNode(point)) != null ) {
					if ( choice == ADD_CONN_CMD ) {

						addconnectionbutton.setText(chooseSecondNodeTxt);

					} else if ( choice == DEL_CONN_CMD ) {

						deleteconnectionbutton.setText(chooseSecondNodeTxt);

					}
					choice_status = SECOND_CHOICE;
					firstnode.setColor(Color.red);
				} else {

					choice_status = NO_CHOICE;
					deleteconnectionbutton.setText(deleteConnectionCmd);
					deleteconnectionbutton.setEnabled(true);
					addconnectionbutton.setText(addConnectionCmd);
					addconnectionbutton.setEnabled(true);

				}

			} else if ( choice_status == SECOND_CHOICE ) {

				if ( (secondnode = Engine.getSingleton().findNode(point)) != null ) {
					if ( choice == ADD_CONN_CMD ) {

						addconnectionbutton.setText(addCmd);
						addconnectionbutton.setEnabled(true);
						deleteconnectionbutton.setText(cancelCmd);
						deleteconnectionbutton.setEnabled(true);

					} else if ( choice == DEL_CONN_CMD ) {

						deleteconnectionbutton.setText(deleteCmd);
						deleteconnectionbutton.setEnabled(true);
						addconnectionbutton.setText(cancelCmd);
						addconnectionbutton.setEnabled(true);

					}
					choice_status = DO_IT;
					secondnode.setColor(Color.red);
				} else {

					choice_status = NO_CHOICE;
					deleteconnectionbutton.setText(deleteConnectionCmd);
					deleteconnectionbutton.setEnabled(true);
					addconnectionbutton.setText(addConnectionCmd);
					addconnectionbutton.setEnabled(true);
					firstnode.setDefaultColor();
				}
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6099692865286022422L;

	// / Width of the drawing pane.
	private int currentWidth;

	// / Height of the drawing pane.
	private int currentHeight;

	private int df_width;

	private int df_height;

	private int cf_xpos = 200;

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

	private static String chooseFirstNodeTxt = "choose first node ...";

	private static String chooseSecondNodeTxt = "choose second node ...";

	private JButton controlbutton;

	private JButton statisticbutton;

	private JButton edgeidbutton;

	private JButton deleteconnectionbutton;

	private JButton addconnectionbutton;

	private static int NO_CHOICE = 0;

	private static int FIRST_CHOICE = 1;

	private static int SECOND_CHOICE = 2;

	private static int DO_IT = 3;

	private int choice_status = NO_CHOICE;

	private static int ADD_CONN_CMD = 0;

	private static int DEL_CONN_CMD = 1;

	private int choice = ADD_CONN_CMD;

	private MouseClick mouseclick = new MouseClick();

	/**
	 * Create the gui window.
	 */
	public Gui() {
		final Gui guip = this;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame.setDefaultLookAndFeelDecorated(true);

				JFrame displayframe = new JFrame("Messaging Network");
				displayframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				displayframe.getContentPane().add(guip);

				// open window maximized
				df_width = displayframe.getGraphicsConfiguration().getDevice().getDisplayMode().getWidth();
				df_height = displayframe.getGraphicsConfiguration().getDevice().getDisplayMode().getHeight();
				displayframe.setBounds(new Rectangle(df_width, df_height));

				displayframe.addMouseListener(mouseclick);

				JFrame controlframe = new JFrame("Controls");
				JPanel buttonpanel = new JPanel();

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

		} else if ( e.getActionCommand().equals(statisticCmd) ) {
			System.out.println("Statistics");
		} else if ( e.getActionCommand().equals(showEdgeIdCmd) ) {
			Edge.setIdOn(true);
			edgeidbutton.setText(hideEdgeIdCmd);
		} else if ( e.getActionCommand().equals(hideEdgeIdCmd) ) {
			Edge.setIdOn(false);
			edgeidbutton.setText(showEdgeIdCmd);
		} else if ( e.getActionCommand().equals(deleteConnectionCmd) ) {
			choice = DEL_CONN_CMD;
			deleteconnectionbutton.setText(chooseFirstNodeTxt);
			deleteconnectionbutton.setEnabled(false);
			addconnectionbutton.setEnabled(false);
			choice_status = FIRST_CHOICE;
		} else if ( e.getActionCommand().equals(addConnectionCmd) ) {
			choice = ADD_CONN_CMD;
			addconnectionbutton.setText(chooseFirstNodeTxt);
			addconnectionbutton.setEnabled(false);
			deleteconnectionbutton.setEnabled(false);
			choice_status = FIRST_CHOICE;
		} else if ( e.getActionCommand().equals(deleteCmd) ) {
			choice = NO_CHOICE;
			mouseclick.firstnode.setDefaultColor();
			mouseclick.secondnode.setDefaultColor();
			addconnectionbutton.setText(addConnectionCmd);
			deleteconnectionbutton.setText(deleteConnectionCmd);
			Engine.getSingleton().removeEdgeFromNodes(mouseclick.firstnode, mouseclick.secondnode);
		} else if ( e.getActionCommand().equals(addCmd) ) {
			choice = NO_CHOICE;
			mouseclick.firstnode.setDefaultColor();
			mouseclick.secondnode.setDefaultColor();
			addconnectionbutton.setText(addConnectionCmd);
			deleteconnectionbutton.setText(deleteConnectionCmd);
			Engine.getSingleton().addEdge(mouseclick.firstnode,mouseclick.secondnode);
		} else if ( e.getActionCommand().equals(cancelCmd) ) {
			choice = NO_CHOICE;
			mouseclick.firstnode.setDefaultColor();
			mouseclick.secondnode.setDefaultColor();
			addconnectionbutton.setText(addConnectionCmd);
			deleteconnectionbutton.setText(deleteConnectionCmd);
		}
	}
}
