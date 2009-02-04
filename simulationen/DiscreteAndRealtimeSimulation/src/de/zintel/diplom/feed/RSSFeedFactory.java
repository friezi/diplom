package de.zintel.diplom.feed;
import java.util.Observable;
import java.util.Observer;

import de.zintel.diplom.util.ObserverJComponent;

public interface RSSFeedFactory {

	public RSSFeed newRSSFeed(RSSFeedGeneralContent generalContent);

	public Observable getFeedNotifier();

	public void addFeedObserver(Observer observer);

	public void deleteFeedObserver(Observer observer);

	public ObserverJComponent createAndStartNewFeedDisplay();

}
