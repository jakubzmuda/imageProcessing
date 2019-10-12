package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class App extends Application {

    private static int appWidth = 800;
    private static int appHeight = 600;
    private static long imageContainerWidth = Math.round(appWidth);

    private Image image;
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
        MenuItem stretchImageItem = new MenuItem("Rozciągnij obraz");
        stretchImageItem.setOnAction(e -> {
            new Stretch().stretch();
        });

        menu.getItems().addAll(stretchImageItem);
        return menu;
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

            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                this.image = image;
                imageView.setImage(image);
            } catch (IOException ignored) {
            }
        });
        return openImageItem;
    }

    private void printHistogram() {
        HistogramPainter histogramPainter = new HistogramPainter(image);
        this.barChart = histogramPainter.paintChart();
        StackPane container = new StackPane(barChart);
        if (histogramPainter.isSuccess()) {
            barChart.getData().addAll(
                    histogramPainter.getSeriesRed(),
                    histogramPainter.getSeriesGreen(),
                    histogramPainter.getSeriesBlue());
        }

        Stage stage = new Stage();
        stage.setTitle("Histogram");
        stage.setScene(new Scene(container, 1000, 800));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
