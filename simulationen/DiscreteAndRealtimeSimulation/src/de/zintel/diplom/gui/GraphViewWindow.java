package de.zintel.diplom.gui;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.zintel.diplom.gnuplot.AbstractGnuplotDataProvider;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.tools.IntRingBuffer;

/**
 * 
 */

/**
 * 
 * Shows Datas provided by an AbstractGnuplotDataProvider graphically in a
 * window.
 * 
 * @author Friedemann Zintel
 * 
 */
@SuppressWarnings("serial")
public class GraphViewWindow extends JComponent implements Observer, ChangeListener {

	protected class GVWindowAdapter extends WindowAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowClosed(WindowEvent arg0) {

			removeObserver();

			super.windowClosed(arg0);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.WindowAdapter#windowDeiconified(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowDeiconified(WindowEvent event) {

			paintImmediately(new Rectangle(wDim));

			super.windowDeiconified(event);

		}

	}

	protected class GVComponentAdapter extends ComponentAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ComponentAdapter#componentResized(java.awt.event.ComponentEvent)
		 */
		@Override
		public void componentResized(ComponentEvent arg0) {

			calculateDimensions();

			paintImmediately(new Rectangle(wDim));

			super.componentResized(arg0);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ComponentAdapter#componentShown(java.awt.event.ComponentEvent)
		 */
		@Override
		public void componentShown(ComponentEvent event) {

			paintImmediately(new Rectangle(wDim));

			super.componentShown(event);

		}

	}

	protected InfoWindow infowindow;

	protected AbstractGnuplotDataProvider dataprovider = null;

	protected static final int DFLT_TIMELIMIT = 6000;

	protected int maxPoints;

	protected IntRingBuffer xValues = null;

	protected IntRingBuffer yValues = null;

	protected int nmbPoints = 0;

	// the number of parts the curve will be partitioned in. Will work after
	// exceeding the max ticks
	protected int parts = 3;

	protected int scaleY = 1;

	// the extent in y-direction
	protected int yExt = 100;

	protected boolean fixedHeight;

	protected Boolean paintSem = new Boolean(true);

	protected Dimension wMin = new Dimension(200, 100);

	protected Insets iS = new Insets(5, 5, 12, 50);

	protected Container cpane;

	protected Dimension wDim = new Dimension(0, 0);

	protected Font font = new Font("Helvetica", Font.PLAIN, 9);

	protected JSlider slider = null;

	protected Point topFontPos = new Point(3, 5);

	protected Point bottomFontPos = new Point(0, 10);

	int dimX;

	int dimY;

	// ground-zero of the axis
	int xOrig;

	int yOrig;

	protected SimParameters params;

	/**
	 * @param dataprovider
	 *            provider of the datas
	 * @param titleText
	 *            window-title
	 * @param timeLimit
	 *            in Kticks
	 * @param yRange
	 *            the range for y-axis
	 * @param fixedMaxHeight
	 *            if true, the yRange is also the max. possible height
	 * @throws HeadlessException
	 */
	public GraphViewWindow(AbstractGnuplotDataProvider dataprovider, String titleText, int timeLimit, int yRange, boolean fixedMaxHeight)
			throws HeadlessException {

		params = Engine.getSingleton().getParams();

		this.dataprovider = dataprovider;

		if ( timeLimit == 0 )
			timeLimit = DFLT_TIMELIMIT;

		maxPoints = (int) (timeLimit / params.getGnuplotTimeStepSecs());

		this.fixedHeight = fixedMaxHeight;

		yExt = yRange == 0 ? 1 : yRange;

		xValues = new IntRingBuffer(maxPoints);

		yValues = new IntRingBuffer(maxPoints);

		infowindow = new InfoWindow("Graph: " + titleText);

		infowindow.addWindowListener(new GVWindowAdapter());
		infowindow.addComponentListener(new GVComponentAdapter());

		infowindow.setMinimumSize(wMin);

		cpane = infowindow.getContentPane();

		cpane.setLayout(new BoxLayout(cpane, BoxLayout.X_AXIS));

		slider = new JSlider(JSlider.VERTICAL, 1, fixedMaxHeight == true ? yExt : 2 * yExt, yExt);
		slider.setPreferredSize(new Dimension(slider.getPreferredSize().width, wMin.height));
		slider.addChangeListener(this);

		cpane.add(slider);

		cpane.add(this);

		setPreferredSize(wMin);

		infowindow.pack();

		calculateDimensions();

		setupObserver();

	}

	protected void calculateDimensions() {

		synchronized ( paintSem ) {

			wDim = cpane.getSize();

			dimX = wDim.width - (iS.left + iS.right);
			dimY = wDim.height - (iS.bottom + iS.top);

			xOrig = iS.left;
			yOrig = wDim.height - iS.bottom;

		}

	}

	protected void paintCross(Graphics g) {

		g.setColor(Color.black);

		// x-axis
		g.drawLine(xOrig, yOrig, xOrig + dimX, yOrig);
		// arrows
		g.drawLine(xOrig + dimX, yOrig, xOrig + dimX - 3, yOrig - 2);
		g.drawLine(xOrig + dimX, yOrig, xOrig + dimX - 3, yOrig + 2);

		// y-axis
		g.drawLine(xOrig, yOrig + 2, xOrig, yOrig - dimY);
		// arrows
		g.drawLine(xOrig, yOrig - dimY, xOrig - 2, yOrig - dimY + 2);
		g.drawLine(xOrig, yOrig - dimY, xOrig + 2, yOrig - dimY + 2);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {

		synchronized ( paintSem ) {

			// the entire dimension-values
			Dimension eDim = getSize();

			g.clearRect(0, 0, eDim.width, eDim.height);

			paintCross(g);

			drawEntireCurve(g);

			g.setFont(font);

			g.setColor(Color.darkGray);

			// y-axis
			g.drawString(Integer.toString(yExt), xOrig + topFontPos.x, yOrig - dimY + topFontPos.y);

			// x-axis zero-pos
			if ( xValues.getElements() > 0 )
				g.drawString(Integer.toString(xValues.get(0)), xOrig + bottomFontPos.x, yOrig + bottomFontPos.y);

		}

	}

	public void update(Observable arg0, Object arg1) {

		AbstractGnuplotDataProvider.GnuplotData data = (AbstractGnuplotDataProvider.GnuplotData) arg1;

		int idx;

		synchronized ( paintSem ) {

			if ( nmbPoints > 0 ) {

				if ( nmbPoints >= maxPoints ) {

					// copy right sight to left sight

					int skipIndices = nmbPoints / parts;
					nmbPoints = (int) (nmbPoints * (1 - 1F / parts));

					xValues.skip(skipIndices);
					yValues.skip(skipIndices);

					paintImmediately(new Rectangle(wDim));

				}

			}

			nmbPoints++;

			idx = nmbPoints - 1;

			xValues.enqueue((int) data.x);
			yValues.enqueue((int) (scaleY * data.y));

			Graphics g = getGraphics();

			if ( g != null && xValues.getElements() > 1 )
				drawCurvePart(g, idx - 1, yValues.get(idx - 1), idx, yValues.get(idx));

		}

	}

	protected void drawEntireCurve(Graphics g) {

		if ( xValues.getElements() == 0 )
			return;

		if ( xValues.getElements() == 1 )
			drawCurvePart(g, 0, yValues.get(0), 0, yValues.get(0));

		else
			for ( int i = 1; i < nmbPoints; i++ )
				drawCurvePart(g, i - 1, yValues.get(i - 1), i, yValues.get(i));

	}

	protected void drawCurvePart(Graphics g, int x1, int y1, int x2, int y2) {

		g.setColor(Color.red);

		// this does the appropriate scaling
		g.drawLine(xOrig + dimX * x1 / maxPoints, yOrig - (dimY * y1 / yExt), xOrig + dimX * x2 / maxPoints, yOrig - (dimY * y2 / yExt));

	}

	protected void setupObserver() {
		dataprovider.addObserver(this);
	}

	protected void removeObserver() {
		dataprovider.deleteObserver(this);
	}

	public void stateChanged(ChangeEvent e) {

		if ( e.getSource() == slider ) {

			synchronized ( paintSem ) {

				yExt = slider.getValue();

				if ( fixedHeight == false ) {

					slider.setMaximum(Integer.MAX_VALUE / 2 < yExt ? yExt : 2 * yExt);
					slider.setValue(yExt);

				}

				paintImmediately(new Rectangle(wDim));

			}

		}

	}

}
