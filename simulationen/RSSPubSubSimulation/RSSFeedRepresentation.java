import rsspubsubframework.*;

public class RSSFeedRepresentation {
	
	protected RSSFeed feed;
	
	protected DisplayableObject dObj;
	
	public RSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed) {
		this.dObj = dObj;
		this.feed = feed;
	}
	
	public void represent(){}

	public void setDObj(DisplayableObject obj) {
		dObj = obj;
	}
	
}
