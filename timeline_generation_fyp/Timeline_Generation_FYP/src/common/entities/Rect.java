package common.entities;

import javax.swing.JComponent;

public class Rect extends JComponent {
    private static final long serialVersionUID = 1L;

    private int x;
    private int y;
    private int width;
    private int height;
    private String toolTip;

    public Rect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        toolTip = "";
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

    public void setToolTipText(String text) {
        this.toolTip = text;
    }

    public String getToolTipText() {
        return toolTip;
    }
}
