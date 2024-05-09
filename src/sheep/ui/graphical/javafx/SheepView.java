package sheep.ui.graphical.javafx;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.layout.BorderPane;
import sheep.core.SheetView;
import sheep.core.ViewElement;
import sheep.ui.graphical.Configuration;

import java.util.function.BiConsumer;


public class SheepView {

    private SheetView view;
    private TableView<CellData[]> sheetTableView;
    private Label formulaLabel;
    private SelectedCell selectedCell = new SelectedCell();
    private BiConsumer<CellData, TableCell<CellData[], String>> cellClickHandler;

    public SheepView(SheetView view) {
        this.view = view;
        sheetTableView = createTableView(selectedCell);
    }

    TableView<CellData[]> createTableView(SelectedCell selectedCell) {
        TableView<CellData[]> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        ObservableList<CellData[]> cellDataGrid = FXCollections.observableArrayList();

        // Adjust column and row index by adding 1
        for (int i = 1; i < view.getRows() + 1; i++) {
            CellData[] row = new CellData[view.getColumns() + 1];

            if (i > 0) {
                for (int j = 1; j < row.length; j++) {
                    if (j > 0) {
                        ViewElement value = view.valueAt(i - 1, j - 1);
                        ViewElement formula = view.formulaAt(i - 1, j - 1);
                        row[j] = new CellData(value, formula);
                    }
                }
            }

            cellDataGrid.add(row);
        }

        tableView.setItems(cellDataGrid);

        for (int columnIndex = 0; columnIndex <= view.getColumns(); columnIndex++) {
            TableColumn<CellData[], String> column = new TableColumn<>();
            // If columnIndex > 0, name the column with a letter, otherwise set it to an empty string.
            column.setText(columnIndex > 0 ? numberToLetter(columnIndex - 1) : "");

            int finalColumnIndex = columnIndex;
            if (columnIndex == 0) {
                firstColView(column, cellDataGrid);

            } else {
                {
                    column.setCellValueFactory(cellData -> {
                        if (cellData.getValue()[finalColumnIndex] != null) {
                            return new SimpleStringProperty(String.valueOf(cellData.getValue()[finalColumnIndex].getValue().getContent()));
                        } else {
                            return new SimpleStringProperty("");
                        }
                    });

                    column.setCellFactory(column1 -> new TableCell<>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);

                            setText(empty ? null : item);
                            setStyle("-fx-background-color: #fff"
                                    + "; -fx-text-fill: #000"
                                    + "; -fx-alignment: CENTER;"
                                    + "; -fx-font-size: 14px;");

                            setOnMouseClicked(event -> {
                                if (cellClickHandler != null && !empty && event.getClickCount() == 1) {
                                    int rowIndex = getIndex();
                                    CellData[] row = getTableView().getItems().get(rowIndex);
                                    CellData cellData = row[finalColumnIndex];
                                    cellClickHandler.accept(cellData, this);
                                }
                            });
                        }
                    });
                }

                column.setSortable(false);
                column.setResizable(false);
                column.setPrefWidth(Configuration.COLUMN_WIDTH);
            }
            tableView.getColumns().add(column);
        }

        disableColOrdering(tableView);

        return tableView;
    }

    public void setCellClickHandler(BiConsumer<CellData, TableCell<CellData[], String>> clickHandler) {
        this.cellClickHandler = clickHandler;
    }

    private static void firstColView(TableColumn<CellData[], String> column, ObservableList<CellData[]> cellDataGrid) {
        column.setPrefWidth(Configuration.HEADER_COLUMN_WIDTH);
        column.setCellValueFactory(cellData -> {
            int rowIndex = 0;
            for (int i = 0; i < cellDataGrid.size(); i++) {
                if (cellDataGrid.get(i) == cellData.getValue()) {
                    rowIndex = i;
                    break;
                }
            }

            return new SimpleStringProperty(String.valueOf(rowIndex));

        });
        column.setCellFactory(column1 -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                // Style updates
                setStyle("-fx-background-color: #fff"
                        + "; -fx-text-fill: #aaa"
                        + "; -fx-alignment: CENTER;"
                        + "; -fx-font-size: 14px;");
            }
        });
    }

    private String numberToLetter(int number) {
        return String.valueOf((char) (number + 'A'));
    }

    private static void disableColOrdering(TableView<CellData[]> tableView) {
        // Disable column reordering
        tableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> header.setReordering(false));
        });
    }

    public void showFormulaInLabel(CellData cellData) {
        ViewElement formula = cellData.getFormula();
        if (formula != null) {
            formulaLabel.setText(formula.getContent());
        } else {
            formulaLabel.setText("");
        }
    }

    public Scene getScene() {
        formulaLabel = new Label("");
        formulaLabel.setAlignment(Pos.CENTER);
        BorderPane pane = new BorderPane();
        pane.setTop(formulaLabel);
        pane.setCenter(sheetTableView);

        Scene scene = new Scene(pane);

        return scene;
    }
}
