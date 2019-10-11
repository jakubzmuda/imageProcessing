package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


//TODO histogram i statystyki jakies do niego
public class Main extends Application {

    ImageView imageView;

    @Override
    public void start(Stage primaryStage) throws Exception{

        imageView = new ImageView();

        EventHandler<ActionEvent> btnLoadEventListener
                = new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent t) {
                FileChooser fileChooser = new FileChooser();

                //Set extension filter
                FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
                FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
                fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

                //Show open file dialog
                File file = fileChooser.showOpenDialog(null);

                try {
                    BufferedImage bufferedImage = ImageIO.read(file);
                    Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                    imageView.setImage(image);
                } catch (IOException ex) {
                }
            }
        };


        MenuItem menuItem1 = new MenuItem("Otwórz");
        menuItem1.setOnAction(btnLoadEventListener);
        MenuItem menuItem2 = new MenuItem("Zamknij");
        menuItem2.setOnAction(e -> {
            Platform.exit();
        });
        Menu menu1 = new Menu("Plik");
        menu1.getItems().addAll(menuItem1, menuItem2);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu1);
        VBox vBox = new VBox(menuBar);
        vBox.getChildren().add(imageView);

        primaryStage.setTitle("Image processing");
        primaryStage.setScene(new Scene(vBox, 600, 400));
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
