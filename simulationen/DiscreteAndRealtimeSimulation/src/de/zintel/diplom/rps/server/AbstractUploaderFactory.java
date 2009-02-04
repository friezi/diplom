package de.zintel.diplom.rps.server;

/**
 * 
 */
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;

/**
 * For generating Uploaders.
 * 
 * @author Friedemann Zintel
 * 
 */
public class AbstractUploaderFactory {

	/**
	 * an uploader for a broker
	 */
	public static final String BROKER_U = "broker";

	/**
	 * an uploader for a server
	 */
	public static final String SERVER_U = "server";

	SimParameters params;

	public AbstractUploaderFactory(SimParameters params) {
		this.params = params;
	}

	public AbstractUploader newUploader(Node node, String type) throws Exception {

		String thisMethod = "newUploader()";

		if ( type.equals(BROKER_U) ) {

			if ( params.isDiscreteSimulation() == false )
				return new DelayingUploader();
			else
				return new VirtualUploader();
		} else if ( type.equals(SERVER_U) ) {

			if ( params.isDiscreteSimulation() == false )
				return new DelayingServerUploader((RSSServerNode) node);
			else
				return new VirtualServerUploader((RSSServerNode) node, params);

		} else
			throw new Exception(thisMethod + ": Type " + type + " not supported!");

	}
}