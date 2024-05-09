package sheep.ui.graphical.javafx;

import javafx.scene.control.TableCell;

public class SelectedCell {
    private TableCell<CellData[], String> selectedCell = null;

    public void selectCell(TableCell<CellData[], String> cell) {
        if (selectedCell != null) {
            selectedCell.setStyle("-fx-background-color: #fff"
                    + "; -fx-text-fill: #000"
                    + "; -fx-alignment: CENTER;"
                    + "; -fx-font-size: 14px;"
                    + "");
        }

        selectedCell = cell;
        if (selectedCell != null) {
            selectedCell.setStyle("-fx-border-color: blue; -fx-border-style: solid;"
                    + "; -fx-background-color: #fff"
                    + "; -fx-text-fill: #000"
                    + "; -fx-alignment: CENTER;"
                    + "; -fx-font-size: 14px;");
        }
    }
}