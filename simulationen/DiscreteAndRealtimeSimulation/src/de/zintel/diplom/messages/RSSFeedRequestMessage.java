package de.zintel.diplom.messages;
import java.awt.Color;

import de.zintel.diplom.rps.pubsub.PubSubNode;
import de.zintel.diplom.rps.server.RSSServerNode;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.simulation.TransferMessage;

public class RSSFeedRequestMessage extends TransferMessage {
	
	protected SimParameters params;

	private static int SIZE = 5;

	public RSSFeedRequestMessage(PubSubNode src, RSSServerNode dest,SimParameters params) {
		super(src, dest, params.getRssFeedRequestMsgRT());
		this.params=params;
		setColor(Color.red);
	}

	public int size() {
		return SIZE;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
