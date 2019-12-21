package app;

import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageSerializer {

    public void saveToFile(Image image) {
//        FileChooser fileChooser = new FileChooser(); //nocommit
//        File outputFile = fileChooser.showOpenDialog(null);
//        File outputFile = new File("/Users/jakubzmuda/hackz/imageProcessing/output.bmp");
//        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        Mat mat = ImageUtils.imageToMat(image);
            Imgcodecs.imwrite("output.bmp", mat);
    }
}
