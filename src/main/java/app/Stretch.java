package app;

import javafx.scene.image.Image;

import java.util.Map;

public class Stretch {

    private ImageConverter imageConverter = new ImageConverter();

    public Image stretch(Image image) {
        Map<Integer, Map<Integer, Canals>> imageMap = imageConverter.toCanals(image);

        return imageConverter.toImage(imageMap);
    }
}
