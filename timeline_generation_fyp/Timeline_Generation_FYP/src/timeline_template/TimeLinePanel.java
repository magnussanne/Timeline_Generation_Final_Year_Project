package timeline_template;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JPanel;

import common.entities.Event;
import common.entities.EventPlot;
import common.entities.Rect;
import common.entities.TimeLine;
import common.functions.Round;

public class TimeLinePanel extends JPanel {

    private static final long serialVersionUID = 1L;
    public ArrayList<EventPlot> eventPlots = new ArrayList<EventPlot>();
    private ArrayList<Rect> bars = new ArrayList<Rect>();;
    private final int plotWidth;
    private int plotHeight;
    private final int margin;
    private final int tickLength;

    private final String[] xAxis;

    private final TimeLine timeLine;

    public TimeLinePanel(int margin, TimeLine timeLine, String[] xAxis) {
        this.plotHeight = 500;
        ArrayList<Event> events = timeLine.getEvents();
        ArrayList<Event> singleEvents = new ArrayList<>();
        ArrayList<Event> periods = new ArrayList<>();
        int stackCount = stackCounter(events);
        sortEventsByType(events, singleEvents, periods);
        if (stackCount > 13) {
            int iteration = stackCount - 13;
            for (int i = 0; i < iteration; i++) {
                if (periods.size() > singleEvents.size()) {
                    this.plotHeight += 50;
                } else {
                    this.plotHeight += 75;
                }
            }
        }
        this.margin = margin;
        this.tickLength = 10;
        this.timeLine = timeLine;
        int endDate;
        if (timeLine.getEndDate() == 3000) {
            endDate = 2021;
        } else {
            endDate = timeLine.getEndDate();
        }
        int duration = endDate - timeLine.getStartDate();
        if (duration < 500 && duration > 100) {
            this.plotWidth = (duration * 4) + 250;
        } else if (duration < 100) {
            this.plotWidth = (duration * 8) + 250;
        } else {
            this.plotWidth = duration + 250;
        }
        this.xAxis = xAxis;
        this.setPreferredSize(new Dimension(plotWidth, plotHeight));
        createEventPlots();
        setToolTipText("");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setFont(getFont().deriveFont(14f).deriveFont(Font.BOLD));
        createXAxis(g2d);
        createTimeLines(g2d);
    }

    private void createXAxis(Graphics2D g2d) {
        int height = plotHeight - margin;
        int width = plotWidth - margin * 4;
        int pixelGap = (width / (xAxis.length - 1));
        int duration;
        if (timeLine.getEndDate() == 3000) {
            duration = 2021 - timeLine.getStartDate();
        } else {
            duration = timeLine.getEndDate() - timeLine.getStartDate();
        }
        int x = margin + margin;
        int x1 = x;
        int y = height - tickLength;

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3f));
        FontMetrics metrics = g2d.getFontMetrics(getFont());
        for (int i = 0; i < xAxis.length; i++) {
            int a = x - metrics.stringWidth(xAxis[i]) / 2;
            int b = y + tickLength + metrics.getHeight() / 2 + metrics.getAscent();
            if (duration < 500) {
                if (i % 10 == 0) {
                    g2d.drawLine(x, y, x, y + tickLength);
                    g2d.drawString(xAxis[i], a, b);
                }
            } else {
                if (i % 100 == 0) {
                    g2d.drawLine(x, y, x, y + tickLength);
                    g2d.drawString(xAxis[i], a, b);
                }
            }
            if (xAxis[i].equals("0")) {
                g2d.drawLine(x, y, x, y + (tickLength * 2));
                g2d.drawString(xAxis[i], a, (b + 5));
            }
            x += pixelGap;
        }
        g2d.drawLine(x1, y, x - pixelGap, y);
    }

    private void createTimeLines(Graphics2D g2d) {
        int timelineStartDate = Round.roundDown(timeLine.getStartDate());
        ArrayList<Event> allEvents = timeLine.getEvents();
        ArrayList<Event> singleEvents = new ArrayList<>();
        ArrayList<Event> periods = new ArrayList<>();
        sortEventsByDate(allEvents);
        int height = plotHeight - margin;
        int width = plotWidth - margin * 4;
        int pixelGapH = (height / (allEvents.size() * 2)) + 10;
        int pixelGapW = width / (xAxis.length - 1);
        int x = margin + margin;

        int yEvent = height - 35;
        int yPeriod = height - 35 - pixelGapH;
        int yBase = height - 35;
        int periodStackCount = 1;

        g2d.setStroke(new BasicStroke(9f));
        FontMetrics metrics = g2d.getFontMetrics(getFont());
        sortEventsByType(allEvents, singleEvents, periods);
        for (int i = 0; i < singleEvents.size(); i++) {
            Event event = singleEvents.get(i);
            int startDate = event.getStart();

            int duration1 = startDate - timelineStartDate;
            int a1 = x + (duration1 * pixelGapW);

            String text = event.getName();
            int a = a1 - (metrics.stringWidth(text) / 2) - 4;
            int b = yEvent - 8;

            g2d.setColor(event.getColor());
            g2d.drawOval(a1, yEvent, 5, 5);
            g2d.setColor(Color.BLACK);
            g2d.drawString(text, a, b);

            if (i < singleEvents.size() - 1) {
                Event event2 = singleEvents.get(i + 1);
                if (event2.getStart() > (event.getStart() + 100)) {
                    yEvent = height - 35 + pixelGapH;
                    yPeriod += pixelGapH;
                }
            }
            yEvent -= pixelGapH;
            yPeriod -= (pixelGapH / 2);
        }
        for (int i = 0; i < periods.size(); i++) {
            Event event = periods.get(i);
            int startDate = event.getStart();
            int endDate;
            if (event.getEnd() == 3000) {
                endDate = 2021;
            } else {
                endDate = event.getEnd();
            }

            int duration1 = startDate - timelineStartDate;
            int duration2 = endDate - timelineStartDate;
            int a1 = x + (duration1 * pixelGapW);
            int a2 = x + (duration2 * pixelGapW);

            String text = event.getName();
            int a = a1 + ((a2 - a1) / 2) - (metrics.stringWidth(text) / 2) - 4;
            int b = yPeriod - 8;
            g2d.setColor(event.getColor());
            g2d.drawLine(a1, yPeriod, a2, yPeriod);
            g2d.setColor(Color.BLACK);
            g2d.drawString(text, a, b);

            if (i < periods.size() - 1) {
                Event event2 = periods.get(i + 1);
                if (event2.getStart() > (event.getEnd() + 70)) {
                    yPeriod = yBase;
                    periodStackCount = 1;
                } else {
                    periodStackCount++;
                }
                if (event2.getStart() > (event.getEnd() + 20) && periodStackCount > 6) {
                    yPeriod = yBase;
                    periodStackCount = 1;
                }
            }
            yPeriod -= pixelGapH;
        }
    }

    private void sortEventsByDate(ArrayList<Event> events) {
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2) {
                return event1.getStart().compareTo(event2.getStart());
            }
        });
    }

    private void sortEventsByType(ArrayList<Event> allEvents, ArrayList<Event> singleEvents, ArrayList<Event> periods) {
        for (int i = 0; i < allEvents.size(); i++) {
            Event event = allEvents.get(i);
            int startDate = event.getStart();
            int endDate = event.getEnd();

            if (startDate == endDate) {
                singleEvents.add(event);
            } else {
                periods.add(event);
            }
        }
    }

    private int stackCounter(ArrayList<Event> events) {
        int stackCount = 1;
        ArrayList<Integer> counts = new ArrayList<>();
        sortEventsByDate(events);
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            if (i < events.size() - 1) {
                Event event2 = events.get(i + 1);
                if (event2.getStart() > (event.getEnd() + 70)) {
                    counts.add(stackCount);
                    stackCount = 1;
                } else {
                    stackCount++;
                }
                if (event2.getStart() > (event.getEnd() + 20) && stackCount > 6) {
                    counts.add(stackCount);
                    stackCount = 1;
                }
            }
        }
        counts.add(stackCount);
        int count = Collections.max(counts);
        return count;
    }

    public ArrayList<EventPlot> getEventPlots() {
        return eventPlots;
    }

    public void addBar(Rect r) {
        bars.add(r);
    }

    public String getToolTipText(MouseEvent evt) {
        for (Rect temp : bars) {
            Rectangle bounds = new Rectangle(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
            if (bounds.contains(evt.getPoint())) {
                return temp.getToolTipText();
            }
        }
        return (String) null;
    }

    private void createEventPlots() {
        int timelineStartDate = Round.roundDown(timeLine.getStartDate());
        ArrayList<Event> allEvents = timeLine.getEvents();
        ArrayList<Event> singleEvents = new ArrayList<>();
        ArrayList<Event> periods = new ArrayList<>();
        sortEventsByDate(allEvents);
        int height = plotHeight - margin;
        int width = plotWidth - margin * 4;
        int pixelGapH = (height / (allEvents.size() * 2)) + 10;
        int pixelGapW = width / (xAxis.length - 1);
        int x = margin + margin;

        int yEvent = height - 35;
        int yPeriod = height - 35 - pixelGapH;
        int yBase = height - 35;
        int periodStackCount = 1;

        sortEventsByType(allEvents, singleEvents, periods);
        for (int i = 0; i < singleEvents.size(); i++) {
            Event event = singleEvents.get(i);
            int startDate = event.getStart();

            int duration1 = startDate - timelineStartDate;
            int a1 = x + (duration1 * pixelGapW);

            eventPlots.add(new EventPlot(a1, yEvent, 5, 5, event.getStart(), event.getEnd()));

            if (i < singleEvents.size() - 1) {
                Event event2 = singleEvents.get(i + 1);
                if (event2.getStart() > (event.getStart() + 100)) {
                    yEvent = height - 35 + pixelGapH;
                    yPeriod += pixelGapH;
                }
            }
            yEvent -= pixelGapH;
            yPeriod -= (pixelGapH / 2);
        }
        for (int i = 0; i < periods.size(); i++) {
            Event event = periods.get(i);
            int startDate = event.getStart();
            int endDate;
            if (event.getEnd() == 3000) {
                endDate = 2021;
            } else {
                endDate = event.getEnd();
            }

            int duration1 = startDate - timelineStartDate;
            int duration2 = endDate - timelineStartDate;
            int a1 = x + (duration1 * pixelGapW);
            int a2 = x + (duration2 * pixelGapW);

            eventPlots.add(new EventPlot(a1, yPeriod, a2, yPeriod, startDate, endDate));
            if (i < periods.size() - 1) {
                Event event2 = periods.get(i + 1);
                if (event2.getStart() > (event.getEnd() + 70)) {
                    yPeriod = yBase;
                    periodStackCount = 1;
                } else {
                    periodStackCount++;
                }
                if (event2.getStart() > (event.getEnd() + 20) && periodStackCount > 6) {
                    yPeriod = yBase;
                    periodStackCount = 1;
                }
            }
            yPeriod -= pixelGapH;
        }
    }
}