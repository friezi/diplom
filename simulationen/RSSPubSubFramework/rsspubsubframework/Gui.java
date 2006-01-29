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
import java.awt.*;
import java.awt.event.*;

/**
 * Gui object for the simulation.
 * 
 * This object handles actual drawing of the user interface.
 */
class Gui extends javax.swing.JComponent implements ActionListener {
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

	private int cf_xpos = 0;

	private int cf_ypos = 28;

	private static String startSimulationCmd = "Start Simulation";

	private static String stopSimulationCmd = "Stop Simulation";

	private static String continueSimulationCmd = "Continue Simulation";

	private static String runningTxt = "running ...";

	private static String statisticCmd = "Statistics";

	private JButton controlbutton;

	private JButton statisticbutton;

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
		}
	}
}
