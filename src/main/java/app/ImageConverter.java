package app;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import java.util.HashMap;
import java.util.Map;

public class ImageConverter {

    public Map<Integer, Map<Integer, Canals>> toCanals(Image image) {
        Map<Integer, Map<Integer, Canals>> imageMap = new HashMap<>();

        PixelReader pixelReader = image.getPixelReader();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = pixelReader.getArgb(x, y);
                int a = (0xff & (argb >> 24));
                int r = (0xff & (argb >> 16));
                int g = (0xff & (argb >> 8));
                int b = (0xff & argb);

                HashMap<Integer, Canals> map = new HashMap<>();
                map.put(y, new Canals(r, g, b));
                imageMap.put(x, map);
            }
        }
        return imageMap;
    }

    public Image toImage(Map<Integer, Map<Integer, Canals>> imageMap) {
        return null;
    }

}
