package org.example;

import java.util.Set;
import java.util.concurrent.Phaser;

public class CrawlerTask implements Runnable{

    private final URLStore urlStore;
    private final URLFethcher urlFetcher;
    private final int maxDepth;
    private final int currDepth;
    private final CrawlReport report;

    // maintain counter of current thread working.When it become 0 we stop.
    private final Phaser phaser;

    public CrawlerTask(URLStore urlStore, URLFethcher urlFetcher, int maxDepth, int currDepth, Phaser phaser, CrawlReport report) {
        this.urlStore = urlStore;
        this.urlFetcher = urlFetcher;
        this.maxDepth = maxDepth;
        this.currDepth = currDepth;
        this.phaser = phaser;
        this.report = report;
    }

    @Override
    public void run(){
        String url = null;
        try{
            url = urlStore.getNextUrl();
            if(url==null || currDepth>maxDepth) return;

            Set<String> links = urlFetcher.fethchLInks(url);

            report.recordCrawledUrl(url, currDepth, Thread.currentThread().getName());

            for(String link:links){
                if(currDepth < maxDepth && urlStore.addUrl(link)){
                    phaser.register();

                    WebCrawler.submitTask(urlStore, urlFetcher, currDepth+1, maxDepth, report);
                }
            }
        }catch (Exception e){
            System.out.println("[ERROR] Failed to crawl: " + url);
            if(url != null){
                report.recordFailedUrl(url);
            }
        }finally {
            phaser.arriveAndDeregister();
        }
    }
}