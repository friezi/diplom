import rsspubsubframework.*;

public class ColorFeedRepresentation extends RSSFeedRepresentation {
	
	public ColorFeedRepresentation(DisplayableObject dObj, ColorFeed feed) {
		super(dObj, feed);
	}
	
	public void represent(){
		dObj.setColor(((ColorFeed)feed).getColor());
	}
}
