package app;

public class Histogram {
    private final int red[];
    private final int green[];
    private final int blue[];

    public Histogram(int[] red, int[] green, int[] blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int[] red() {
        return red;
    }

    public int[] green() {
        return green;
    }

    public int[] blue() {
        return blue;
    }

}
