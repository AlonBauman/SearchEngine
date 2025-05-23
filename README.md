# Newgle Search Engine

A simplified Java-based search engine inspired by early Google. Built using BFS crawling, inverted indexing, and a PageRank-style algorithm to rank and return web pages based on keyword search.

## 🔍 What It Does

- Starts from seed URLs and **crawls connected web pages** using a simulated browser.
- **Indexes** all words and tracks **links** between pages.
- Computes **page importance scores** using both a memory-based and disk-based PageRank implementation.
- Supports **keyword search queries**, returning the most relevant pages in ranked order.

## 💡 Key Features

- Uses `jsoup` to fetch and parse HTML content.
- Custom file system simulation via `HardDisk<T>` for scalable data storage.
- Implements both `rankSlow()` (in-memory) and `rankFast()` (file-based external sort) algorithms.
- Returns only pages that contain **all** search terms using an efficient merging iterator strategy.
- Results are sorted by computed importance (PageRank score).

## 🛠 Technologies

- Java (Collections, File I/O, Generics)
- jsoup (HTML parsing)
- Simulated disk-based storage and sorting
- Custom comparators, iterators, and priority queues

## 🚀 How to Run

1. Clone the repo and open in IntelliJ.
2. Ensure `jsoup-1.8.3.jar` is added to the classpath.
3. Run:
   - `Collect.java` to crawl and index new data
   - `Rank.java` to assign page importance
   - `Search.java` to query indexed content

## 📁 Project Structure

- `Newgle.java`: Main engine with collect, rank, and search methods
- `PageFile.java`, `WordFile.java`: Indexed page and word objects
- `HardDisk.java`: Simulated disk for scalable data storage
- `BetterBrowser.java`: Uses jsoup to extract links and text
- `Search.java`: GUI and console search interface

## ✍️ Created By

Developed as part of a lab series at the University of Miami, Spring 2025.
