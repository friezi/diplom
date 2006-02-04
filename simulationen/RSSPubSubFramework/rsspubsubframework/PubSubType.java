package rsspubsubframework;

/**
 * This type represents a PubSub
 */

/**
 * @author friezi
 * 
 */
public interface PubSubType {

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
