package app;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa reprezentująca okno wykrywania krawędzi w obrazie.
 */
public class ThinningWindow {

    private static final double WHITE = 255;
    private static final double BLACK = 0;
    private static final double ANY = 150;

    /**
     * Wartości wskazujące rodzaj opracji na pikselach brzegowych.
     */
    private static final int BORDER_MINIMUM = 254;
    private static final int BORDER_MAXIMUM = 255;

    /**
     * Wysokość panelu opcji.
     */
    private static final int OPTIONS_HEIGHT = 140;

    /**
     * Minimalna szerokość okna.
     */
    private static final int MINIMAL_WIDTH = 550;
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
    private Slider stepSlider;
    private Label stepValue;

    /**
     * Obrazy przed i po operacji.
     */
    private Image before;
    private Image after;

    /**
     * Wskazuje wybrany przez użytkownika krok ścieniania.
     */
    private int step;

    /**
     * Aktualnie wybrana przez użytkownika operacja na pikselach brzegowych.
     */
    private int currentBorderType;

    /**
     * Wartość pikseli brzegowych (jeśli wybrana stała wartość)
     */
    private Scalar border;

    /**
     * Początkowe wartości poziomu jasności dla obiektu i tła.
     */
    private double object = BLACK;
    private double background = WHITE;

    /**
     * Lista wzorców do porównania.
     */
    private List<double[]> patterns;

    /**
     * Lista obrazów w kolejnych krokach skieletyzacji.
     */
    List<Image> stepImages;

    public ThinningWindow(Image image, App app) {
        this.app = app;
        before = ImageUtils.binarize(image);
        border = new Scalar(255, 255, 255, 255);
        patterns = BlackObjectPatterns.getPATTERNS();

        currentBorderType = Core.BORDER_CONSTANT;

        createStepSlider();
        createBeforeImageView();
        createAfterImageView();

        HBox beforeImageViewHbox = new HBox(beforeImageView);
        beforeImageViewHbox.setAlignment(Pos.CENTER);
        HBox afterImageViewHbox = new HBox(afterImageView);
        afterImageViewHbox.setAlignment(Pos.CENTER);
        hBox = new HBox(beforeImageViewHbox, afterImageViewHbox);
        hBox.setAlignment(Pos.CENTER);

        Button save = new Button("Kontynuuj");
        save.setOnAction(event -> saveAndClose());
        HBox buttonsHbox = new HBox(save);
        buttonsHbox.setSpacing(15);
        buttonsHbox.setAlignment(Pos.CENTER);
        HBox stepSliderHBox = new HBox(stepSlider, stepValue);
        stepSliderHBox.setAlignment(Pos.CENTER);
        VBox buttonsStepVbox = new VBox(stepSliderHBox, buttonsHbox);
        buttonsStepVbox.setAlignment(Pos.CENTER);
        buttonsStepVbox.setSpacing(15);

        VBox borderVBox = createBorderOptions();
        borderVBox.setAlignment(Pos.CENTER);

        VBox buttons = new VBox(
                borderVBox,
                buttonsStepVbox
        );

        buttons.setPadding(new Insets(13, 10, 10, 0));
        buttons.setSpacing(15);
        buttons.setAlignment(Pos.CENTER);
        vBox = new VBox(hBox, buttons);
        vBox.setAlignment(Pos.CENTER);

        Scene scene = createScene(beforeImageViewHbox, afterImageViewHbox);

        object = BLACK;
        background = WHITE;
        patterns = BlackObjectPatterns.getPATTERNS();

        reloadPreview();

        currentBorderType = Core.BORDER_CONSTANT;
        border = new Scalar(255, 255, 255, 255);

        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setScene(scene);
        stage.setTitle("Szkieletyzacja");
        save.requestFocus();
        stage.showAndWait();
    }

    private void saveAndClose() {
        if (stepImages != null && stepImages.size() > step) {
            this.app.updateImage(stepImages.get(step));
        } else {
            this.app.updateImage(after);
        }

        stage.close();
    }

    /**
     * Tworzy slider do poruszania się pomiędzy krokami skieletyzacji.
     */
    private void createStepSlider() {
        stepSlider = new Slider(1, 10, 1);
        stepSlider.setPrefWidth(180);
        stepSlider.setDisable(true);
        stepValue = new Label("1");
        stepValue.setPrefWidth(30);
        stepSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            stepValue.setText(String.valueOf(newValue.intValue()));
            step = newValue.intValue() - 1;

            if (afterImageView != null && stepImages.size() > step) {
                afterImageView.setImage(stepImages.get(step));
            }
        });
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
        Scene scene = new Scene(vBox, windowWidth, 800);
        scene.setOnKeyPressed(event -> {
            if (KeyCode.ESCAPE.equals(event.getCode())) stage.close();
        });
        beforeImageViewHbox.setPrefWidth(windowWidth / 2);
        afterImageViewHbox.setPrefWidth(windowWidth / 2);
        return scene;
    }

    /**
     * Tworzy obszar z opcjami dotyczącymi pikseli brzegowych.
     *
     * @return obszar z opcjami dotyczącymi pikseli brzegowych.
     */
    private VBox createBorderOptions() {
        Label borderTypeLabel = new Label("Piksele brzegowe: wartość maksymalna");

        return new VBox(borderTypeLabel);
    }

    /**
     * Tworzy podgląd obrazu po operacji.
     */
    private void createAfterImageView() {
        after = before;
        afterImageView = new ImageView(after);
        afterImageView.setPreserveRatio(true);
        afterImageView.setFitWidth(400);
        afterImageView.setFitHeight(400);
        // TODO new HistogramWindow(afterImageView);
    }

    /**
     * Tworzy podgląd obrazu przed operacją.
     */
    private void createBeforeImageView() {
        beforeImageView = new ImageView((before));
        beforeImageView.setPreserveRatio(true);
        beforeImageView.setFitWidth(400);
        beforeImageView.setFitHeight(400);

        // TODO new HistogramWindow(beforeImageView);
    }

    /**
     * Przeprowadza ścienianie i odświeża podgląd.
     */
    private void reloadPreview() {
        after = applyThinning();
        afterImageView.setImage(after);
    }

    private Image applyThinning() {
        stepImages = new ArrayList<>();
        Mat image = ImageUtils.imageToMat(before);
        ImageUtils.binarize(image);

        applyThinning(image);
        refreshSlider();

        return ImageUtils.mat2Image(image);
    }


    private void applyThinning(Mat image) {
        Mat copy = new Mat();
        image.copyTo(copy);

        boolean remain = true;
        while (remain) {
            remain = false;
            for (int j = 1; j < 8; j += 2) {
                for (int col = 1; col < image.cols() - 1; col++) {
                    for (int row = 1; row < image.rows() - 1; row++) {
                        double p = image.get(row, col)[0];
                        if (p != BLACK && p != WHITE) {
                            throw new RuntimeException("IMAGE IS NOT BINARY!");
                        }

                        double jNeighbour = getNeighbour(image, j, col, row);
                        if (p == object && jNeighbour == background) {
                            if (!anyPatternMatches(image, col, row)) {
                                copy.put(row, col, object);
                            } else {
                                copy.put(row, col, background);
                                remain = true;
                            }
                        }
                    }
                }
            }

            FilteringUtils.handleBorder(copy, border);
            copy.copyTo(image);
            stepImages.add(ImageUtils.mat2Image(image));
        }
    }

    /**
     * Odświeża slider do przesuwania kroków szkieletyzacji.
     */
    private void refreshSlider() {
        stepSlider.setMax(stepImages.size());
        stepSlider.setValue(stepImages.size());
        stepSlider.setDisable(false);
    }

    /**
     * Sprawdza, czy któryś z wzorców pasuje do sąsiedztwa obecnego piksela.
     *
     * @param image obraz
     * @param col   kolumna piksela
     * @param row   wiersz piksela
     * @return <tt>true</tt> jeśli co najmniej jeden wzorzec pasuje.
     */
    private boolean anyPatternMatches(Mat image, int col, int row) {
        for (double[] pattern : patterns) {
            if (patternMatches(pattern, image, col, row)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Sprawdza, czy wzorzec pasuje do sąsiedztwa piksela
     *
     * @param pattern wzorzec do sprawdzenia
     * @param image   obraz
     * @param col     kolumna piksela
     * @param row     wiersz piksela
     * @return <tt>true</tt> jeśli wzorzec pasuje.
     */
    private boolean patternMatches(double[] pattern, Mat image, int col, int row) {
        for (int i = 0; i < 9; i++) {
            if (pattern[i] == ANY) continue;
            double value = getNeighbour(image, i, col, row);

            if (pattern[i] != value) {
                return false;
            }
        }

        return true;
    }

    /**
     * Pobiera wartość sąsiedniego piksela o podanym indeksie.
     *
     * @param image          obraz
     * @param neighbourIndex indeks sąsiada
     * @param col            kolumna piksela
     * @param row            wiersz piksela
     * @return wartość sąsiedniego piksela
     */
    private double getNeighbour(Mat image, int neighbourIndex, int col, int row) {
        switch (neighbourIndex) {
            case 0:
                return image.get(row - 1, col - 1)[0];
            case 1:
                return image.get(row - 1, col)[0];
            case 2:
                return image.get(row - 1, col + 1)[0];
            case 3:
                return image.get(row, col - 1)[0];
            case 4:
                return image.get(row, col)[0];
            case 5:
                return image.get(row, col + 1)[0];
            case 6:
                return image.get(row + 1, col - 1)[0];
            case 7:
                return image.get(row + 1, col)[0];
            case 8:
                return image.get(row + 1, col + 1)[0];
        }

        throw new IllegalArgumentException();
    }

}
