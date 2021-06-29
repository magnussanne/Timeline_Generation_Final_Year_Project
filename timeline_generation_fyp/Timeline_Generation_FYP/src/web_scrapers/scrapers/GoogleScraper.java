package web_scrapers.scrapers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;

//Web scraper to scrape Wikipedia URL's from Google
public class GoogleScraper {

  public String scrape(String scrapeTopic) {
    Document doc = null;
    try {
      doc = Jsoup.connect("https://www.google.com/search?q=" + scrapeTopic
          + "&rlz=1C1CHBF_enIE805IE805&oq=&aqs=chrome.0.0i355j46j69i59j0j46j0j69i60l2.911j0j7&sourceid=chrome&ie=UTF-8")
          .get();
    } catch (IOException e) {
      e.printStackTrace();
    }
    ArrayList<String> wikiLinks = new ArrayList<String>();
    Elements links = doc.select("a[href]");
    for (Element link : links) {
      if (link.text().contains("Wikipedia")) {
        wikiLinks.add(link.attr("href"));
      }
    }
    String wikiLink = wikiLinks.get(0);
    return wikiLink;
  }
}
