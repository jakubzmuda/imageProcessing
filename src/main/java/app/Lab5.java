package app;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
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

    ImageView inputImageView;

    public Lab5(Image image, App app) {
        this.image = image;
        this.app = app;
        this.inputImageView = new ImageView(image);
    }

    public void segmentationGrow() {

    }

    public void segmentationSplitAndMerge() {
        Stage stage = new Stage();
        stage.setTitle("Segmentacja: Dziel i łącz");

        HBox inputImage = buildInputImageBox();

        VBox container = new VBox(inputImage);

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

    private StackPane buildImageContainer(ImageView imageView) {
        int size = 300;
        imageView.setFitHeight(size);
        imageView.setFitWidth(size);
        StackPane stackImageView = new StackPane(imageView);
        ScrollPane imageContainer = new ScrollPane(imageView);
        imageContainer.setPrefWidth(size);
        imageContainer.setMaxWidth(size);
        imageContainer.setPrefHeight(size);
        imageContainer.setMaxHeight(size);
        imageContainer.setFitToHeight(true);
        imageContainer.setFitToWidth(true);
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
