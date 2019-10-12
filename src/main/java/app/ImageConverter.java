package app;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ImageConverter {

    public Map<Integer, Map<Integer, Canals>> toCanals(Image image) {
        Map<Integer, Map<Integer, Canals>> imageMap = new HashMap<>();

        PixelReader pixelReader = image.getPixelReader();
        for (int y = 0; y < image.getHeight(); y++) {
            HashMap<Integer, Canals> map = new HashMap<>();

            for (int x = 0; x < image.getWidth(); x++) {
                int argb = pixelReader.getArgb(x, y);
                int a = (0xff & (argb >> 24));
                int r = (0xff & (argb >> 16));
                int g = (0xff & (argb >> 8));
                int b = (0xff & argb);

                map.put(x, new Canals(r, g, b));
            }
            imageMap.put(y, map);
        }
        return imageMap;
    }

    public Image toImage(Map<Integer, Map<Integer, Canals>> imageMap) {
        int width = imageMap.size();
        int height = imageMap.get(0).size();

        return buildImage(width, height, imageMap);
    }

    public Image buildImage(int width, int height, Map<Integer, Map<Integer, Canals>> imageMap) {
        int[] data = new int[width * height];
        int i = 0;
        for (int y = 0; y < height; y++) {
            int red = (y * 255) / (height - 1);
            for (int x = 0; x < width; x++) {
                int green = (x * 255) / (width - 1);
                int blue = 128;
                data[i++] = (red << 16) | (green << 8) | blue;
            }
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, data, 0, width);

        return SwingFXUtils.toFXImage(image, null);
    }

}
