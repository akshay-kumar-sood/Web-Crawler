#!/bin/bash

cd "$(dirname "$0")"

java -jar WebCrawler.jar

echo
echo "========================================"
echo "          CRAWLER FINISHED"
echo "========================================"
echo

read -p "Press Enter to exit..."