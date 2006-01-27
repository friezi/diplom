import java.awt.Color;
import java.util.Date;

public class ColorFeedFactory implements RSSFeedFactory {

	Color color = Color.gray;

	public RSSFeed newRSSFeed() {
		cycleColors();
		return new ColorFeed(color, new Date());
	}

	protected void cycleColors() {

		if ( color == Color.black )
			color = Color.blue;
		else if ( color == Color.blue )
			color = Color.cyan;
		else if ( color == Color.cyan )
			color = Color.darkGray;
		else if ( color == Color.darkGray )
			color = Color.gray;
		else if ( color == Color.gray )
			color = Color.green;
		else if ( color == Color.green )
			color = Color.lightGray;
		else if ( color == Color.lightGray )
			color = Color.magenta;
		else if ( color == Color.magenta )
			color = Color.orange;
		else if ( color == Color.orange )
			color = Color.pink;
		else if ( color == Color.pink )
			color = Color.white;
		else if ( color == Color.white )
			color = Color.yellow;
		else if ( color == Color.yellow )
			color = Color.black;

	}
}
