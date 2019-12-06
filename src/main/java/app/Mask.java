package app;


import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    @Override
    public String toString() {
        return "Mask{" +
                "i0=" + i0 +
                ", i1=" + i1 +
                ", i2=" + i2 +
                ", i3=" + i3 +
                ", i4=" + i4 +
                ", i5=" + i5 +
                ", i6=" + i6 +
                ", i7=" + i7 +
                ", i8=" + i8 +
                '}';
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

    public Pane asInteractiveTable() {
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

        TextField interactiveField = new TextField("" + i4);
        interactiveField.setMaxWidth(48);
        interactiveField.setOnKeyReleased(event -> {
            this.i4 = Integer.parseInt(interactiveField.getText());
        });
        table.add(interactiveField, 2, 2);

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

    public int sum() {
        return i0 + i1 + i2 + i3 + i4 + i5 + i6 + i7 + i8;
    }

    public int sumForNonNull(Neighbourhood3x3 neighbourhood) {
        return
                (neighbourhood.i0 != null ? i0 : 0)
                        + (neighbourhood.i1 != null ? i1 : 0)
                        + (neighbourhood.i2 != null ? i2 : 0)
                        + (neighbourhood.i3 != null ? i3 : 0)
                        + (neighbourhood.i4 != null ? i4 : 0)
                        + (neighbourhood.i5 != null ? i5 : 0)
                        + (neighbourhood.i6 != null ? i6 : 0)
                        + (neighbourhood.i7 != null ? i7 : 0)
                        + (neighbourhood.i8 != null ? i8 : 0);
    }
}
