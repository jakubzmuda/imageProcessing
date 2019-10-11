package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox vBox = buildMainBox();
        primaryStage.setTitle("Image processing");
        primaryStage.setScene(new Scene(vBox, 1200, 1000));
        primaryStage.show();
    }

    private VBox buildMainBox() {
        ImageView imageView = new ImageView();
        ScrollPane imageContainer = new ScrollPane(imageView);

        MenuBar menuBar = buildMenuBox(imageView);
        VBox mainBox = new VBox(menuBar);
        mainBox.getChildren().add(imageContainer);
        return mainBox;
    }

    private MenuBar buildMenuBox(ImageView imageView) {
        Menu fileMenu = buildFileMenuTab(imageView);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);
        return menuBar;
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

    private MenuItem buildOpenImageMenuItem(ImageView imageView) {
        MenuItem openImageItem = new MenuItem("Otwórz");

        openImageItem.setOnAction(t -> {
            FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter anyFile = new FileChooser.ExtensionFilter("any file", "*");
            fileChooser.getExtensionFilters().addAll(anyFile);

            File file = fileChooser.showOpenDialog(null);

            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                imageView.setImage(image);
            } catch (IOException ignored) {
            }
        });
        return openImageItem;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
