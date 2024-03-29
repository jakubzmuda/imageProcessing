package app;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

/**
 * Reprezentuje okno operacji morfologicznych.
 */
public class MorphologyWindow {

    /**
     * Wartości wskazujące rodzaj opracji na pikselach brzegowych.
     */
    private static final int BORDER_MINIMUM = 254;
    private static final int BORDER_MAXIMUM = 255;

    /**
     * Minimalna wielkość kształu do operacji
     */
    private static final double MIN_LEVEL = 3;

    /**
     * Maksymalna wielkość kształu do operacji
     */
    private static final double MAX_LEVEL = 7;

    /**
     * Wysokość panelu opcji.
     */
    private static final int OPTIONS_HEIGHT = 130;

    /**
     * Minimalna szerokość okna.
     */
    private static final int MINIMAL_WIDTH = 700;

    /**
     * Podgląd obrazu przed i po operacji.
     */
    private ImageView beforeImageView;
    private ImageView afterImageView;
    private VBox afterImageViewHbox;

    /**
     * Elementy okna.
     */
    private Stage stage;
    private VBox vBox;
    private HBox hBox;

    /**
     * Obrazy przed i po operacji.
     */
    private Image before;
    private Image after;
    private BarChart<String, Number> beforeImageHistogram;
    private BarChart<String, Number> afterImageHistogram;

    /**
     * Wartość zwielokrotnienia operacji.
     */
    private int times;

    /**
     * Aktualnie wybrana operacja
     */
    private int currentOperation;

    /**
     * Aktualnie wybrany kształt.
     */
    private int currentShape;

    /**
     * Aktualny rozmiar kształtu do operacji.
     */
    private int currentSize;

    /**
     * Aktualnie wybrana przez użytkownika operacja na pikselach brzegowych.
     */
    private int currentBorderType;

    /**
     * Wartość pikseli brzegowych (jeśli wybrana stała wartość)
     */
    private Scalar border;

    public MorphologyWindow(Image image, App app) {
        before = image;

        VBox operationVBox = createOperationVBox();
        VBox shapeVBox = createShapeVBox();

        times = 1;
        currentOperation = Imgproc.MORPH_ERODE;
        currentShape = Imgproc.MORPH_RECT;
        currentSize = 3;
        currentBorderType = Core.BORDER_REPLICATE;
        border = new Scalar(0, 0, 0, 255);

        createBeforeImageView();
        createAfterImageView();

        this.beforeImageHistogram = buildHistogram(beforeImageView.getImage());
        this.afterImageHistogram = buildHistogram(afterImageView.getImage());

        VBox beforeImageViewHbox = new VBox(beforeImageView, beforeImageHistogram);
        beforeImageViewHbox.setAlignment(Pos.CENTER);
        VBox afterImageViewHbox = new VBox(afterImageView, afterImageHistogram);
        this.afterImageViewHbox = afterImageViewHbox;
        afterImageViewHbox.setAlignment(Pos.CENTER);
        hBox = new HBox(beforeImageViewHbox, afterImageViewHbox);
        hBox.setAlignment(Pos.CENTER);

        Button save = new Button("Kontynuuj");
        save.setOnAction(event -> {
            app.updateImage(after);
            stage.close();
        });

        HBox buttonsHbox = new HBox(save);
        buttonsHbox.setAlignment(Pos.CENTER);
        buttonsHbox.setSpacing(15);
        VBox buttonsTimesVbox = new VBox(buttonsHbox);
        buttonsTimesVbox.setAlignment(Pos.CENTER);
        buttonsTimesVbox.setSpacing(15);

        VBox borderVBox = createBorderOptions();

        VBox buttons = new VBox(operationVBox, shapeVBox, borderVBox, buttonsTimesVbox);
        buttons.setPadding(new Insets(13, 10, 10, 0));
        buttons.setSpacing(15);
        buttons.setAlignment(Pos.CENTER);
        vBox = new VBox(hBox, buttons);

        Scene scene = createScene(beforeImageViewHbox, afterImageViewHbox);

        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setScene(scene);
        stage.setTitle("Operacje morfologiczne");
        save.requestFocus();
        stage.showAndWait();
    }

    /**
     * Oblicza wielkość okna na podstawie wielkości obrazów i tworzy układ okna.
     *
     * @param beforeImageViewHbox obszar z podglądem obrazu przed zmianami
     * @param afterImageViewHbox  obszar z podglądem obrazu po zmianach
     * @return <tt>Scene</tt> z układem okna
     */
    private Scene createScene(VBox beforeImageViewHbox, VBox afterImageViewHbox) {
        double windowWidth = Math.max(MINIMAL_WIDTH, afterImageView.getBoundsInLocal().getWidth() * 2);
        Scene scene = new Scene(vBox, windowWidth, 800);
        scene.setOnKeyPressed(event -> {
            if (KeyCode.ESCAPE.equals(event.getCode())) stage.close();
        });
        beforeImageViewHbox.setPrefWidth(windowWidth / 2);
        afterImageViewHbox.setPrefWidth(windowWidth / 2);
        return scene;
    }

    /**
     * Tworzy opcje kształtu do operacji.
     *
     * @return obszar z wyborem kształtu.
     */
    private VBox createShapeVBox() {
        ToggleGroup shape = new ToggleGroup();
        RadioButton rectangle = createButton(shape, Imgproc.MORPH_RECT, "Kwadrat");
        RadioButton cross = createButton(shape, Imgproc.MORPH_CROSS, "Romb");

        rectangle.setSelected(true);
        handleShapeChanges(shape);
        VBox vBox = new VBox(rectangle, cross);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    /**
     * Tworzy opcje operacji.
     *
     * @return obszar z wyborem operacji.
     */
    private VBox createOperationVBox() {
        ToggleGroup operations = new ToggleGroup();
        RadioButton erode = createButton(operations, Imgproc.MORPH_ERODE, "Erozja");
        RadioButton dilate = createButton(operations, Imgproc.MORPH_DILATE, "Dylacja");
        RadioButton open = createButton(operations, Imgproc.MORPH_OPEN, "Otwarcie");
        RadioButton close = createButton(operations, Imgproc.MORPH_CLOSE, "Zamknięcie");

        erode.setSelected(true);
        handleOperationChanges(operations);
        VBox vBox = new VBox(erode, dilate, open, close);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    /**
     * Obsługuje zmianę wybranej operacji - przeprowadza operację i odświeża podgląd.
     *
     * @param options grupa z opcjami operacji
     */
    private void handleOperationChanges(ToggleGroup options) {
        options.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                changeCurrentOperation(newValue);
            }
        });
    }

    /**
     * Obsługuje zmianę wybranego kształtu - przeprowadza operację i odświeża podgląd.
     *
     * @param options grupa z opcjami kształtu
     */
    private void handleShapeChanges(ToggleGroup options) {
        options.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                changeCurrentShape(newValue);
            }
        });
    }

    /**
     * Zmienia wybraną operację, przeprowadza ją i odświeża podgląd.
     *
     * @param newValue wybrana operacja
     */
    private void changeCurrentOperation(Toggle newValue) {
        currentOperation = (int) newValue.getUserData();
        reloadPreview();
    }

    /**
     * Zmienia wybrany kształt, przeprowadza operację i odświeża podgląd.
     *
     * @param newValue wybrany kształt
     */
    private void changeCurrentShape(Toggle newValue) {
        currentShape = (int) newValue.getUserData();
        reloadPreview();
    }

    /**
     * Tworzy podgląd obrazu po operacji.
     */
    private void createAfterImageView() {
        after = applyOperation();
        afterImageView = new ImageView(after);
        afterImageView.setPreserveRatio(true);
        afterImageView.setFitWidth(400);
        afterImageView.setFitHeight(400);
    }

    /**
     * Tworzy przycisk i przypisuje go do podanej grupy.
     *
     * @param toggleGroup grupa opcji
     * @param value       wartość pod przyciskiem
     * @param name        tekst
     * @return przycisk o podanych parametrach
     */
    private RadioButton createButton(ToggleGroup toggleGroup, int value, String name) {
        RadioButton maskButton = new RadioButton(name);
        maskButton.setUserData(value);
        maskButton.setToggleGroup(toggleGroup);
        return maskButton;
    }

    /**
     * Tworzy podgląd obrazu przed operacją.
     */
    private void createBeforeImageView() {
        beforeImageView = new ImageView((before));
        beforeImageView.setPreserveRatio(true);
        beforeImageView.setFitWidth(400);
        beforeImageView.setFitHeight(400);
    }

    /**
     * Tworzy obszar z opcjami dotyczącymi pikseli brzegowych.
     *
     * @return obszar z opcjami dotyczącymi pikseli brzegowych.
     */
    private VBox createBorderOptions() {
        Label borderTypeLabel = new Label("Piksele brzegowe:\n Istniejące sąsiedztwo");

        handleBorderOptionChange(Core.BORDER_DEFAULT);

        VBox vBox = new VBox(borderTypeLabel);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    /**
     * Obsługuję zmianę opcji dotyczących pikseli brzegowych.
     *
     * @param newValue wybrana opcja
     */
    private void handleBorderOptionChange(int newValue) {
        currentBorderType = newValue;

        reloadPreview();
    }

    /**
     * Przeprowadza wybraną operację morfologiczną i odświeża podgląd
     */
    private void reloadPreview() {
        after = applyOperation();
        afterImageView.setImage(after);
        this.afterImageHistogram = buildHistogram(after);
        afterImageViewHbox.getChildren().set(1, this.afterImageHistogram);
    }

    /**
     * Przeprowadza wybraną przez użytkownika operację.
     *
     * @return obraz po operacji
     */
    private Image applyOperation() {
        Mat image = ImageUtils.imageToMat(before);

        Mat shape = Imgproc.getStructuringElement(currentShape, new Size(currentSize, currentSize));
        switch (currentOperation) {
            case Imgproc.MORPH_ERODE:
                Imgproc.erode(image, image, shape, new Point(-1, -1), times, currentBorderType, border);
                break;
            case Imgproc.MORPH_DILATE:
                Imgproc.dilate(image, image, shape, new Point(-1, -1), times, currentBorderType, border);
                break;
            case Imgproc.MORPH_OPEN:
            case Imgproc.MORPH_CLOSE:
                Imgproc.morphologyEx(image, image, currentOperation, shape, new Point(-1, -1), times, currentBorderType, border);
        }

        if (currentBorderType == Core.BORDER_CONSTANT) {
            FilteringUtils.handleBorder(image, border);
        }

        return ImageUtils.mat2Image(image);
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
