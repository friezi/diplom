public class PubSub extends PubSubNode {

	public PubSub(int xp, int yp) {
		super(xp, yp);
		// TODO Auto-generated constructor stub
	}

	public void init() {
		new RSSFeedRequestMessage(this, getRssServer());
	}

}
