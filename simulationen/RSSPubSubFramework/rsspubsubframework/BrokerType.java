package rsspubsubframework;

/**
 * This type represents a broker.
 */

/**
 * @author friezi
 * 
 */
public interface BrokerType {

	/**
	 * Register at a broker
	 * 
	 * @param broker
	 *            the broker
	 */
	public void register(BrokerType broker);

	/**
	 * Unregister from a broker
	 * 
	 * @param broker
	 *            the broker
	 */
	public void unregister(BrokerType broker);

}
