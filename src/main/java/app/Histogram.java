package app;

import javafx.scene.image.Image;

import java.util.List;

public class Histogram {
    private final Image image;

    public Histogram(Image image) {
        this.image = image;
    }

    public List<List<Integer>> asColoredMatrix() {
        image.getPixelReader();
        return null;
    }

}
