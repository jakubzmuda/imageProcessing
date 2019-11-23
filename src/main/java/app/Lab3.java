package app;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Lab3 {

    private Image image;
    private ImageView imageView;
    private StackPane histogramPane;

    public Lab3(Image image) {
        this.image = image;
    }

    public void smoothing() {
        Stage stage = new Stage();
        stage.setTitle("Wygładzanie");

        HBox masks = buildMasks();
        VBox preview = buildPreview();
        HBox content = new HBox(masks, preview);
        content.setSpacing(16);
        content.setAlignment(Pos.CENTER);

        Button doIt = new Button("Kontynuuj");
        doIt.setOnAction((event) -> {
            Image newImage = new ImageOperations().negate(image);
            updateImageAndHistogram(newImage);
            stage.close();
        });

        VBox container = new VBox(content, doIt);

        Scene scene = new Scene(container, 1200, 1000);

        container.setAlignment(Pos.CENTER);
        container.setSpacing(16);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildPreview() {
        this.imageView = new ImageView(image);
        StackPane stackImageView = new StackPane(imageView);
        stackImageView.setAlignment(Pos.CENTER);
        ScrollPane imageContainer = buildImageContainer(stackImageView);

        this.histogramPane = new StackPane(buildHistogram());

        return new VBox(imageContainer, histogramPane);
    }

    private HBox buildMasks() {
        Mask mask1 = new Mask(0, 1, 0, 1, 4, 1, 0, 1, 0);
        Button chooseMask1Button = new Button("Wybierz");
        chooseMask1Button.setOnAction(e -> {
            Image newImage = new ImageOperations().smoothWithMask(image, mask1);
            updateImageAndHistogram(newImage);
        });
        VBox mask1Box = new VBox(mask1.asTable(), chooseMask1Button);
        mask1Box.setAlignment(Pos.CENTER);
        mask1Box.setStyle("-fx-padding: 16px;");

        Mask mask2 = new Mask(1, 1, 1, 1, 1, 1, 1, 1, 1);
        Button chooseMask2Button = new Button("Wybierz");
        chooseMask2Button.setOnAction(e -> {
            Image newImage = new ImageOperations().smoothWithMask(image, mask2);
            updateImageAndHistogram(newImage);
        });
        VBox mask2Box = new VBox(mask2.asTable(), chooseMask2Button);
        mask2Box.setAlignment(Pos.CENTER);
        mask2Box.setStyle("-fx-padding: 16px;");

        Mask mask3 = new Mask(1, 2, 1, 2, 4, 2, 1, 2, 1);
        Button chooseMask3Button = new Button("Wybierz");
        VBox mask3Box = new VBox(mask3.asTable(), chooseMask3Button);
        chooseMask3Button.setOnAction(e -> {
            Image newImage = new ImageOperations().smoothWithMask(image, mask3);
            updateImageAndHistogram(newImage);
        });
        mask3Box.setAlignment(Pos.CENTER);
        mask3Box.setStyle("-fx-padding: 16px;");

        return new HBox(mask1Box, mask2Box, mask3Box);
    }

    private void updateImageAndHistogram(Image newImage) {
        image = newImage;
        imageView.setImage(newImage);
        this.histogramPane.getChildren().clear();
        this.histogramPane.getChildren().add(buildHistogram());
    }

    private ScrollPane buildImageContainer(Pane imageView) {
        ScrollPane imageContainer = new ScrollPane(imageView);
        imageContainer.setPrefWidth(600);
        imageContainer.setPrefHeight(600);
        imageContainer.setFitToHeight(true);
        imageContainer.setFitToWidth(true);
        return imageContainer;
    }

    private BarChart<String, Number> buildHistogram() {
        HistogramPainter histogramPainter = new HistogramPainter(image);
        BarChart<String, Number> histogram = histogramPainter.paintChart();

        histogram.getData().addAll(
                histogramPainter.getSeriesRed(),
                histogramPainter.getSeriesGreen(),
                histogramPainter.getSeriesBlue());

        return histogram;
    }
}
