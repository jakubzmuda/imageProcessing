package app;

import javafx.scene.image.Image;

import java.util.Map;

public class ImageOperations {

    private ImageConverter imageConverter = new ImageConverter();

    public Image stretch(Image image) {
        Map<Integer, Map<Integer, Canals>> imageMap = imageConverter.toCanals(image);

        return imageConverter.toImage(imageMap);
    }

    public Image negate(Image image) {
        Map<Integer, Map<Integer, Canals>> imageMap = imageConverter.toCanals(image);
        imageMap.forEach((x, value) -> {
            value.forEach((y, canals) -> {
                value.put(y, new Canals(255-canals.red, 255-canals.green, 255-canals.blue));
            });
        });

        return imageConverter.toImage(imageMap);
    }
}
