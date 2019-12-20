package app;

import org.opencv.core.Mat;

public interface IMask {

    int getKernelSize();
    Mat getMat();

}
