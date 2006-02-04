package rsspubsubframework;

/**
 * This type represents a PubSub
 */

/**
 * @author friezi
 *
 */
public interface PubSubType {
	
	public void register(BrokerType broker);
	public void unregister(BrokerType broker);

}
