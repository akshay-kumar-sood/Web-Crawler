# Java Web Crawler

A multithreaded web crawler built in Java that crawls web pages up to a user-defined depth using multiple worker threads. The crawler extracts links from web pages, prevents duplicate URL processing, and generates timestamped crawl reports and URL logs.

## Tech Stack

- Java
- Maven
- Jsoup
- ExecutorService
- Phaser

## Algorithms and Data Structures

- ConcurrentHashMap — thread-safe tracking of visited URLs
- BlockingQueue — thread-safe URL queue for pending URLs
- Fixed Thread Pool — manages concurrent crawler workers
- Phaser — coordinates dynamically created crawler tasks
- Depth-Limited Crawling — controls how far the crawler traverses from the starting URL
- URL Deduplication — prevents the same URL from being crawled multiple times
- Multithreaded Task Execution — processes multiple URLs concurrently

## Running the Project

### Linux

```text
WebCrawler/
├── WebCrawler.jar
└── run.sh
```

Double-click `run.sh` and select **Run in Terminal**.

### Windows

```text
WebCrawler/
├── WebCrawler.jar
└── run.bat
```
Double-click `run.bat` to start the crawler.


## High-Level Design

![High Level Design](docs/hld.png)

## Screenshots

### Console input
![Crawler Execution](docs/console.png)

### Report File
![Crawler Report](docs/report.png)

### Summary
![Crawler Report](docs/summary.png)

## Crawling Notice

**Not all websites allow automated crawling. Always check a website's
`robots.txt` Terms of Service, and applicable laws before crawling.**

For testing and learning purposes, this project can be used with:

https://books.toscrape.com/
