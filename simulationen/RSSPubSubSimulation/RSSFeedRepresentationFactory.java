import rsspubsubframework.DisplayableObject;

public interface RSSFeedRepresentationFactory {
	public RSSFeedRepresentation newRSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed);
}
