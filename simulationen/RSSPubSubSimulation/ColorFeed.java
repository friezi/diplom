import rsspubsubframework.*;
import java.awt.*;
import java.util.*;

public class ColorFeed extends RSSFeed{

	protected Color color;

	protected Date lastBuiltDate;

	public ColorFeed(Color color, Date lastBuiltDate) {
		this.color = color;
		this.lastBuiltDate = lastBuiltDate;
	}

	public Color getColor() {
		return color;
	}

	public Date getLastBuiltDate() {
		return lastBuiltDate;
	}

	@Override
	public boolean isNewerThan(RSSFeed feed) {
		return lastBuiltDate.after(((ColorFeed)feed).getLastBuiltDate());
	}

}
