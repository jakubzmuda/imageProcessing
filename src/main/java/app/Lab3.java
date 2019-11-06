package app;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Lab3 {

    public void smoothing() {
        Stage stage = new Stage();
        stage.setTitle("Wygładzanie");

        GridPane container = new GridPane();

        Mask mask1 = new Mask(0, 1, 0, 1, 4, 1, 0, 1, 0);
        Button chooseMask1Button = new Button("Wybierz");
        VBox mask1Box = new VBox(mask1.asTable(), chooseMask1Button);
        mask1Box.setAlignment(Pos.CENTER);
        mask1Box.setStyle("-fx-padding: 16px;");
        container.add(mask1Box, 0, 0);

        Mask mask2 = new Mask(1, 1, 1, 1, 1, 1, 1, 1, 1);
        Button chooseMask2Button = new Button("Wybierz");
        VBox mask2Box = new VBox(mask2.asTable(), chooseMask2Button);
        mask2Box.setAlignment(Pos.CENTER);
        mask2Box.setStyle("-fx-padding: 16px;");
        container.add(mask2Box, 1, 0);

        Mask mask3 = new Mask(1, 2, 1, 2, 4, 2, 1, 2, 1);
        Button chooseMask3Button = new Button("Wybierz");
        VBox mask3Box = new VBox(mask3.asTable(), chooseMask3Button);
        mask3Box.setAlignment(Pos.CENTER);
        mask3Box.setStyle("-fx-padding: 16px;");
        container.add(mask3Box, 2, 0);

        Scene scene = new Scene(container, 600, 600);

        Button doIt = new Button("Kontynuuj");
        doIt.setOnAction((event) -> {
//                Image newImage = new ImageOperations().threshold();
//                updateImage(newImage);
            stage.close();
        });

        container.setAlignment(Pos.CENTER);
        container.add(doIt, 0, 3);
        stage.setScene(scene);
        stage.show();
    }
}
