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
