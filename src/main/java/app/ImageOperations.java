package app;

import io.vavr.Tuple;
import io.vavr.Tuple3;
import javafx.scene.image.Image;

import java.util.stream.IntStream;

public class ImageOperations {

    private ImageConverter imageConverter = new ImageConverter();

    public Image negate(Image image) {
        ImageMap imageMap = imageConverter.toImageMap(image);
        imageMap.singlePointOperation((x, y, canals) -> {
            return new Canals(255 - canals.red, 255 - canals.green, 255 - canals.blue);
        });

        return imageConverter.toImage(imageMap);
    }

    public Image stretchHistogram(Image image) {
        ImageMap imageMap = imageConverter.toImageMap(image);

        Canals maxValues = imageMap.maxColorCanalValues();
        Canals minValues = imageMap.minColorValues();

        imageMap.singlePointOperation((x, y, canals) -> {
            int redValue = stretchSingleColorCanal(canals.red, maxValues.red, minValues.red);
            int greenValue = stretchSingleColorCanal(canals.green, maxValues.green, minValues.green);
            int blueValue = stretchSingleColorCanal(canals.blue, maxValues.blue, minValues.blue);
            return new Canals(redValue, greenValue, blueValue);
        });

        return imageConverter.toImage(imageMap);
    }

    public Image equalizeHistogram(Image image) {
        ImageMap imageMap = imageConverter.toImageMap(image);

        Histogram histogram = imageMap.histogram();

        imageMap.singlePointOperation((x, y, canals) -> {
            Tuple3<Float, Float, Float> distribution = cumulativeDistribution(histogram, canals);
            Tuple3<Float, Float, Float> firstNotZeroDistribution = firstNotZeroDistribution(histogram);
            int r = equalizeSingleColorCanal(distribution._1(), firstNotZeroDistribution._1());
            int g = equalizeSingleColorCanal(distribution._2(), firstNotZeroDistribution._2());
            int b = equalizeSingleColorCanal(distribution._3(), firstNotZeroDistribution._3());
            return new Canals(r, g, b);
        });

        return imageConverter.toImage(imageMap);
    }

    public Image threshold(Image image, int threshold, int min, int max) {
        ImageMap imageMap = imageConverter.toImageMap(image);
        imageMap.singlePointOperation((x, y, canals) -> {
            return new Canals(canals.red > threshold ? max : min, canals.green > threshold ? max : min, canals.blue > threshold ? max : min);
        });

        return imageConverter.toImage(imageMap);
    }

    public Image thresholdWithPreservation(Image image, int from, int to, int min) {
        ImageMap imageMap = imageConverter.toImageMap(image);
        imageMap.singlePointOperation((x, y, canals) -> {
            return new Canals(
                    canals.red > from && canals.red < to ? canals.red : min,
                    canals.green > from && canals.green < to ? canals.green : min,
                    canals.blue > from && canals.blue < to ? canals.blue : min);
        });

        return imageConverter.toImage(imageMap);
    }

    public Image levelReduction(Image image, int[] p, int[] q) {
        ImageMap imageMap = imageConverter.toImageMap(image);
        imageMap.singlePointOperation((x, y, canals) -> {
            int red = reduceLevelForColorCanal(canals.red, p, q);
            int green = reduceLevelForColorCanal(canals.green, p, q);
            int blue = reduceLevelForColorCanal(canals.blue, p, q);

            return new Canals(red, green, blue);
        });

        return imageConverter.toImage(imageMap);
    }

    public Image spreadRange(Image image, int p1, int p2, int q1, int q2) {
        ImageMap imageMap = imageConverter.toImageMap(image);
        imageMap.singlePointOperation((x, y, canals) -> {
            int r = spreadRangeSingleColorCanal(canals.red, p1, p2, q1, q2);
            int g = spreadRangeSingleColorCanal(canals.green, p1, p2, q1, q2);
            int b = spreadRangeSingleColorCanal(canals.blue, p1, p2, q1, q2);
            return new Canals(r, g, b);
        });

        return imageConverter.toImage(imageMap);
    }

    private int spreadRangeSingleColorCanal(int color, int p1, int p2, int q1, int q2) {
        float ratioP = (float) color / p2;
        float deltaQ = (float) q2 - q1;

        return Math.round(color <= p2 && color >= p1 ? (q1 + ratioP * deltaQ) : color);
    }

    private int reduceLevelForColorCanal(int color, int[] p, int[] q) {
        for (int i = 0; i < p.length; i++) {
            if (color < p[i]) {
                return q[i];
            }
        }
        return q[q.length - 1];
    }

    private int stretchSingleColorCanal(int current, int max, int min) {
        return (current - min) * (255 / max - min);
    }

    private int equalizeSingleColorCanal(float distribution, float firstNotZeroDistribution) {
        return Math.round((distribution - firstNotZeroDistribution / (1 - firstNotZeroDistribution)) * (255 - 1));
    }

    private Tuple3<Float, Float, Float> cumulativeDistribution(Histogram histogram, Canals threshold) {
        Canals sum = histogram.sum();
        Canals sumToThreshold = histogram.sumToThreshold(threshold);

        float distR = (float) sumToThreshold.red / sum.red;
        float distG = (float) sumToThreshold.green / sum.green;
        float distB = (float) sumToThreshold.blue / sum.blue;

        return Tuple.of(distR, distG, distB);
    }

    private Tuple3<Float, Float, Float> firstNotZeroDistribution(Histogram histogram) {
        float r = IntStream.range(0, histogram.red().length)
                .filter(i -> histogram.red()[i] != 0)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No non zero red fields in histogram"));
        float g = IntStream.range(0, histogram.green().length)
                .filter(i -> histogram.green()[i] != 0)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No non zero green fields in histogram"));
        float b = IntStream.range(0, histogram.blue().length)
                .filter(i -> histogram.blue()[i] != 0)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No non zero blue fields in histogram"));

        return Tuple.of(r, g, b);
    }
}
