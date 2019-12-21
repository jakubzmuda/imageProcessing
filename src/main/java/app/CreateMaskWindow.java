package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa reprezentująca okno wyostrzania obrazu.
 */
public class CreateMaskWindow {

    /**
     * Wartości wskazujące rodzaj opracji na pikselach brzegowych.
     */
    private static final int BORDER_MINIMUM = 254;
    private static final int BORDER_MAXIMUM = 255;

    /**
     * Wysokość panelu opcji.
     */
    private static final int OPTIONS_HEIGHT = 125;

    /**
     * Minimalna szerokość okna.
     */
    private static final int MINIMAL_WIDTH = 600;
    private final App app;

    /**
     * Podgląd obrazu przed i po operacji.
     */
    private ImageView beforeImageView;
    private ImageView afterImageView;

    private BarChart<String, Number> beforeImageHistogram;
    private BarChart<String, Number> afterImageHistogram;
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

    /**
     * Wartość zwielokrotnienia operacji.
     */
    private double times;

    /**
     * Aktualnie wybrana przez użytkownika maska.
     */
    private Mask3x3 currentMask;

    /**
     * Aktualnie wybrana przez użytkownika operacja na pikselach brzegowych.
     */
    private int currentBorderType;

    /**
     * Wartość pikseli brzegowych (jeśli wybrana stała wartość)
     */
    private Scalar border;

    /**
     * Wartości dostępne w masce.
     */
    private ObservableList<Integer> availableValues;


    public CreateMaskWindow(Image image, App app) {
        this.app = app;
        before = image;
        availableValues = FXCollections.observableArrayList();
        for (int j = -20; j <= 20; j++) availableValues.add(j);

        List<Spinner<Integer>> spinners = createSpinners();
        HBox spinner1Hbox = new HBox(spinners.get(0), spinners.get(1), spinners.get(2));
        HBox spinner2Hbox = new HBox(spinners.get(3), spinners.get(4), spinners.get(5));
        HBox spinner3Hbox = new HBox(spinners.get(6), spinners.get(7), spinners.get(8));
        VBox vBox = new VBox(spinner1Hbox, spinner2Hbox, spinner3Hbox);
        vBox.setPrefWidth(180);
        HBox createMaskHBox = new HBox(vBox);
        createMaskHBox.setAlignment(Pos.CENTER);
        createMaskHBox.setPrefWidth(200);

        currentMask = new Mask3x3("DEFAULT", false, 1, 1, 1, 1, 1, 1, 1, 1, 1);
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
        buttonsHbox.setSpacing(15);
        buttonsHbox.setAlignment(Pos.CENTER);
        VBox buttonsTimesVbox = new VBox(buttonsHbox);
        buttonsTimesVbox.setAlignment(Pos.CENTER);
        buttonsTimesVbox.setSpacing(15);
        VBox borderVBox = createBorderOptions();
        VBox buttons = new VBox(createMaskHBox,
                borderVBox,
                buttonsTimesVbox);
        buttons.setPadding(new Insets(13, 10, 10, 0));
        buttons.setSpacing(15);
        buttons.setAlignment(Pos.CENTER);
        vBox = new VBox(hBox, buttons);

        Scene scene = createScene(vBox, beforeImageViewHbox, afterImageViewHbox);

        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setScene(scene);
        stage.setTitle("Własna maska");
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
    private Scene createScene(VBox vBox, VBox beforeImageViewHbox, VBox afterImageViewHbox) {
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
     * Tworzy pola do wybierania wartości w masce
     *
     * @return lista pól do wybierania wartości w masce
     */
    private List<Spinner<Integer>> createSpinners() {
        List<Spinner<Integer>> spinners = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            SpinnerValueFactory<Integer> values = new SpinnerValueFactory.ListSpinnerValueFactory<>(availableValues);
            values.setValue(1);
            Spinner<Integer> spinner = new Spinner<>(values);
            spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                handleValueChange(spinners);
            });
            spinners.add(spinner);
        }
        return spinners;
    }

    /**
     * Obsługuje zmianę wartości w masce - przelicza maskę, przeprowadza operację
     * i odświeża podgląd.
     *
     * @param spinners lista pól z wartościami maski
     */
    private void handleValueChange(List<Spinner<Integer>> spinners) {
        double[] spinnerValues = new double[9];
        for (int j = 0; j < 9; j++) {
            spinnerValues[j] = spinners.get(j).getValue();
        }

        changeCurrentMask(spinnerValues);
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

        RadioButton reflectedBorder = new RadioButton("Powielenie pikseli brzegowych");
        reflectedBorder.setUserData(Core.BORDER_REPLICATE);
        reflectedBorder.setToggleGroup(borderTypeGroup);

        RadioButton existingBorder = new RadioButton("Istniejące sąsiedztwo");
        existingBorder.setUserData(Core.BORDER_DEFAULT);
        existingBorder.setToggleGroup(borderTypeGroup);

        borderTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue)
                -> handleBorderOptionChange(newValue));

        VBox vBox = new VBox(borderTypeLabel, replicatedBorder, reflectedBorder, existingBorder);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    /**
     * Obsługuję zmianę opcji dotyczących pikseli brzegowych.
     *
     * @param newValue wybrana opcja
     */
    private void handleBorderOptionChange(Toggle newValue) {
        int selected = (int) newValue.getUserData();
        if (selected == BORDER_MINIMUM) {
            currentBorderType = Core.BORDER_CONSTANT;
            border = new Scalar(0, 0, 0, 255);
        } else if (selected == BORDER_MAXIMUM) {
            currentBorderType = Core.BORDER_CONSTANT;
            border = new Scalar(255, 255, 255, 255);
        } else {
            currentBorderType = selected;
            border = null;
        }

        reloadPreview();
    }

    /**
     * Tworzy maskę z wybranych przez użytkownika wartości, przeprowadza operację
     * na obrazie z jej użyciem i odświeża podgląd.
     *
     * @param values wartości maski do utworzenia
     */
    private void changeCurrentMask(double[] values) {
        currentMask = new Mask3x3("CUSTOM", false, values);
        reloadPreview();
    }

    /**
     * Tworzy podgląd obrazu po operacji.
     */
    private void createAfterImageView() {
        after = applyMask(currentMask);
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
     * Stosuje wybraną maskę i odświeża podgląd.
     */
    private void reloadPreview() {
        after = applyMask(currentMask);
        afterImageView.setImage(after);

        this.afterImageHistogram = buildHistogram(after);
        afterImageViewHbox.getChildren().set(1, this.afterImageHistogram);
    }

    /**
     * Przeprowadza operację filtrowania utworzoną maską. Jeśli suma wartości w masce
     * jest równa 0, maska zostaje przekonwertowana na skalę szarości.
     *
     * @param mask maska do operacji
     * @return obraz wynikowy.
     */
    private Image applyMask(Mask3x3 mask) {
        if (mask == null) {
            return before;
        }
        Mat image = ImageUtils.imageToMat(before);

        if (mask.getKernelSize() == 1) {
            applyMask(mask, image);
        } else {
            applyMaskWithColorConversion(mask, image);
        }

        return ImageUtils.mat2Image(image);
    }

    private void applyMask(Mask3x3 mask, Mat image) {
        for (int i = 0; i < times; i++) {
            FilteringUtils.applyMask(image, mask, currentBorderType, border);
        }
    }

    private void applyMaskWithColorConversion(Mask3x3 mask, Mat image) {
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY);

        for (int i = 0; i < times; i++) {
            FilteringUtils.applyMask(image, mask, currentBorderType, border);
        }

        Core.convertScaleAbs(image, image);
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
