package rsspubsubframework;

/**
 * This type represents a broker.
 */

/**
 * @author Friedemann Zintel
 * 
 */
public interface BrokerType {

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
	 * Unregister from all brokers
	 */
	public void callbackUnregisterFromAllBrokers();

	/**
	 * Shows Information about the broker
	 */
	public void showInfo();
}
