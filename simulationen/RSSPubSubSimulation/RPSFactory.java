public interface RPSFactory {

	BrokerNode newBrokerNode(int xp, int yp);

	PubSubNode newPubSubNode(int xp, int yp);

	RSSServerNode newRSSServerNode(int xp, int yp);

}
