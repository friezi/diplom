import java.awt.Color;

/**
 * 
 */

/**
 * Just to produce colors (color-cycling).
 *
 * @author Friedemann Zintel
 * 
 */
public class ColorFactory {

	static private Color color = Color.orange;

	//  not for use
	private ColorFactory(){}
	
	/**
	 * produces a new Color out of a circle.
	 * 
	 * @return the next Color
	 */
	static Color nextColor() {

		if ( color == Color.black )
			color = Color.blue;
		else if ( color == Color.blue )
			color = Color.cyan;
		else if ( color == Color.cyan )
			color = Color.darkGray;
		else if ( color == Color.darkGray )
			color = Color.green;
		else if ( color == Color.green )
			color = Color.gray;
		else if ( color == Color.gray )
			color = Color.magenta;
		else if ( color == Color.magenta )
			color = Color.lightGray;
		else if ( color == Color.lightGray )
			color = Color.orange;
		else if ( color == Color.orange )
			color = Color.pink;
		else if ( color == Color.pink )
			color = Color.white;
		else if ( color == Color.white )
			color = Color.yellow;
		else if ( color == Color.yellow )
			color = Color.black;

		return color;

	}

}
