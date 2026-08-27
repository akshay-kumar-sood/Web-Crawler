package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CrawlReport {

    private final BufferedWriter urlsWriter;

    private final List<String> crawledEntries = new ArrayList<>();
    private final List<String> failedUrls = new ArrayList<>();

    private final String startUrl;
    private final int maxDepth;
    private final int workers;

    // Same timestamp will be used for both files
    private final String timestamp;

    private int pagesCrawled = 0;

    public CrawlReport(String startUrl, int maxDepth, int workers) throws IOException {

        this.startUrl = startUrl;
        this.maxDepth = maxDepth;
        this.workers = workers;

        File folder = new File("output");

        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Generate timestamp only once for this crawler run
        timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

        // Create a new file for this run
        urlsWriter = new BufferedWriter(new FileWriter("output/crawled-urls-" + timestamp + ".txt"));
    }

    // Called by worker threads whenever a URL is successfully crawled.
    // synchronized keeps file writing thread-safe.
    public synchronized void recordCrawledUrl(String url, int depth, String threadName) {

        try {

            urlsWriter.write(url);
            urlsWriter.newLine();
            urlsWriter.flush();

        } catch (IOException e) {
            System.out.println("[WARN] Could not write to crawled URLs file: " + e.getMessage());
        }

        crawledEntries.add("[Depth " + depth + "] [" + threadName + "]\n" + url + "\n");
        pagesCrawled++;
    }

    public synchronized void recordFailedUrl(String url) {
        failedUrls.add(url);
    }

    private void closeUrlsFile() {

        try {
            urlsWriter.close();
        } catch (IOException e) {
            System.out.println("[WARN] Could not close crawled URLs file: " + e.getMessage());
        }
    }

    // Called once after crawling finishes.
    public synchronized void writeFinalReport(int totalUniqueDiscovered, long timeTakenMillis) {

        closeUrlsFile();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output/crawl-report-" + timestamp + ".txt"))) {

            writer.write("========================================\n");
            writer.write("          JAVA WEB CRAWLER\n");
            writer.write("========================================\n\n");

            writer.write("Starting URL      : " + startUrl + "\n");

            writer.write("Maximum Depth     : " + maxDepth + "\n");

            writer.write(
                    "Number of Workers : "
                            + workers + "\n\n"
            );

            writer.write("----------------------------------------\n");
            writer.write("CRAWLED URLS\n");
            writer.write("----------------------------------------\n\n");

            for (String entry : crawledEntries) {
                writer.write(entry);
                writer.write("\n");
            }

            writer.write("----------------------------------------\n");
            writer.write("CRAWL SUMMARY\n");
            writer.write("----------------------------------------\n\n");

            writer.write("Total pages crawled      : " + pagesCrawled + "\n");

            writer.write("Total unique URLs found  : " + totalUniqueDiscovered + "\n");

            writer.write("Failed URLs              : " + failedUrls.size() + "\n");

            for (String failed : failedUrls) {
                writer.write("   - " + failed + "\n");
            }

            writer.write("Maximum depth            : " + maxDepth + "\n");

            writer.write("Number of workers        : " + workers + "\n");

            writer.write("Total execution time     : " + (timeTakenMillis / 1000.0) + " seconds\n\n");

            writer.write("========================================\n");
            writer.write("          CRAWL COMPLETED\n");
            writer.write("========================================\n");

        } catch (IOException e) {
            System.out.println("[WARN] Could not write crawl report: " + e.getMessage());
        }
    }

    // Used by WebCrawler to display the exact filenames.
    public String getReportFileName() {
        return "output/crawl-report-" + timestamp + ".txt";
    }

    public String getUrlsFileName() {
        return "output/crawled-urls-" + timestamp + ".txt";
    }
}