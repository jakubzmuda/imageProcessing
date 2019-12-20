package app;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Lab5 {

    private final Image image;
    private final App app;

    private ImageView inputImageView;
    private ImageView outputImageView;
    private Image outputImage;

    public Lab5(Image inputImage, App app) {
        this.image = inputImage;
        this.app = app;
        this.inputImageView = new ImageView(inputImage);
        this.outputImage = new ImageOperations().segmentationSplitAndMerge(inputImage);
        this.outputImageView = new ImageView(outputImage);
    }

    public void segmentationGrow() {

    }

    public void segmentationSplitAndMerge() {
        Stage stage = new Stage();
        stage.setTitle("Segmentacja: Dziel i łącz");

        HBox inputBox = buildInputImageBox();
        HBox outputBox = buildOutputImageBox();


        Button doIt = new Button("Kontynuuj");
        doIt.setOnAction((event) -> {
            app.updateImage(outputImage);
            stage.close();
        });

        VBox container = new VBox(inputBox, outputBox, doIt);
        container.setSpacing(32);

        Scene scene = new Scene(container, 1200, 1000);

        container.setAlignment(Pos.CENTER);
        container.setSpacing(16);
        stage.setScene(scene);
        stage.show();
    }


    private HBox buildInputImageBox() {
        HBox box = new HBox(new Label("Obraz wejściowy"), buildImageContainer(this.inputImageView), buildHistogram(image));
        box.setAlignment(Pos.CENTER);
        box.setSpacing(16);
        return box;
    }

    private HBox buildOutputImageBox() {
        HBox box = new HBox(new Label("Obraz wynikowy"), buildImageContainer(this.outputImageView), buildHistogram(outputImage));
        box.setAlignment(Pos.CENTER);
        box.setSpacing(16);
        return box;
    }

    private StackPane buildImageContainer(ImageView imageView) {
        int height = 300;
        imageView.setFitHeight(height);
        StackPane stackImageView = new StackPane(imageView);
        ScrollPane imageContainer = new ScrollPane(imageView);
        imageContainer.setPrefHeight(height);
        imageContainer.setMaxHeight(height);
        imageContainer.setFitToHeight(true);
        stackImageView.setAlignment(Pos.CENTER);
        return stackImageView;
    }

    private BarChart<String, Number> buildHistogram(Image image) {
        HistogramPainter histogramPainter = new HistogramPainter(image);
        BarChart<String, Number> histogram = histogramPainter.paintChart();

        histogram.setMaxWidth(300);
        histogram.setMaxHeight(300);

        histogram.getData().addAll(
                histogramPainter.getSeriesRed(),
                histogramPainter.getSeriesGreen(),
                histogramPainter.getSeriesBlue());

        return histogram;
    }
}
