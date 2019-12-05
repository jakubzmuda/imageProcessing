package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
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
import java.util.Arrays;


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
        Menu lab3Menu = buildLab3MenuTab();
        Menu lab4Menu = buildLab4MenuTab();
        Menu lab5Menu = buildLab5MenuTab();
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, lab1Menu, lab2Menu, lab3Menu , lab4Menu, lab5Menu);
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

        MenuItem levelReductionItem = new MenuItem("Redukcja poziomów szarości");
        levelReductionItem.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setTitle("Redukcja poziomów szarości");

            GridPane container = new GridPane();
            Scene scene = new Scene(container, 400, 200);

            Label pLabel = new Label("p (oddielone przecinkami) ");
            TextField pField = new TextField("32, 64, 96, 128");
            container.add(pLabel, 0, 0);
            container.add(pField, 1, 0);

            Label qLabel = new Label("q (oddielone przecinkami) ");
            TextField qField = new TextField("50, 100, 150, 200");
            container.add(qLabel, 0, 1);
            container.add(qField, 1, 1);

            Button doIt = new Button("Kontynuuj");
            doIt.setOnAction((event) -> {
                Image newImage = new ImageOperations().levelReduction(image,
                        Arrays.stream(pField.getText().split(", ")).mapToInt(Integer::parseInt).toArray(),
                        Arrays.stream(qField.getText().split(", ")).mapToInt(Integer::parseInt).toArray());
                updateImage(newImage);
                stage.close();
            });

            container.setAlignment(Pos.CENTER);
            container.add(doIt, 0, 3);
            stage.setScene(scene);
            stage.show();

        });

        MenuItem spreadingP1P2Item = new MenuItem("Rozciąganie z zakresu p1-p2");
        spreadingP1P2Item.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setTitle("Rozciąganie z zakresu p1-p2");

            GridPane container = new GridPane();
            Scene scene = new Scene(container, 400, 200);

            Label p1Label = new Label("p1");
            TextField p1Field = new TextField("50");
            container.add(p1Label, 0, 0);
            container.add(p1Field, 1, 0);

            Label p2Label = new Label("p2");
            TextField p2Field = new TextField("100");
            container.add(p2Label, 0, 1);
            container.add(p2Field, 1, 1);

            Label q1Label = new Label("q1");
            TextField q1Field = new TextField("0");
            container.add(q1Label, 0, 2);
            container.add(q1Field, 1, 2);

            Label q2Label = new Label("q2");
            TextField q2Field = new TextField("255");
            container.add(q2Label, 0, 3);
            container.add(q2Field, 1, 3);

            Button doIt = new Button("Kontynuuj");
            doIt.setOnAction((event) -> {
                Image newImage = new ImageOperations().spreadRange(image,
                        Integer.parseInt(p1Field.getText()),
                        Integer.parseInt(p2Field.getText()),
                        Integer.parseInt(q1Field.getText()),
                        Integer.parseInt(q2Field.getText()));
                updateImage(newImage);
                stage.close();
            });

            container.setAlignment(Pos.CENTER);
            container.add(doIt, 0, 4);
            stage.setScene(scene);
            stage.show();

        });


        menu.getItems().addAll(stretchImageItem, equalizeImageItem, negateImageItem, thresholdingItem, thresholdingWithPreservationItem, levelReductionItem, spreadingP1P2Item);
        return menu;
    }

    private Menu buildLab3MenuTab() {
        MenuItem smoothingItem = buildSmoothingMenuItem();
        Menu menu = new Menu("Lab 3");
        menu.getItems().addAll(smoothingItem);
        return menu;
    }

    private Menu buildLab4MenuTab() {
        Menu menu = new Menu("Lab 4");
        return menu;
    }

    private Menu buildLab5MenuTab() {
        Menu menu = new Menu("Lab 5");
        return menu;
    }

    public void updateImage(Image newImage) {
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
//            FileChooser fileChooser = new FileChooser(); //nocommit
//            File file = fileChooser.showOpenDialog(null);
            ClassLoader classLoader = getClass().getClassLoader(); // fast load
            File file = new File(classLoader.getResource("niedzkol.bmp").getFile());

            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                updateImage(image);
            } catch (IOException ignored) {
            }
        });
        return openImageItem;
    }

    private MenuItem buildSmoothingMenuItem() {
        MenuItem smoothingItem = new MenuItem("Wygładzanie");
        smoothingItem.setOnAction(e -> new Lab3(image, this).smoothing());
        return smoothingItem;
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
