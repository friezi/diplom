package de.zintel.diplom.rps.pubsub;
/**
 * This type represents a PubSub
 */

import java.util.*;

import de.zintel.diplom.rps.broker.BrokerType;

/**
 * @author Friedemann Zintel
 * 
 */
public interface PubSubType {

	/**
	 * Register at a broker
	 * 
	 * @param broker
	 *            the broker
	 */
	public void callbackRegisterAtBroker(BrokerType broker);

	/**
	 * Unregister from a broker
	 * 
	 * @param broker
	 *            the broker
	 */
	public void callbackUnregisterFromBroker(BrokerType broker);

	/**
	 * Blocks a PubSub
	 */
	public void callbackBlock(long delay);

	/**
	 * Unblocks a PubSub
	 */
	public void callbackUnblock(long delay);

	/**
	 * @return true if node is blocked, false otherwise
	 */
	public boolean callbackIsBlocked();

	/**
	 * @return a new list with all brokers the node is connected with
	 */
	public HashSet<BrokerType> getBrokers();

	/**
	 * @return the number of brokers the node is connected with
	 */
	public int getNumberOfBrokers();

	/**
	 * @return true if the node is ijn registering-phase at any broker
	 */
	public boolean isRegistering();

	/**
	 * Shows Information about the pubsub
	 */
	public void showInfo();

	/**
	 * Changes the current Polling-period
	 */
	public void callbackResetCpp();

	/**
	 * all connections will be disconnected (except for the current RSS-server), all variables will be resetted
	 */
	public void renew();

	/**
	 * initializes the node
	 */
	public void init();

	/**
	 * resets all parameters, the pubsub is naked.
	 */
	public void reset();

	/**
	 * starts processing of the node;
	 */
	public void start();

}
