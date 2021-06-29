package common.entities;

import java.util.ArrayList;

public class TimeLine {

    private ArrayList<Event> events;

    public TimeLine() {
        this.events = new ArrayList<>();
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public int getStartDate() {
        int startDate = 0;

        for (Event event : events) {
            if (startDate == 0 || event.getStart() < startDate) {
                startDate = event.getStart();
            }
        }

        return startDate;
    }

    public int getEndDate() {
        int endDate = 0;

        for (Event event : events) {
            if (endDate == 0 || event.getEnd() > endDate) {
                endDate = event.getEnd();
            }
        }

        return endDate;
    }

}