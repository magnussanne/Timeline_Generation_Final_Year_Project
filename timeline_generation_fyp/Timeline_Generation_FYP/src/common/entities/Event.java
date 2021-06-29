package common.entities;

import java.awt.Color;

public class Event {

    private Color color;

    private final String name;

    private final Integer startDate;
    private final Integer endDate;
    private final String link;

    public Event(String name, Color color, Integer startDate, Integer endDate, String link) {
        this.name = name;
        this.color = color;
        this.startDate = startDate;
        this.endDate = endDate;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Integer getStart() {
        return startDate;
    }

    public Integer getEnd() {
        return endDate;
    }

    public String getLink() {
        return link;
    }
}