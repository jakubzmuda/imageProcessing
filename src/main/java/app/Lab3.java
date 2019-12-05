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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Lab3 {

    private final App app;
    private Image image;
    private ImageView imageView;
    private StackPane histogramPane;
    private Mask selectedMask;
    private BorderOperationStrategy selectedBorderOperationStrategy = BorderOperationStrategy.DUPLICATE;

    public Lab3(Image image, App app) {
        this.image = image;
        this.app = app;
    }

    public void smoothing() {
        Stage stage = new Stage();
        stage.setTitle("Wygładzanie");

        VBox masks = buildMasks();
        VBox strategy = buildStrategy();
        VBox leftSideContainer = new VBox(masks, strategy);
        VBox preview = buildPreview();
        HBox content = new HBox(leftSideContainer, preview);
        content.setSpacing(16);
        content.setAlignment(Pos.CENTER);

        Button doIt = new Button("Kontynuuj");
        doIt.setOnAction((event) -> {
            Image newImage = new ImageOperations().smoothWithMask(image, selectedMask, selectedBorderOperationStrategy);
            updateImageAndHistogram(newImage);
            app.updateImage(newImage);
            stage.close();
        });

        VBox container = new VBox(content, doIt);

        Scene scene = new Scene(container, 1200, 1000);

        container.setAlignment(Pos.CENTER);
        container.setSpacing(16);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildStrategy() {
        Label strategyLabel = new Label("Strategia: ");
        Label selectedStrategyLabel = new Label(this.selectedBorderOperationStrategy.description());
        HBox header = new HBox(strategyLabel, selectedStrategyLabel);
        Button button1 = new Button(BorderOperationStrategy.DUPLICATE.description());
        button1.setOnAction(event -> {
            BorderOperationStrategy strategy = BorderOperationStrategy.DUPLICATE;
            this.selectedBorderOperationStrategy = strategy;
            selectedStrategyLabel.setText(strategy.description());
        });

        Button button2 = new Button(BorderOperationStrategy.EXISTING_ONLY.description());
        button2.setOnAction(event -> {
            BorderOperationStrategy strategy = BorderOperationStrategy.EXISTING_ONLY;
            this.selectedBorderOperationStrategy = strategy;
            selectedStrategyLabel.setText(strategy.description());
        });

        Button button3 = new Button(BorderOperationStrategy.NO_CHANGE.description());
        button3.setOnAction(event -> {
            BorderOperationStrategy strategy = BorderOperationStrategy.NO_CHANGE;
            this.selectedBorderOperationStrategy = strategy;
            selectedStrategyLabel.setText(strategy.description());
        });
        return new VBox(header, button1, button2, button3);
    }

    private VBox buildPreview() {
        this.imageView = new ImageView(image);
        StackPane stackImageView = new StackPane(imageView);
        stackImageView.setAlignment(Pos.CENTER);
        ScrollPane imageContainer = buildImageContainer(stackImageView);

        this.histogramPane = new StackPane(buildHistogram());

        return new VBox(imageContainer, histogramPane);
    }

    private VBox buildMasks() {
        Mask mask1 = new Mask(0, 1, 0, 1, 4, 1, 0, 1, 0);
        Button chooseMask1Button = new Button("Wybierz");
        chooseMask1Button.setOnAction(e -> {
            Image newImage = new ImageOperations().smoothWithMask(image, mask1, this.selectedBorderOperationStrategy);
            updateImageAndHistogram(newImage);
            this.selectedMask = mask1;
        });
        VBox mask1Box = new VBox(mask1.asTable(), chooseMask1Button);
        mask1Box.setAlignment(Pos.CENTER);
        mask1Box.setStyle("-fx-padding: 16px;");

        Mask mask2 = new Mask(1, 1, 1, 1, 1, 1, 1, 1, 1);
        Button chooseMask2Button = new Button("Wybierz");
        chooseMask2Button.setOnAction(e -> {
            Image newImage = new ImageOperations().smoothWithMask(image, mask2, this.selectedBorderOperationStrategy);
            updateImageAndHistogram(newImage);
            this.selectedMask = mask2;
        });
        VBox mask2Box = new VBox(mask2.asTable(), chooseMask2Button);
        mask2Box.setAlignment(Pos.CENTER);
        mask2Box.setStyle("-fx-padding: 16px;");

        Mask mask3 = new Mask(1, 2, 1, 2, 4, 2, 1, 2, 1);
        Button chooseMask3Button = new Button("Wybierz");
        VBox mask3Box = new VBox(mask3.asTable(), chooseMask3Button);
        chooseMask3Button.setOnAction(e -> {
            Image newImage = new ImageOperations().smoothWithMask(image, mask3, this.selectedBorderOperationStrategy);
            updateImageAndHistogram(newImage);
            this.selectedMask = mask3;
        });
        mask3Box.setAlignment(Pos.CENTER);
        mask3Box.setStyle("-fx-padding: 16px;");

        Mask mask4 = new Mask(1, 1, 1, 1, 1, 1, 1, 1, 1);
        Button chooseMask4Button = new Button("Wybierz");
        VBox mask4Box = new VBox(mask4.asInteractiveTable(), chooseMask4Button);
        chooseMask3Button.setOnAction(e -> {
            Image newImage = new ImageOperations().smoothWithMask(image, mask4, this.selectedBorderOperationStrategy);
            updateImageAndHistogram(newImage);
            this.selectedMask = mask4;
        });
        mask4Box.setAlignment(Pos.CENTER);
        mask4Box.setStyle("-fx-padding: 16px;");

        Mask mask5 = new Mask(0, 1, 0, 1, 1, 1, 0, 1, 0);
        Button chooseMask5Button = new Button("Wybierz");
        VBox mask5Box = new VBox(mask5.asInteractiveTable(), chooseMask5Button);
        chooseMask3Button.setOnAction(e -> {
            Image newImage = new ImageOperations().smoothWithMask(image, mask5, this.selectedBorderOperationStrategy);
            updateImageAndHistogram(newImage);
            this.selectedMask = mask5;
        });
        mask5Box.setAlignment(Pos.CENTER);
        mask5Box.setStyle("-fx-padding: 16px;");

        HBox hBox1 = new HBox(mask1Box, mask2Box, mask3Box);
        HBox hBox2 = new HBox(mask4Box, mask5Box);
        return new VBox(hBox1, hBox2);
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
