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
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.Core.BORDER_CONSTANT;

/**
 * Reprezentuje okno filtrowania medianowego.
 */
public class MedianFilterWindow {

    /**
     * Wysokość panelu opcji.
     */
    private static final int OPTIONS_HEIGHT = 80;

    /**
     * Minimalna szerokość okna.
     */
    private static final int MINIMAL_WIDTH = 550;

    /**
     * Wielkości masek do rozmycia medianowego.
     */
    private static final int KERNEL_3X3 = 3;
    private static final int KERNEL_5X5 = 5;
    private static final int KERNEL_7X7 = 7;
    private static final int KERNEL_9X9 = 9;
    private static final int KERNEL_11X11 = 11;
    private final App app;

    private BarChart<String, Number> beforeImageHistogram;
    private BarChart<String, Number> afterImageHistogram;
    private VBox afterImageViewHbox;

    /**
     * Podgląd obrazu przed i po operacji.
     */
    private ImageView beforeImageView;
    private ImageView afterImageView;

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

    /**
     * Wartość zwielokrotnienia operacji.
     */
    private double times;

    /**
     * Aktualnie wybrana wielkość maski.
     */
    private int currentKernelSize;

    /**
     * Aktualnie wybrana przez użytkownika operacja na pikselach brzegowych.
     */
    private int currentBorderType;

    public MedianFilterWindow(Image image, App app) {
        this.app = app;
        before = image;

        HBox radioHBox = createButtonSelectionHBox();
        radioHBox.setAlignment(Pos.CENTER);

        currentKernelSize = KERNEL_3X3;
        currentBorderType = Core.BORDER_CONSTANT;
        times = 1;

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
        VBox buttonsTimesVbox = new VBox(buttonsHbox);

        VBox borderVBox = createBorderOptions();

        VBox buttons = new VBox(radioHBox,
                borderVBox,
                buttonsTimesVbox);
        buttons.setPadding(new Insets(13, 10, 10, 0));
        buttons.setSpacing(15);
        buttons.setAlignment(Pos.CENTER);
        vBox = new VBox(hBox, buttons);

        Scene scene = createScene(beforeImageViewHbox, afterImageViewHbox);

        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setScene(scene);
        stage.setTitle("Filtracja medianowa");
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
        Scene scene = new Scene(vBox, windowWidth, 900);
        scene.setOnKeyPressed(event -> {
            if (KeyCode.ESCAPE.equals(event.getCode())) stage.close();
        });
        beforeImageViewHbox.setPrefWidth(windowWidth / 2);
        afterImageViewHbox.setPrefWidth(windowWidth / 2);
        return scene;
    }

    /**
     * Tworzy obszar do wybrania wielkości maski medianowej.
     *
     * @return obszar do wyboru wielkości maski
     */
    private HBox createButtonSelectionHBox() {
        ToggleGroup options = new ToggleGroup();
        RadioButton mask1 = createMaskRadioButton(options, "3x3", KERNEL_3X3);
        RadioButton mask2 = createMaskRadioButton(options, "5x5", KERNEL_5X5);
        RadioButton mask3 = createMaskRadioButton(options, "7x7", KERNEL_7X7);

        mask1.setSelected(true);

        handleOptionChanges(options);

        HBox radioHBox = new HBox(mask1, mask2, mask3);
        radioHBox.setSpacing(15);
        return radioHBox;
    }

    /**
     * Tworzy przycisk do wyboru wielkości maski.
     *
     * @param options    grupa do której należy przycisk
     * @param text       tekst
     * @param kernelSize wielkość maski
     * @return przycisk wyboru wielkości maski
     */
    private RadioButton createMaskRadioButton(ToggleGroup options, String text, int kernelSize) {
        RadioButton maskButton = new RadioButton(text);
        maskButton.setUserData(kernelSize);
        maskButton.setToggleGroup(options);
        maskButton.setPrefHeight(25);
        return maskButton;
    }

    /**
     * Sprawdza, jaka opcja została ustawiona, zmiania maskę, przeprowadza
     * operację wygładzania medianowego i odświeża podgląd.
     *
     * @param options opcje wyboru maski
     */
    private void handleOptionChanges(ToggleGroup options) {
        options.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                changeCurrentMask(newValue);
            }
        });
    }

    /**
     * Zmienia maskę na wybraną przez użytkownika i odświeża podgląd.
     *
     * @param newValue nazwa wybranej przez użytkownika maski
     */
    private void changeCurrentMask(Toggle newValue) {
        currentKernelSize = (int) newValue.getUserData();
        reloadPreview();
    }

    /**
     * Tworzy podgląd obrazu po operacji.
     */
    private void createAfterImageView() {
        after = applyMask();
        afterImageView = new ImageView(after);
        afterImageView.setPreserveRatio(true);
        afterImageView.setFitWidth(400);
        afterImageView.setFitHeight(400);
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
        ToggleGroup borderTypeGroup = new ToggleGroup();
        Label borderTypeLabel = new Label("Piksele brzegowe:");

        RadioButton replicatedBorder = new RadioButton("Bez zmian");
        replicatedBorder.setUserData(Core.BORDER_CONSTANT);
        replicatedBorder.setToggleGroup(borderTypeGroup);
        replicatedBorder.setSelected(true);

        RadioButton reflectedBorder = new RadioButton("Powielenie");
        reflectedBorder.setUserData(Core.BORDER_REPLICATE);
        reflectedBorder.setToggleGroup(borderTypeGroup);

        borderTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            currentBorderType = (int) newValue.getUserData();
            reloadPreview();
        });

        VBox vBox = new VBox(borderTypeLabel, replicatedBorder, reflectedBorder);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    /**
     * Przeprowadza operację i odświeża podgląd
     */
    private void reloadPreview() {
        after = applyMask();
        afterImageView.setImage(after);

        this.afterImageHistogram = buildHistogram(after);
        afterImageViewHbox.getChildren().set(1, this.afterImageHistogram);
    }

    /**
     * Przeprowadza operację medianową utworzoną maską.
     *
     * @return obraz wynikowy.
     */
    private Image applyMask() {
        Mat image = ImageUtils.imageToMat(before);
        Mat destination = new Mat(image.rows(), image.cols(), image.type());
        image.copyTo(destination);

        if (currentBorderType == BORDER_CONSTANT) {
            applyMaskWithConstantBorder(image, destination);
        } else {
            applyMask(image, destination);
        }

        return ImageUtils.mat2Image(image);
    }

    /**
     * Przeprowadza operację medianową utworzoną maską.
     *
     * @param image       obraz wejściowy
     * @param destination obraz wyjściowy
     */
    private void applyMask(Mat image, Mat destination) {
        for (int i = 0; i < times; i++) {
            Imgproc.medianBlur(destination, destination, currentKernelSize);
        }
        destination.copyTo(image);
    }

    /**
     * Przeprowadza operację medianową utworzoną maską (ze stałą wartością
     * pikseli brzegowych).
     *
     * @param image       obraz wejściowy
     * @param destination obraz wyjściowy
     */
    private void applyMaskWithConstantBorder(Mat image, Mat destination) {
        for (int i = 0; i < times; i++) {
            Imgproc.medianBlur(destination, destination, currentKernelSize);
        }

        restoreBorder(image, destination);
    }

    /**
     * Ustawia wartość pikseli brzegowych z oryginalnego obrazu.
     *
     * @param image       obraz wejściowy
     * @param destination obraz wyjściowy
     */
    private void restoreBorder(Mat image, Mat destination) {
        Mat cropped = destination.submat(1, destination.height() - 1, 1, destination.width() - 1);
        cropped.convertTo(cropped, image.type());
        cropped.copyTo(image.submat(1, image.height() - 1, 1, image.width() - 1));
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
