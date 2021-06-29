package timeline_template;

import java.awt.Cursor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

import common.entities.Event;
import common.entities.EventPlot;
import common.entities.Rect;
import common.entities.ScrapeResult;
import common.entities.TimeLine;
import common.functions.ClickAndDrag;
import common.functions.Round;
import common.functions.UploadCSV;
import common.functions.EmbedURL;
import gui.Gui;
import web_scrapers.scrapers.GoogleScraper;
import web_scrapers.scrapers.WikiScraper;

public class TimeLinePlot implements Runnable {

    private final int margin;
    private final String[] xAxis;
    private final TimeLine timeLine;

    public TimeLinePlot() {
        this.margin = 50;
        this.timeLine = generateTimeLine();
        this.xAxis = createXAxis();
    }

    private TimeLine generateTimeLine() {
        TimeLine timeLine = new TimeLine();
        WikiScraper wikiScrape = new WikiScraper();
        GoogleScraper google = new GoogleScraper();
        Gui gui = new Gui();
        ArrayList<String> scrapeTopics = new ArrayList<String>();
        ScrapeResult result;
        UploadCSV uploadCSV = new UploadCSV();
        String output = "";

        uploadCSV.extractingData(scrapeTopics, "timeline.csv");
        scrapeTopics.remove(0);
        for (String scrapeTopic : scrapeTopics) {
            String link = google.scrape(scrapeTopic.replaceAll("-P-", ""));
            System.out.println(link);
            try {
                if (scrapeTopic.contains("-P-")) {
                    scrapeTopic = scrapeTopic.replaceAll("-P-", "");
                    result = wikiScrape.periodScrape(link);
                    Event event = new Event(scrapeTopic, Color.BLUE, result.getStartDate(), result.getEndDate(), link);
                    String toPrint = result.getName() + ", " + result.getStartDate() + " - " + result.getEndDate()
                            + "\n";
                    output += toPrint;
                    timeLine.addEvent(event);
                } else {
                    result = wikiScrape.eventScrape(link);
                    Event event = new Event(scrapeTopic, Color.BLUE, result.getStartDate(), result.getEndDate(), link);
                    String toPrint = result.getName() + ", " + result.getStartDate() + "\n";
                    output += toPrint;
                    timeLine.addEvent(event);
                }
            } catch (Exception exception) {
                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints c = new GridBagConstraints();
                JLabel label1 = new JLabel("Error: Unable to find dates for " + scrapeTopic);
                JLabel label2 = new JLabel(
                        "Perhaps try being more specific or follow the link and enter the dates manually below.");
                JLabel url = new JLabel("<HTML><U>" + link + "</U></HTML>");
                JLabel startLabel = new JLabel("Start Date:");
                JLabel endLabel = new JLabel("End Date (Enter 'Present' if the event has not ended):");
                JTextField startTF = new JTextField(10);
                JTextField endTF = new JTextField(10);
                c.gridy = 0;
                panel.add(label1, c);
                c.gridy = 1;
                panel.add(label2, c);
                c.gridy = 2;
                panel.add(url, c);
                c.gridy = 3;
                panel.add(startLabel, c);
                c.gridy = 4;
                panel.add(startTF, c);
                c.gridy = 5;
                panel.add(endLabel, c);
                c.gridy = 6;
                panel.add(endTF, c);
                url.setForeground(Color.BLUE.darker());
                EmbedURL.embed(link, url);
                Object[] options = { "Submit", "Cancel" };
                int option = JOptionPane.showOptionDialog(null, panel, "Error: " + scrapeTopic,
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        int startDate = Integer.parseInt(startTF.getText());
                        int endDate;
                        if (endTF.getText().equals("Present") || endTF.getText().equals("present")) {
                            endDate = 3000;
                        } else {
                            endDate = Integer.parseInt(endTF.getText());
                        }
                        Event event = new Event(scrapeTopic, Color.BLUE, startDate, endDate, link);
                        timeLine.addEvent(event);
                        if (endDate == 3000) {
                            String toPrint = scrapeTopic + ", " + startDate + " - " + "Present" + "\n";
                            output += toPrint;
                        } else {
                            String toPrint = scrapeTopic + ", " + startDate + " - " + endDate + "\n";
                            output += toPrint;
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "You must enter dates", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            gui.printText(output);
        }
        return timeLine;
    }

    private String[] createXAxis() {
        Integer startDate = timeLine.getStartDate();
        int endDate;
        if (timeLine.getEndDate() == 3000) {
            endDate = 2021;
        } else {
            endDate = timeLine.getEndDate();
        }
        int roundStart = Round.roundDown(startDate);
        int roundEnd = Round.roundUp(endDate);
        int duration = roundEnd - roundStart;

        String[] axis = new String[duration + 1];
        Integer date = roundStart;
        for (int i = 0; i <= duration; i += 1) {
            axis[i] = date.toString();
            date += 1;
        }

        return axis;
    }

    @Override
    public void run() {
        JFrame frame = new JFrame("Time Line");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane();
        frame.add(scrollPane, BorderLayout.CENTER);

        ArrayList<String> scrapeTopics = new ArrayList<String>();
        UploadCSV uploadCSV = new UploadCSV();

        uploadCSV.extractingData(scrapeTopics, "timeline.csv");
        String title = scrapeTopics.get(0);

        TimeLinePanel timeLinePanel = new TimeLinePanel(margin, timeLine, xAxis);
        String timelineStart = String.valueOf(timeLine.getStartDate());
        String timelineEnd = String.valueOf(timeLine.getEndDate());
        JPanel panel = new JPanel();
        if (timeLine.getEndDate() == 3000) {
            JLabel titleLabel = new JLabel(
                    "<html><center>" + title + "<br>" + "(" + timelineStart + " - " + "Present)</center></html>");
            panel.add(titleLabel);
        } else {
            JLabel titleLabel = new JLabel("<html><center>" + title + "<br>" + "(" + timelineStart + " - " + timelineEnd
                    + ")</center></html>");
            panel.add(titleLabel);
        }

        frame.getContentPane().add(BorderLayout.NORTH, panel);
        timeLinePanel.setBorder(new MatteBorder(5, 5, 5, 5, Color.BLUE));
        frame.setLocationByPlatform(true);
        new ClickAndDrag(timeLinePanel);
        scrollPane.setViewportView(timeLinePanel);
        for (EventPlot temp : timeLinePanel.getEventPlots()) {
            Rect rect = new Rect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
            if (temp.getEnd() == 3000) {
                rect.setToolTipText("(" + temp.getStart() + " - " + "Present" + ")");
            } else {
                rect.setToolTipText("(" + temp.getStart() + " - " + temp.getEnd() + ")");
            }
            rect.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            timeLinePanel.addBar(rect);
        }
        frame.setVisible(true);
    }
}