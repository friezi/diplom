import java.awt.Color;

public class RSSFeedRequestMessage extends Message {
	
	protected SimParameters params;

	private static int SIZE = 5;

	public RSSFeedRequestMessage(PubSubNode src, RSSServerNode dest,SimParameters params) {
		super(src, dest, params.rssFeedRequestMsgRT);
		this.params=params;
		setColor(Color.red);
	}

	protected int size() {
		return SIZE;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
