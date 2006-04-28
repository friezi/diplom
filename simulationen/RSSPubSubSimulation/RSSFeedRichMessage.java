import rsspubsubframework.Node;

/**
 * 
 */

/**
 * In this Message can be stored additional (network-relevant-)information.
 * 
 * @author Friedemann Zintel
 * 
 */
public class RSSFeedRichMessage extends RSSFeedMessage {

	/**
	 * The roundtriptime
	 */
	long rtt = 0;

	/**
	 * @param src
	 * @param dst
	 * @param feed
	 * @param rssFeedRepresentation
	 * @param params
	 */
	public RSSFeedRichMessage(Node src, Node dst, RSSFeed feed, RSSFeedRepresentation rssFeedRepresentation, SimParameters params) {
		super(src, dst, feed, rssFeedRepresentation, params);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return Returns the rtt.
	 */
	public long getRtt() {
		return rtt;
	}

	/**
	 * @param rtt
	 *            The rtt to set.
	 */
	public void setRtt(long rtt) {
		this.rtt = rtt;
	}

	public void copyRichInformation(RSSFeedRichMessage rfrm) {
		this.rtt = rfrm.getRtt();
	}

}
