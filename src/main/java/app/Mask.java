package app;


import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class Mask {

    int i0, i1, i2, i3, i4, i5, i6, i7, i8;

    public Mask(int i0, int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.i0 = i0;
        this.i1 = i1;
        this.i2 = i2;
        this.i3 = i3;
        this.i4 = i4;
        this.i5 = i5;
        this.i6 = i6;
        this.i7 = i7;
        this.i8 = i8;
    }

    public Pane asTable() {
        GridPane table = new GridPane();

        table.setStyle("-fx-padding: 32px;");

        table.add(new Label("" + i0), 0, 0);
        table.add(new Label(" | "), 1, 0);
        table.add(new Label("" + i1), 2, 0);
        table.add(new Label(" | "), 3, 0);
        table.add(new Label("" + i2), 4, 0);

        spacers(table, 1);

        table.add(new Label("" + i3), 0, 2);
        table.add(new Label(" | "), 1, 2);
        table.add(new Label("" + i4), 2, 2);
        table.add(new Label(" | "), 3, 2);
        table.add(new Label("" + i5), 4, 2);

        spacers(table, 3);

        table.add(new Label("" + i6), 0, 4);
        table.add(new Label(" | "), 1, 4);
        table.add(new Label("" + i7), 2, 4);
        table.add(new Label(" | "), 3, 4);
        table.add(new Label("" + i8), 4, 4);

        return table;
    }

    private void spacers(GridPane table, int rowIndex) {
        table.add(new Label("--"), 0, rowIndex);
        table.add(new Label("---"), 1, rowIndex);
        table.add(new Label("--"), 2, rowIndex);
        table.add(new Label("---"), 3, rowIndex);
        table.add(new Label("--"), 4, rowIndex);
    }
}
