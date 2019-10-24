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

        return imageConverter.toImage(imageMap);
    }
}
