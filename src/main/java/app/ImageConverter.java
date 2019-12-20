package app;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import java.awt.image.BufferedImage;

public class ImageConverter {

    public ImageMap toImageMap(Image image) {
        ImageMap imageMap = new ImageMap();

        PixelReader pixelReader = image.getPixelReader();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = pixelReader.getArgb(x, y);
                int a = (0xff & (argb >> 24));
                int r = (0xff & (argb >> 16));
                int g = (0xff & (argb >> 8));
                int b = (0xff & argb);
                imageMap.put(x, y, new Canals(r, g, b));
            }
        }
        return imageMap;
    }

    public Image toImage(ImageMap imageMap) {
        int height = imageMap.height();
        int width = imageMap.width();

        return buildImage(width, height, imageMap);
    }

    public Image buildImage(int width, int height, ImageMap imageMap) {
        int[] data = new int[width * height];
        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Canals entry = imageMap.get(x, y);
                int red = entry.red;
                int green = entry.green;
                int blue = entry.blue;
                data[i++] = (red << 16) | (green << 8) | blue;
            }
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, data, 0, width);

        return SwingFXUtils.toFXImage(image, null);
    }

}
