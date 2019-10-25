package app;

import java.util.Arrays;
import java.util.stream.IntStream;

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

    public Canals sum() {
        return new Canals(IntStream.of(red).sum(), IntStream.of(green).sum(), IntStream.of(blue).sum());
    }

    public Canals sumToThreshold(Canals threshold) {
        int r = IntStream.of(Arrays.copyOf(red, threshold.red)).sum();
        int g = IntStream.of(Arrays.copyOf(green, threshold.green)).sum();
        int b = IntStream.of(Arrays.copyOf(blue, threshold.blue)).sum();
        return new Canals(r, g, b);
    }
}
