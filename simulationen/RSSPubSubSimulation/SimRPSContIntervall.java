import rsspubsubframework.*;

public class SimRPSContIntervall {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        Szenario1 szenario = new Szenario1(new RPSFactory() {
            public BrokerNode newBrokerNode(int xp, int yp){ return new Broker(xp,yp); }
            public PubSubNode newPubSubNode(int xp, int yp){ return new PubSub(xp,yp); }
            public RSSServerNode newRSSServerNode(int xp, int yp){ return new RSSServer(xp,yp); }
        });

    }

}
