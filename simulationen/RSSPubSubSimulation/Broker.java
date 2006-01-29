import rsspubsubframework.*;
import java.util.*;

public class Broker extends BrokerNode {

	public Broker(int xp, int yp) {
		super(xp, yp);
		// TODO Auto-generated constructor stub
	}

	public void receiveMessage(Message m) {

		if ( m instanceof RSSFeedMessage ) {

//			getRssFeedRepresentationFactory().
			
		}
	}

}
