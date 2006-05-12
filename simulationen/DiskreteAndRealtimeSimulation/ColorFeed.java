import java.awt.Color;

/**
 * A Feed which stores a color
 * 
 * @author Friedemann Zintel
 *
 */
public class ColorFeed extends RSSFeed {

	protected Color color;

	public ColorFeed(Color color, RSSFeedGeneralContent generalContent) {
		super(generalContent);
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

}
