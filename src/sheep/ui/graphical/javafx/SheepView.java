package sheep.ui.graphical.javafx;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.layout.VBox;
import sheep.core.SheetView;
import sheep.core.ViewElement;
import sheep.sheets.Sheet;
import javafx.scene.paint.Color;
import sheep.ui.graphical.Configuration;

public class SheepView {

    private SheetView view;
    private TableView<ViewElement[]> sheetTableView;

    public SheepView(SheetView view) {
        this.view = view;
        sheetTableView = createTableView();
    }

    private TableView<ViewElement[]> createTableView() {
        TableView<ViewElement[]> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        disableColOrdering(tableView);

        TableColumn<ViewElement[], String> numberCol = createRowIndex(tableView);


        ObservableList<ViewElement[]> data = FXCollections.observableArrayList();
        for (int i = 0; i < view.getRows(); i++) {
            ViewElement[] row = new ViewElement[view.getColumns()];
            for (int j = 0; j < row.length; j++) {
                row[j] = view.valueAt(i, j);
            }
            data.add(row);
        }

        tableView.setItems(data);
        

        
        numberCol.setStyle("-fx-background-color: #" + toRGBCode(Configuration.HEADER_COLUMN_BACKGROUND)
                + "; -fx-text-fill: #" + toRGBCode(Configuration.HEADER_COLUMN_FOREGROUND)
                + "; -fx-alignment: CENTER;");
        numberCol.setPrefWidth(Configuration.HEADER_COLUMN_WIDTH);

        String columnHeaderStyle = "-fx-background-color: #" + toRGBCode(Configuration.HEADER_COLUMN_BACKGROUND) + "; "
                + "-fx-text-fill: #" + toRGBCode(Configuration.HEADER_COLUMN_FOREGROUND) + "; -fx-alignment: CENTER;";


        for (int columnIndex = 0; columnIndex < view.getColumns(); columnIndex++) {
            TableColumn<ViewElement[], String> column = new TableColumn<>((char) ('A' + columnIndex) + "");
            column.setSortable(false);
            column.setResizable(false);
            int finalColumnIndex = columnIndex;
            column.setCellValueFactory(cellData -> {
                ViewElement[] row = cellData.getValue();
                int rowIndex = data.indexOf(row);
                ViewElement ve = view.valueAt(rowIndex, finalColumnIndex);
                return new SimpleStringProperty(ve.getContent());
            });
            column.setStyle(columnHeaderStyle);
            tableView.getColumns().add(column);

        }

        return tableView;
    }

    private static TableColumn<ViewElement[], String> createRowIndex(TableView<ViewElement[]> tableView) {
        TableColumn<ViewElement[], String> numberCol = new TableColumn<>("");
        numberCol.setSortable(false);
        numberCol.setResizable(false);
        numberCol.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                this.setText(empty ? null : String.valueOf(this.getIndex()));
            }
        });
        tableView.getColumns().add(0, numberCol);
        return numberCol;
    }

    private static void disableColOrdering(TableView<ViewElement[]> tableView) {
        // Disable column reordering
        tableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> header.setReordering(false));
        });
    }

    public Scene getScene() {
        return new Scene(sheetTableView);
    }

    private String toRGBCode(java.awt.Color color) {
        return Integer.toHexString(color.getRGB() & 0x00ffffff);
    }
}