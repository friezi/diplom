package de.zintel.diplom.gnuplot;
/**
 * 
 */

import java.io.*;

import de.zintel.diplom.gui.InfoOutput;
import de.zintel.diplom.handler.ActionHandler;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.Message;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.simulation.VirtualNode;

/**
 * This class will print arrows and labels in the file "markers.gnuplot"
 * 
 * @author Friedemann Zintel
 * 
 */
public class GnuplotMarkerWriter extends VirtualNode {

	SimParameters params;

	private boolean recording = false;

	private String filename = "";

	private FileWriter file;

	private final int y = -6;

	private InfoOutput infoOutput = Engine.getSingleton().getInfoOutput();

	public GnuplotMarkerWriter(SimParameters params) {

		this.params = params;
		this.filename = params.getGnuplotFileMarkers();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Node#receiveMessage(Message)
	 */
	@Override
	protected void receiveMessage(Message m) {

		if ( filename.equals(SimParameters.NONE) )
			return;

		long time = Engine.getSingleton().getTime() / 1000;

		try {

			if ( m instanceof ActionHandler.StartGnuplotRecordingMessage ) {

				file = new FileWriter(filename);
				infoOutput.write("Recording of actions startet to file " + filename + "\n");
				file.write("set xzeroaxis\n");
				recording = true;

			} else if ( m instanceof ActionHandler.StopGnuplotRecordingMessage ) {

				if ( recording == false ) {

					System.err.println("WARNING: GnuplotMarkerWriter: stopGnuplotRecording without startGnuplotRecording");
					return;

				}

				file.close();
				infoOutput.write("Recording of actions stopped\n");
				recording = false;

			} else if ( m instanceof ActionHandler.SetServiceTimeFactorMessage ) {

				ActionHandler.SetServiceTimeFactorMessage sstfm = (ActionHandler.SetServiceTimeFactorMessage) m;

				if ( recording == false )
					return;

				file.write("set arrow from " + time + "," + y + " to " + time + ",0\n");
				file.write("set label \"ST(" + sstfm.value + ")\" at " + time + "," + (y - 2) + "\n");

			} else if ( m instanceof ActionHandler.StartChurnMessage ) {

				ActionHandler.StartChurnMessage scm = (ActionHandler.StartChurnMessage) m;

				if ( recording == false )
					return;

				file.write("set arrow from " + time + "," + y + " to " + time + ",0\n");
				file.write("set label \"CB(" + scm.percent + "," + scm.timerange + ")\" at " + time + "," + (y - 2) + "\n");

			} else if ( m instanceof ActionHandler.StopChurnMessage ) {

				ActionHandler.StopChurnMessage scm = (ActionHandler.StopChurnMessage) m;

				if ( recording == false )
					return;

				file.write("set arrow from " + time + "," + y + " to " + time + ",0\n");
				file.write("set label \"CE\" at " + time + "," + (y - 2) + "\n");

			} else if ( m instanceof ActionHandler.BlockSubscribersMessage ) {

				ActionHandler.BlockSubscribersMessage bsm = (ActionHandler.BlockSubscribersMessage) m;

				if ( recording == false )
					return;

				file.write("set arrow from " + time + "," + y + " to " + time + ",0\n");
				file.write("set label \"BS(" + bsm.percent + ")\" at " + time + "," + (y - 2) + "\n");

			} else if ( m instanceof ActionHandler.UnblockSubscribersMessage ) {

				if ( recording == false )
					return;

				file.write("set arrow from " + time + "," + y + " to " + time + ",0\n");
				file.write("set label \"US\" at " + time + "," + (y - 2) + "\n");

			} else if ( m instanceof ActionHandler.SubscribersLeaveMessage ) {

				ActionHandler.SubscribersLeaveMessage slm = (ActionHandler.SubscribersLeaveMessage) m;

				if ( recording == false )
					return;

				file.write("set arrow from " + time + "," + y + " to " + time + ",0\n");
				file.write("set label \"SL(" + slm.percent + ")\" at " + time + "," + (y - 2) + "\n");

			} else if ( m instanceof ActionHandler.SubscribersRejoinMessage ) {

				if ( recording == false )
					return;

				file.write("set arrow from " + time + "," + y + " to " + time + ",0\n");
				file.write("set label \"SJ\" at " + time + "," + (y - 2) + "\n");

			} else if ( m instanceof ActionHandler.JoinBeginMessage ) {

				if ( recording == false )
					return;

				file.write("set arrow from " + time + "," + y + " to " + time + ",0\n");
				file.write("set label \"JB\" at " + time + "," + (y - 2) + "\n");

			} else if ( m instanceof ActionHandler.JoinEndMessage ) {

				if ( recording == false )
					return;

				file.write("set arrow from " + time + "," + y + " to " + time + ",0\n");
				file.write("set label \"JE\" at " + time + "," + (y - 2) + "\n");

			} else
				throw new Exception("Got illegal messagetype: " + m.getClass());

		} catch ( Exception e ) {
			System.err.println("WARNING: GnuplotMarkerWriter got Exception: " + e.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {

		if ( recording == true )
			file.close();
		super.finalize();

	}

	/**
	 * @return Returns the filename.
	 */
	public synchronized String getFilename() {
		return filename;
	}

	/**
	 * @param filename
	 *            The filename to set.
	 */
	public synchronized void setFilename(String filename) {
		this.filename = filename;
	}

}
