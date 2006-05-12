import java.awt.Color;
import java.util.Date;

/**
 * 
 * For generating Feeds which just contain a color.
 * 
 * @author Friedemann Zintel
 *
 */
public class ColorFeedFactory implements RSSFeedFactory {

	public RSSFeed newRSSFeed(RSSFeedGeneralContent generalContent) {
		return new ColorFeed(ColorFactory.nextColor(), generalContent);
	}

}
