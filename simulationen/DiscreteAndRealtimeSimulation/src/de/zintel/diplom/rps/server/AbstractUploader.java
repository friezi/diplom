package de.zintel.diplom.rps.server;
import de.zintel.diplom.simulation.Message;

/**
 * 
 */

/**
 * To simulate the upload of data to a node.
 * 
 * @author Friedemann Zintel
 *
 */
public interface AbstractUploader {

	/**
	 * @param m the message
	 */
	public void upload(Message m);

}
