package org.example;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class WebCrawler {

    private static Phaser phaser;
    private static ExecutorService executorService;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("========================================");
        System.out.println("          JAVA WEB CRAWLER");
        System.out.println("========================================\n");

        System.out.println("Enter your url : ");
        String url = sc.nextLine();

        System.out.println("Enter the Depth of the crawler : ");
        final int MAX_DEPTH = sc.nextInt();

        System.out.println("Enter the number of workers : ");
        final int MAX_THREADS = sc.nextInt();

        System.out.println();
        System.out.println("[INFO] Crawler started...");
        System.out.println("[INFO] URL: " + url);
        System.out.println("[INFO] Depth: " + MAX_DEPTH);
        System.out.println("[INFO] Workers: " + MAX_THREADS);
        System.out.println();

        URLStore urlStore = new URLStore();
        URLFethcher urlFetcher = new URLFethcher();

        CrawlReport report;
        try {
            report = new CrawlReport(url, MAX_DEPTH, MAX_THREADS);
        } catch (IOException e) {
            System.out.println("[ERROR] Could not set up output files: " + e.getMessage());
            return;
        }

        phaser = new Phaser(1);

        executorService = Executors.newFixedThreadPool(MAX_THREADS);

        urlStore.addUrl(url);
        long start = System.currentTimeMillis();
        submitTask(urlStore, urlFetcher, 0, MAX_DEPTH, report);
        phaser.awaitAdvance(phaser.getPhase());

        executorService.shutdown();
        long timeTaken = System.currentTimeMillis() - start;

        report.writeFinalReport(urlStore.getVisitedCount(), timeTaken);

        System.out.println("[INFO] Crawling completed.\n");
        System.out.println("Report saved to:");
        System.out.println(report.getReportFileName());
        System.out.println("URLs saved to:");
        System.out.println(report.getUrlsFileName());
        System.out.println("Time taken: " + (timeTaken / 1000.0) + " seconds");
    }

    public static void submitTask(URLStore urlStore, URLFethcher urlFetcher, int currDepth, int maxDepth, CrawlReport report) {
        executorService.submit(
                new CrawlerTask(urlStore, urlFetcher, maxDepth, currDepth, phaser, report)
        );
    }
}