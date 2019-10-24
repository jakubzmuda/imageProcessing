package app;

import javafx.scene.image.Image;

public class ImageOperations {

    private ImageConverter imageConverter = new ImageConverter();

    public Image negate(Image image) {
        ImageMap imageMap = imageConverter.toImageMap(image);
        imageMap.singlePointOperation((x, y, canals) -> {
            imageMap.put(x, y, new Canals(255 - canals.red, 255 - canals.green, 255 - canals.blue));
            return null;
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
            imageMap.put(x, y, new Canals(redValue, greenValue, blueValue));
            return null;
        });

        return imageConverter.toImage(imageMap);
    }

    private int stretchSingleColorCanal(int current, int max, int min) {
        return (current - min) * (255 / max - min);
    }
}
