package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class App extends Application {

    private static int appWidth = 800;
    private static int appHeight = 600;
    private static long imageContainerWidth = Math.round(appWidth);

    private Image image;
    private ImageView imageView;
    private BarChart<String, Number> barChart;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        VBox vBox = buildMainBox();
        primaryStage.setTitle("Image processing");
        primaryStage.setScene(new Scene(vBox, appWidth, appHeight));
        primaryStage.setResizable(false);
        primaryStage.show();
        this.primaryStage = primaryStage;
    }

    private VBox buildMainBox() {
        ImageView imageView = new ImageView();
        this.imageView = imageView;
        StackPane stackImageView = new StackPane(imageView);
        MenuBar menuBar = buildMenuBox(imageView);
        VBox mainBox = new VBox(menuBar);
        ScrollPane imageContainer = buildImageContainer(stackImageView);

        imageContainer.setFitToHeight(true);
        imageContainer.setFitToWidth(true);

        GridPane gridPane = new GridPane();
        gridPane.add(imageContainer, 0, 0);

        gridPane.setAlignment(Pos.TOP_CENTER);

        mainBox.getChildren().add(gridPane);
        return mainBox;
    }

    private ScrollPane buildImageContainer(Pane imageView) {
        ScrollPane imageContainer = new ScrollPane(imageView);
        imageContainer.setPrefWidth(imageContainerWidth);
        imageContainer.setPrefHeight(appHeight);
        return imageContainer;
    }

    private MenuBar buildMenuBox(ImageView imageView) {
        Menu fileMenu = buildFileMenuTab(imageView);
        Menu lab1Menu = buildLab1MenuTab();
        Menu lab2Menu = buildLab2MenuTab();
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, lab1Menu, lab2Menu);
        return menuBar;
    }

    private Menu buildLab1MenuTab() {
        MenuItem histogramItem = buildHistogramMenuItem();
        Menu menu = new Menu("Lab 1");
        menu.getItems().addAll(histogramItem);
        return menu;
    }

    private Menu buildLab2MenuTab() {
        Menu menu = new Menu("Lab 2");
        MenuItem stretchImageItem = new MenuItem("Rozciągnięcie histogramu");
        stretchImageItem.setOnAction(e -> {
            Image newImage = new ImageOperations().stretchHistogram(image);
            updateImage(newImage);
        });
        MenuItem equalizeImageItem = new MenuItem("Wyrównanie histogramu");
        equalizeImageItem.setOnAction(e -> {
            Image newImage = new ImageOperations().equalizeHistogram(image);
            updateImage(newImage);
        });
        MenuItem negateImageItem = new MenuItem("Negacja");
        negateImageItem.setOnAction(e -> {
            Image newImage = new ImageOperations().negate(image);
            updateImage(newImage);
        });
        MenuItem thresholdingItem = new MenuItem("Progowanie");
        thresholdingItem.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setTitle("Progowanie");

            GridPane container = new GridPane();
            Scene scene = new Scene(container, 300, 200);

            Label thresholdLabel = new Label("próg ");
            TextField thresholdField = new TextField("128");
            container.add(thresholdLabel, 0, 0);
            container.add(thresholdField, 1, 0);

            Label minLabel = new Label("min ");
            TextField minField = new TextField("0");
            container.add(minLabel, 0, 1);
            container.add(minField, 1, 1);

            Label maxLabel = new Label("max ");
            TextField maxField = new TextField("255");
            container.add(maxLabel, 0, 2);
            container.add(maxField, 1, 2);

            Button doIt = new Button("Kontynuuj");
            doIt.setOnAction((event) -> {
                Image newImage = new ImageOperations().threshold(image, Integer.parseInt(thresholdField.getText()), Integer.parseInt(minField.getText()), Integer.parseInt(maxField.getText()));
                updateImage(newImage);
                stage.close();
            });

            container.setAlignment(Pos.CENTER);
            container.add(doIt, 0, 3);
            stage.setScene(scene);
            stage.show();
        });

        MenuItem thresholdingWithPreservationItem = new MenuItem("Progowanie z zachowaniem poziomów szarości");
        thresholdingWithPreservationItem.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setTitle("Progowanie");

            GridPane container = new GridPane();
            Scene scene = new Scene(container, 300, 200);

            Label thresholdFromLabel = new Label("od ");
            TextField thresholdFromField = new TextField("32");
            container.add(thresholdFromLabel, 0, 0);
            container.add(thresholdFromField, 1, 0);

            Label thresholdToLabel = new Label("do ");
            TextField thresholdToField = new TextField("64");
            container.add(thresholdToLabel, 0, 1);
            container.add(thresholdToField, 1, 1);


            Label minLabel = new Label("min ");
            TextField minField = new TextField("0");
            container.add(minLabel, 0, 2);
            container.add(minField, 1, 2);

            Button doIt = new Button("Kontynuuj");
            doIt.setOnAction((event) -> {
                Image newImage = new ImageOperations().thresholdWithPreservation(image,
                        Integer.parseInt(thresholdFromField.getText()),
                        Integer.parseInt(thresholdToField.getText()),
                        Integer.parseInt(minField.getText()));
                updateImage(newImage);
                stage.close();
            });

            container.setAlignment(Pos.CENTER);
            container.add(doIt, 0, 3);
            stage.setScene(scene);
            stage.show();

        });

        menu.getItems().addAll(stretchImageItem, negateImageItem);
        return menu;
    }

    private void updateImage(Image newImage) {
        image = newImage;
        imageView.setImage(newImage);
    }

    private Menu buildFileMenuTab(ImageView imageView) {
        MenuItem openImageItem = buildOpenImageMenuItem(imageView);
        MenuItem closeItem = buildCloseAppMenuItem();
        Menu menu = new Menu("Plik");
        menu.getItems().addAll(openImageItem, closeItem);
        return menu;
    }

    private MenuItem buildCloseAppMenuItem() {
        MenuItem closeItem = new MenuItem("Zamknij");
        closeItem.setOnAction(e -> {
            Platform.exit();
        });
        return closeItem;
    }

    private MenuItem buildHistogramMenuItem() {
        MenuItem closeItem = new MenuItem("Histogram");
        closeItem.setOnAction(e -> {
            this.printHistogram();
        });
        return closeItem;
    }

    private MenuItem buildOpenImageMenuItem(ImageView imageView) {
        MenuItem openImageItem = new MenuItem("Otwórz");

        openImageItem.setOnAction(t -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
//            ClassLoader classLoader = getClass().getClassLoader(); // fast load
//            File file = new File(classLoader.getResource("niedzkol.bmp").getFile());

            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                updateImage(image);
            } catch (IOException ignored) {
            }
        });
        return openImageItem;
    }

    private void printHistogram() {
        HistogramPainter histogramPainter = new HistogramPainter(image);
        this.barChart = histogramPainter.paintChart();
        StackPane container = new StackPane(barChart);
        barChart.getData().addAll(
                histogramPainter.getSeriesRed(),
                histogramPainter.getSeriesGreen(),
                histogramPainter.getSeriesBlue());

        Stage stage = new Stage();
        stage.setTitle("Histogram");
        stage.setScene(new Scene(container, 1000, 800));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
