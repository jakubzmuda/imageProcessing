package app;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

import java.util.Arrays;
import java.util.List;

import static app.LinearFilters.SMOOTH_1;
import static app.LinearFilters.SMOOTH_2;

/**
 * Klasa reprezentująca okno wygładzania obrazu.
 */
public class SmoothLinearWindow {

    /**
     * Wartości wskazujące rodzaj opracji na pikselach brzegowych.
     */
    private static final int BORDER_MINIMUM = 254;
    private static final int BORDER_MAXIMUM = 255;

    /**
     * Minimalna wartość k w masce parametryzowanej.
     */
    private static final double MIN_LEVEL = 1;

    /**
     * Maksymalna wartość k w masce parametryzowanej.
     */
    private static final double MAX_LEVEL = 16;

    /**
     * Wysokość panelu opcji.
     */
    private static final int OPTIONS_HEIGHT = 130;

    /**
     * Minimalna szerokość okna.
     */
    private static final int MINIMAL_WIDTH = 700;
    private final App app;

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
    private TextField slider;

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
     * Maski parametryzowane.
     */
    private Mask3x3 parametrized1 = new Mask3x3("PARAMETRIZED_1", true, 1, 1, 1, 1, 1, 1, 1, 1, 1);
    private Mask3x3 parametrized2 = new Mask3x3("PARAMETRIZED_2", true, 0, 1, 0, 1, 1, 1, 0, 1, 0);

    /**
     * List dostępnych masek
     */
    private List<Mask3x3> masks;

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

    public SmoothLinearWindow(Image image, App app) {
        this.app = app;
        before = image;
        masks = Arrays.asList(SMOOTH_1, SMOOTH_2, parametrized1, parametrized2);

        ToggleGroup options = new ToggleGroup();
        RadioButton mask1 = createMaskRadioButton(options, SMOOTH_1);
        RadioButton mask2 = createMaskRadioButton(options, SMOOTH_2);
        RadioButton maskK1 = createMaskRadioButton(options, parametrized1);
        RadioButton maskK2 = createMaskRadioButton(options, parametrized2);
        mask1.setSelected(true);
        handleOptionChanges(options);
        HBox radioHBox = new HBox(mask1, mask2, maskK1, maskK2);
        radioHBox.setAlignment(Pos.CENTER);
        radioHBox.setSpacing(15);

        currentMask = SMOOTH_1;
        currentBorderType = Core.BORDER_CONSTANT;
        times = 1;

        HBox kSliderHbox = createSliderHBox();
        createBeforeImageView();
        createAfterImageView();

        HBox beforeImageViewHbox = new HBox(beforeImageView);
        beforeImageViewHbox.setAlignment(Pos.CENTER);
        HBox afterImageViewHbox = new HBox(afterImageView);
        afterImageViewHbox.setAlignment(Pos.CENTER);
        hBox = new HBox(beforeImageViewHbox, afterImageViewHbox);
        hBox.setAlignment(Pos.CENTER);

        Button save = new Button("Kontynuuj");
        save.setOnAction(event -> {
            app.updateImage(after);
            stage.close();
        });

        VBox parametrizedSlider = new VBox(kSliderHbox);
        parametrizedSlider.setSpacing(5);
        parametrizedSlider.setAlignment(Pos.CENTER);

        VBox radioAndKSliderVBox = new VBox(radioHBox, parametrizedSlider);
        radioAndKSliderVBox.setSpacing(15);
        radioAndKSliderVBox.setAlignment(Pos.CENTER);
        HBox buttonsHbox = new HBox(save);
        buttonsHbox.setAlignment(Pos.CENTER);
        VBox buttonsTimesVbox = new VBox(buttonsHbox);

        VBox borderVBox = createBorderOptions();

        VBox buttons = new VBox(radioAndKSliderVBox,
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
        stage.setTitle("Wygładzanie");
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
    private Scene createScene(HBox beforeImageViewHbox, HBox afterImageViewHbox) {
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
     * Obsługuje zmianę dowolnej opcji przez użytkownika (ustawia maskę i odświeża podgląd)
     *
     * @param options
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
        String maskName = newValue.getUserData().toString();

        masks.stream()
                .filter(mask -> maskName.equals(mask.getName()))
                .findFirst()
                .ifPresent(this::setAsCurrentMask);

        reloadPreview();
    }

    /**
     * Ustawia maskę jako aktualnie wybraną.
     *
     * @param mask3x3 maska do ustawienia
     */
    private void setAsCurrentMask(Mask3x3 mask3x3) {
        currentMask = mask3x3;
    }

    /**
     * Tworzy podgląd obrazu po operacji.
     */
    private void createAfterImageView() {
        after = applyMask(SMOOTH_1);
        afterImageView = new ImageView(after);
        afterImageView.setPreserveRatio(true);
        afterImageView.setFitWidth(400);
        afterImageView.setFitHeight(400);
    }

    /**
     * Tworzy obszar ze Sliderem do wyboru parametru k w masce parametryzowanej.
     *
     * @return obszar ze Sliderem
     */
    private HBox createSliderHBox() {
        Label value = new Label("k = ");
        value.setPrefWidth(35);
        createSlider(value);

        HBox sliderHbox = new HBox(value, slider);
        sliderHbox.setAlignment(Pos.CENTER);
        sliderHbox.setSpacing(5);
        return sliderHbox;
    }

    /**
     * Tworzy RadioButton dla wybranej maski i przypisuje go do podanego ToggleGroup
     *
     * @param options do przypisania utworzonego RadioButton.
     * @param mask    maska dla której utworzony zostanie RadioButton
     * @return <tt>RadioButton</tt> dla maski
     */
    private RadioButton createMaskRadioButton(ToggleGroup options, Mask3x3 mask) {
        RadioButton maskButton = new RadioButton(mask.toString());
        maskButton.setUserData(mask.getName());
        maskButton.setToggleGroup(options);
        maskButton.setPrefHeight(50);
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
        ToggleGroup borderTypeGroup = new ToggleGroup();
        Label borderTypeLabel = new Label("Piksele brzegowe:");

        RadioButton replicatedBorder = new RadioButton("Bez zmian");
        replicatedBorder.setUserData(Core.BORDER_CONSTANT);
        replicatedBorder.setToggleGroup(borderTypeGroup);
        replicatedBorder.setSelected(true);

        RadioButton reflectedBorder = new RadioButton("Powielenie");
        reflectedBorder.setUserData(Core.BORDER_REPLICATE);
        reflectedBorder.setToggleGroup(borderTypeGroup);

        RadioButton existingBorder = new RadioButton("Istniejące");
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
     * Tworzy Slider do wyboru parametru k w masce parametryzowanej.
     *
     * @param value Label z wyświetlaną wartością
     */
    private void createSlider(Label value) {
        TextField kField = new TextField("1");

        this.slider = kField;

        kField.textProperty().addListener((observable, oldValue, newValue) -> {
            parametrized1.updateMiddleElement(Integer.parseInt(newValue));
            parametrized2.updateMiddleElement(Integer.parseInt(newValue));
            reloadPreview();
        });

        kField.setPrefWidth(100);
//        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
//            slider.setValue(newValue.intValue());
//            value.setText("k = " + newValue.intValue());
//            parametrized1.updateMiddleElement(newValue.intValue());
//            parametrized2.updateMiddleElement(newValue.intValue());
//            reloadPreview();
//        });
    }

    /**
     * Stosuje wybraną maskę i odświeża podgląd.
     */
    private void reloadPreview() {
        after = applyMask(currentMask);
        afterImageView.setImage(after);
    }

    /**
     * Przeprowadza operację filtrowania wybraną maską.
     *
     * @param mask maska do operacji
     * @return obraz wynikowy.
     */
    private Image applyMask(Mask3x3 mask) {
        Mat image = ImageUtils.imageToMat(before);
        applyMask(mask, image);

        return ImageUtils.mat2Image(image);
    }

    /**
     * Przeprowadza operację filtrowania wybraną maską na podanym obiekcie Mat.
     *
     * @param mask  maska do operacji
     * @param image obiekt Mat z obrazem
     */
    private void applyMask(Mask3x3 mask, Mat image) {
        for (int i = 0; i < times; i++) {
            FilteringUtils.applyMask(image, mask, currentBorderType, border);
        }
    }

}
