import java.awt.Color;

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
