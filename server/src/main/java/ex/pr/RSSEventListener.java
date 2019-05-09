package ex.pr;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherListener;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RSSEventListener implements FetcherListener {

    private Date currentDate;
    private List<FeedItem> feedItemList = new ArrayList<>();

    public RSSEventListener() {
        this.currentDate = new Date();
    }

    @Override
    public void fetcherEvent(final FetcherEvent event) {
         synchronized (event) {
            try {
                final String eventType = event.getEventType();
                if (FetcherEvent.EVENT_TYPE_FEED_RETRIEVED.equals(eventType)) {
                    System.out.println("RETRIEVED " + event.getUrlString());
                    final SyndFeed feeds = event.getFeed();
                    if (feeds != null && feeds.getEntries() != null) {
                        @SuppressWarnings("unchecked")
                        List<SyndEntry> feedsFiltered = (List<SyndEntry>) feeds.getEntries().stream()
                                .filter((entry) -> getLastDate((SyndEntry) entry) != null)
                                .filter((entry) ->
                                        getLastDate((SyndEntry) entry) != null && getLastDate((SyndEntry) entry).after(currentDate))
                                .sorted((entry1, entry2) -> ObjectUtils.compare(getLastDate((SyndEntry) entry1), getLastDate((SyndEntry) entry2)))
                                .collect(Collectors.toList());

                        if (!feedsFiltered.isEmpty()) {
                            currentDate = getLastDate(feedsFiltered.get(feedsFiltered.size() - 1));
                            System.out.println(currentDate);
                            feedsFiltered.forEach((entry) -> addFeedItemToList(entry));
                            Thread sendNotificationArticle = new Thread(new SendNotificationArticle(feedItemList));
                            sendNotificationArticle.start();
                        }
                        //feedsFiltered.forEach((entry) -> out.send(toJson(entry), null));
                        feedsFiltered.forEach((entry) -> System.out.println(entry.getTitle()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addFeedItemToList(SyndEntry entry) {
        String title = entry.getTitle();
        String description = entry.getDescription().getValue();
        String link = entry.getLink();
        Date pubDate = entry.getPublishedDate();

        FeedItem item = new FeedItem(title, description, link, pubDate);
        feedItemList.add(item);
    }

    private Date getLastDate(SyndEntry entry) {
        if (entry.getUpdatedDate() != null) {
            return entry.getUpdatedDate();
        } else {
            return entry.getPublishedDate();
        }
    }
/*
    private String toJson(final SyndEntry entry) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();
        return gson.toJson(entry).toString();
    }*/
}
