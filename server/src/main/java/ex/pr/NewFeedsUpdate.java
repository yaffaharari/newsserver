package ex.pr;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.fetcher.FetcherListener;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.io.FeedException;
import org.eclipse.jetty.server.Server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

public class NewFeedsUpdate implements Runnable {

    private URL feedUrl;
    private final HttpURLFeedFetcher fetcher;
    private ScheduledExecutorService service;
    private ArrayList<URL> rssProviders;

    public NewFeedsUpdate(ArrayList<URL> urls) throws MalformedURLException {
        //this.feedUrl = new URL(rssUrl);
        this.rssProviders = urls;
        final FeedFetcherCache feedInfoCache = HashMapFeedInfoCache.getInstance();
        this.fetcher = new HttpURLFeedFetcher(feedInfoCache);
        //FetcherEventListenerImpl listener = new FetcherEventListenerImpl();
        final FetcherListener listener = new RSSEventListener();
        this.fetcher.addFetcherEventListener(listener);
    }

    @Override
    public void run() {
        while (true) {
            try {
                rssProviders.forEach(new Consumer<URL>() {
                    @Override
                    public void accept(URL url) {
                        final SyndFeed feeds;
                        try {
                            feeds = fetcher.retrieveFeed(url);
                            System.out.println(feeds.getPublishedDate());
                            System.out.println("---------------------------------");
                            Thread.sleep(8000);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (FeedException e) {
                            e.printStackTrace();
                        } catch (FetcherException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
