package de.zintel.diplom.feed;
import java.util.Observable;
import java.util.Observer;

import de.zintel.diplom.util.ColorFactory;
import de.zintel.diplom.util.ObserverJComponent;

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

	public void addFeedObserver(Observer observer) {

	}

	public void deleteFeedObserver(Observer observer) {

	}

	public Observable getFeedNotifier() {
		return null;
	}

	public ObserverJComponent createAndStartNewFeedDisplay() {
		return null;
	}

}
