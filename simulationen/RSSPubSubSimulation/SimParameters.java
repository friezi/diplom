import java.util.*;
import java.io.*;

/**
 * To hold the relevant parameters for the simulation
 */

/**
 * @author friezi
 * 
 */
public class SimParameters {

	// default parameters

	/**
	 * RSSServerNode: minimum update-interval
	 */
	int minUpIntv = 5;

	/**
	 * RSSServerNode: maximum update-interval
	 */
	int maxUpIntv = 30;

	/**
	 * RSSServerNode: time to life of the feed
	 */
	int ttl = 10;

	/**
	 * PubSubNode: spread-factor for update-interval of the feeds from the
	 * publishers
	 */
	int spreadFactor = 20/* 3 */;

	/**
	 * RSSFeedRequestMessage: runtime for message RSSFeedRequestMessage
	 */
	int rssFdReqMsgRT = 10;

	/**
	 * RSSFeedMessage: runtime for message RSSFeedMessage
	 */
	int rssFdMsgRT = 4;

	/**
	 * RSSFeedMessage: if true, RSSFeedMessages will represent the feed
	 */
	boolean rssFdMsgRepr = true;

	private String comment = "Parameters for a RPSSimulation";

	/**
	 * @param args
	 *            from the command-line. Should hold the filename to read
	 *            parameters from.
	 * 
	 * Opens the file args[0] and reads the parameters.
	 * @throws IOException
	 */
	SimParameters(String args[]) throws IOException {

		Properties properties = new Properties();

		FileInputStream infile;
		FileOutputStream outfile;
		String filename;

		// set default-parameters
		properties.setProperty("minUpIntv", String.valueOf(minUpIntv));
		properties.setProperty("maxUpIntv", String.valueOf(maxUpIntv));
		properties.setProperty("ttl", String.valueOf(ttl));
		properties.setProperty("spreadFactor", String.valueOf(spreadFactor));
		properties.setProperty("rssFdReqMsgRT", String.valueOf(rssFdReqMsgRT));
		properties.setProperty("rssFdMsgRT", String.valueOf(rssFdMsgRT));
		properties.setProperty("rssFdMsgRepr", String.valueOf(rssFdMsgRepr));

		if ( args.length > 1 ) {

			System.out.println("Invalid calling syntax!");
			System.out.println("usage: <applname> [<filename>]");
			System.exit(1);

		} else if ( args.length < 1 ) {
			System.out.println("No input-file given! Using default-parameters");
		} else {

			filename = args[0];

			try {

				infile = new FileInputStream(filename);
				properties.load(infile);

			} catch ( FileNotFoundException ifne ) {

				System.out.println("Warning: file " + filename
						+ " not found. Will create it with default parameters");

				try {

					outfile = new FileOutputStream(filename);
					properties.store(outfile, comment);

				} catch ( FileNotFoundException ofne ) {
					System.out.println("Warning: couldn't open file " + filename + " for writing.");
				}
			}

		}

		// set params
		minUpIntv = Integer.valueOf((String) properties.getProperty("minUpIntv"));
		maxUpIntv = Integer.valueOf((String) properties.getProperty("maxUpIntv"));
		ttl = Integer.valueOf((String) properties.getProperty("ttl"));
		spreadFactor = Integer.valueOf((String) properties.getProperty("spreadFactor"));
		rssFdReqMsgRT = Integer.valueOf((String) properties.getProperty("rssFdReqMsgRT"));
		rssFdMsgRT = Integer.valueOf((String) properties.getProperty("rssFdMsgRT"));
		if ( properties.getProperty("rssFdMsgRepr").toLowerCase().equals("true") )
			rssFdMsgRepr = true;
		else
			rssFdMsgRepr = false;

	}

}
