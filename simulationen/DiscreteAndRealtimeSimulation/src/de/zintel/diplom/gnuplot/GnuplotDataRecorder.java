package de.zintel.diplom.gnuplot;
import java.io.FileWriter;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import de.zintel.diplom.gui.InfoOutput;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.SimParameters;

/**
 * 
 */

/**
 * <p>
 * For Recording Datas to a file in gnuplot-format. Each
 * SimParams.getGnuplotTimeStepSecs() seconds, the datas will be written in the
 * format:
 * </p>
 * 
 * <pre>
 * 	&lt;time_in_seconds&gt; &lt;data&gt;
 * </pre>
 * 
 * 
 * @author Friedemann Zintel
 * 
 */
public class GnuplotDataRecorder implements Observer {

	protected String filename;

	protected FileWriter file;

	protected SimParameters params;

	protected InfoOutput infoOutput = Engine.getSingleton().getInfoOutput();

	boolean closed = true;

	public GnuplotDataRecorder(String filename, SimParameters params) throws Exception {

		this.params = params;
		this.filename = filename;

		file = new FileWriter(filename);

		closed = false;

		infoOutput.write("Recording started to file " + filename + "\n");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {

		if ( closed != true )
			this.close();
		
		super.finalize();

	}

	public synchronized void close() throws Exception {

		file.close();
		closed = true;
		infoOutput.write("Recording stopped for file " + filename + "\n");

	}

	public synchronized void update(Observable arg0, Object value) {

		AbstractGnuplotDataProvider.GnuplotData data = (AbstractGnuplotDataProvider.GnuplotData) value;

		try {

			file.write(data.x + " " + String.format(Locale.US, "% 9.9f", data.y) + "\n");

		} catch ( Exception e ) {

			System.err.println("GnuplotDataRecorder: Exception for file " + filename + ": " + e);
			e.printStackTrace();
			infoOutput.write("EXCEPTION:\n" + e + "\n");

		}

	}

}