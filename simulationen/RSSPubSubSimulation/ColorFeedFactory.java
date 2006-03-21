import java.awt.Color;
import java.util.Date;

/**
 * @author Friedemann Zintel
 * 
 * For generatinf Feeds which just contain a color.
 *
 */
public class ColorFeedFactory implements RSSFeedFactory {

	public RSSFeed newRSSFeed(RSSFeedGeneralContent generalContent) {
		return new ColorFeed(ColorFactory.nextColor(), generalContent);
	}

}
