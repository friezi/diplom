package rsspubsubframework;

/**
 * This type represents a PubSub
 */

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

}
