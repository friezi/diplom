package de.zintel.diplom.gui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;

import de.zintel.diplom.gnuplot.AbstractGnuplotDataProvider;
import de.zintel.diplom.simulation.Engine;

/**
 * 
 */

/**
 * @author Friedemann Zintel
 * 
 */
@SuppressWarnings("serial")
public class TotTempReqGraphViewWindow extends GraphViewWindow {

	/**
	 * @param dataprovider
	 * @param titleText
	 * @param timeLimit
	 * @throws HeadlessException
	 */
	public TotTempReqGraphViewWindow(AbstractGnuplotDataProvider dataprovider, String titleText, int timeLimit) throws HeadlessException {
		super(dataprovider, titleText, timeLimit, 100, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see GraphViewWindow#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.green);

		int queue = Engine.getSingleton().getParams().getServerQueueSize();

		g.drawLine(xOrig, yOrig - (dimY * queue / yExt), xOrig + dimX, yOrig - (dimY * queue / yExt));

	}

}
