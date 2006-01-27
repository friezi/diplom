import java.awt.Color;

import rsspubsubframework.Message;

public class RSSFeedRequestMessage extends Message {

	private static int RUNTIME = 10;

	private static int SIZE = 5;

	public RSSFeedRequestMessage(PubSubNode src, RSSServerNode dest) {
		super(src, dest, RUNTIME);
		setColor(Color.red);
	}

	protected int size() {
		return SIZE;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
