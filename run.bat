@echo off

cd /d "%~dp0"

java -jar WebCrawler.jar

echo.
echo ========================================
echo          CRAWLER FINISHED
echo ========================================
echo.

pause