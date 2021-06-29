package web_scrapers.scrapers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import java.io.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import common.entities.ScrapeResult;
import common.functions.NumberCheck;

public class WikiScraper {
    public ScrapeResult result;

    // Web scraper to scrape single date events from Wikipedia
    public ScrapeResult eventScrape(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String title = doc.title();
        title = title.replaceAll(" - Wikipedia", "");
        Elements paragraphs = doc.select(".mw-content-ltr tr");

        Element firstParagraph = paragraphs.first();
        Element lastParagraph = paragraphs.last();
        Element p;
        int i = 1;
        ArrayList<String> datesInWiki = new ArrayList<String>();
        p = firstParagraph;
        while (p != lastParagraph) {
            p = paragraphs.get(i);
            // More key words can be added in this if statement to cover more topics
            if (p.text().contains("Date") || p.text().contains("Released") || p.text().contains("Signed")) {
                String period = p.text();
                String justDates = period.replaceAll("[^0-9]", " ");
                datesInWiki.add(justDates);
            }
            i++;
        }
        String datesWithDay = datesInWiki.get(0);
        int date = justDate(datesWithDay);
        result = new ScrapeResult(title, date, date);
        return result;
    }

    // Web scraper to scrape time periods from Wikipedia
    public ScrapeResult periodScrape(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String title = doc.title();
        title = title.replaceAll(" - Wikipedia", "");
        Elements paragraphs = doc.select(".mw-content-ltr tr");

        Element firstParagraph = paragraphs.first();
        Element lastParagraph = paragraphs.last();
        Element p;
        int i = 1;
        ArrayList<String> datesInWiki = new ArrayList<String>();
        p = firstParagraph;
        while (p != lastParagraph) {
            p = paragraphs.get(i);
            // if statement to single out a persons birth and death date
            if (p.text().contains("Died") || p.text().contains("Born")) {
                String paragraph = p.text();
                if (paragraph.contains("BC")) {
                    String[] bcSplit = paragraph.split("\\s+");
                    for (int j = 0; j < bcSplit.length; j++) {
                        if (bcSplit[j].contains("BC")) {
                            String bcDates = bcSplit[j - 1].replaceAll("[^0-9]", " ");
                            String bcDate = "-" + bcDates;
                            datesInWiki.add(bcDate);
                        }
                    }
                } else if (paragraph.contains("AD")) {
                    String[] adSplit = paragraph.split("\\s+");
                    for (int j = 0; j < adSplit.length; j++) {
                        if (adSplit[j].contains("AD")) {
                            if (NumberCheck.check(adSplit[j + 1]) == true) {
                                datesInWiki.add(adSplit[j + 1].replaceAll("[^0-9]", " "));
                            } else {
                                datesInWiki.add(adSplit[j - 1].replaceAll("[^0-9]", " "));
                            }
                        }
                    }
                } else {
                    String justDates = paragraph.replaceAll("[^0-9]", " ");
                    datesInWiki.add(justDates);
                }
                // else if that covers wars and other historical events
            } else if (p.text().contains("Date")) {
                String paragraph = p.text();
                String justDates = paragraph.replaceAll("[^0-9]", " ");
                String[] dates = justDates.split("\\s+");
                for (int j = 0; j < dates.length; j++) {
                    if (dates[j].length() >= 3) {
                        datesInWiki.add(dates[j]);
                    }
                }
            }
            i++;
        }

        // Take the first 2 strings found as the start and end date
        String datesWithDayStart = datesInWiki.get(0);
        String datesWithDayEnd = datesInWiki.get(1);
        // just date function removes any number that is not the year from the string
        // and converts to int
        int startDate = justDate(datesWithDayStart);
        int endDate = justDate(datesWithDayEnd);
        // two if statements to ensure dates are correctly marked as AD or BC
        if (startDate > endDate && startDate > 0 && endDate > 0) {
            startDate = startDate * -1;
            endDate = endDate * -1;
        }
        if (endDate < 0 && startDate > 0) {
            startDate = startDate * -1;
        }
        result = new ScrapeResult(title, startDate, endDate);
        return result;
    }

    private int justDate(String datesWithDay) {
        int minLength = 3;
        String dateFinal = "";
        ArrayList<String> inconclusiveDates = new ArrayList<String>();
        String[] dates = datesWithDay.split("\\s+");
        if (dates.length != 1) {
            for (int i = 0; i < dates.length; i++) {
                String date = dates[i];
                if (date.length() >= minLength) {
                    inconclusiveDates.add(date);
                    dateFinal += inconclusiveDates.get(0) + " ";
                }
            }
        } else {
            dateFinal += dates[0];
        }
        dateFinal = Arrays.stream(dateFinal.split("\\s+")).distinct().collect(Collectors.joining(""));
        int dateInt = Integer.parseInt(dateFinal);
        return dateInt;
    }
}