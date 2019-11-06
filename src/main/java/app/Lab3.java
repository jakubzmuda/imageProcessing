package app;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Lab3 {

    public void smoothing() {
        Stage stage = new Stage();
        stage.setTitle("Wygładzanie");

        GridPane container = new GridPane();

        Label maskLabel = new Label("Maska ");
        container.add(maskLabel, 0, 0);


        Label previewLabel = new Label("Podgląd ");
        container.add(previewLabel, 0, 2);

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
