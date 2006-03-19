import java.awt.Color;
import java.util.Date;

public class ColorFeedFactory implements RSSFeedFactory {

	public RSSFeed newRSSFeed(RSSFeedGeneralContent generalContent) {
		return new ColorFeed(ColorFactory.nextColor(), generalContent);
	}

}
