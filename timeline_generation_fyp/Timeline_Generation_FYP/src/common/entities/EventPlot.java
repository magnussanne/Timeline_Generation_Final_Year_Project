package common.entities;

public class EventPlot {

    private final int x;

    private final int y;
    private final int width;
    private final int height;
    private final int start;
    private final int end;

    public EventPlot(int x, int y, int width, int height, int start, int end) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.start = start;
        this.end = end;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
