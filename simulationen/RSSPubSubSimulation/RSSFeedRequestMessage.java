import java.awt.Color;

import rsspubsubframework.Message;

public class RSSFeedRequestMessage extends Message {

	private static int RUNTIME = 10;

	private static int SIZE = 5;

	public RSSFeedRequestMessage(PubSubNode pubsub, RSSServerNode rssServer) {
		super(pubsub, rssServer, RUNTIME);
		setColor(Color.red);
	}

	protected int size() {
		return SIZE;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
