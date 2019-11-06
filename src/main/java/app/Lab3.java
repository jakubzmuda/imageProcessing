package app;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Lab3 {

    public void smoothing() {
        Stage stage = new Stage();
        stage.setTitle("Wygładzanie");

        GridPane container = new GridPane();

        Mask mask1 = new Mask(0, 1, 0, 1, 4, 1, 0, 1, 0);
        container.add(mask1.asTable(), 0, 0);

        Mask mask2 = new Mask(1, 1, 1, 1, 1, 1, 1, 1, 1);
        container.add(mask2.asTable(), 1, 0);

        Mask mask3 = new Mask(1, 2, 1, 2, 4, 2, 1, 2, 1);
        container.add(mask3.asTable(), 2, 0);

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
