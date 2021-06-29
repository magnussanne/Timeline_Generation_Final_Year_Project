package common.entities;

public class ScrapeResult {
    private final String name;

    private final int startDate;
    private final int endDate;

    public ScrapeResult(String name, int startDate, int endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public int getStartDate() {
        return startDate;
    }

    public int getEndDate() {
        return endDate;
    }

}