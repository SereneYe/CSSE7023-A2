package sheep.ui.graphical.javafx;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.layout.BorderPane;
import sheep.core.SheetUpdate;
import sheep.core.SheetView;
import sheep.core.UpdateResponse;
import sheep.core.ViewElement;
import sheep.ui.graphical.Configuration;


import java.util.Objects;

public class SheepView {

    /**
     * Handles rendering of cells within a sheet.
     */
    private final SheetView view;

    /**
     * TableView object used for rendering cells within a sheet.
     */
    private final TableView<CellData[]> sheetTableView;

    /**
     * Used in the SheepView class for updating the value of a cell in the sheet.
     */
    private final SheetUpdate sheetUpdate;

    /**
     * Represents a label used to display a formula in a graphical user interface.
     */
    private Label formulaLabel;


    /**
     * Creates a SheepView object.
     *
     * @param view The SheetView object that handles rendering of cells within a sheet.
     * @param sheetUpdate The SheetUpdate object that handles replacing the value of a
     *                    cell with a given input.
     * @require view != null
     * @require sheetUpdate != null
     */
    public SheepView(SheetView view, SheetUpdate sheetUpdate) {
        this.view = view;
        this.sheetUpdate = sheetUpdate;
        sheetTableView = createTableView();
    }


    /**
     * Returns the TableView object associated with the SheepView class.
     *
     * @return The TableView object used for rendering cells within a sheet.
     */
    public TableView<CellData[]> getTableView() {
        return sheetTableView;
    }

    /**
     * Retrieves the Scene object for displaying the sheet view.
     * The scene consists of a BorderPane with the formula label at the top
     * and the sheetTableView at the center.
     *
     * @return The Scene object for displaying the sheet view.
     */
    public Scene getScene() {
        formulaLabel = new Label("");
        formulaLabel.setPrefHeight(Configuration.ROW_HEIGHT+5);
        formulaLabel.setAlignment(Pos.CENTER);


        BorderPane pane = new BorderPane();
        pane.setTop(formulaLabel); // Set the VBox as the top
        pane.setCenter(sheetTableView);
        return new Scene(pane);
    }

    /**
     * Creates a TableView object for rendering cells within a sheet.
     *
     * @return The created TableView object.
     */
    TableView<CellData[]> createTableView() {
        TableView<CellData[]> tableView = new TableView<>();
        disableColOrdering(tableView);

        // Create an ObservableList of CellData arrays representing the cell data grid
        ObservableList<CellData[]> cellDataGrid = populateCellDataGrid(tableView);

        // Create columns for the TableView
        for (int columnIndex = 0; columnIndex <= view.getColumns(); columnIndex++) {
            TableColumn<CellData[], String> column = new TableColumn<>();

            // If columnIndex > 0, name the column with a letter, else, leave it empty
            column.setText(columnIndex > 0 ? numberToLetter(columnIndex - 1) : "");

            // Set the first column and the cell data sheet
            if (columnIndex == 0) {
                setFirstCol(column, cellDataGrid);
            } else {
                setCellValueFactoryCol(column, columnIndex);
                setCellFactoryCol(column, tableView, columnIndex);
                column.setSortable(false);
                column.setResizable(false);
                column.setPrefWidth(Configuration.COLUMN_WIDTH);
            }

            tableView.getColumns().add(column);
        }
        // handle the mouse click event of the whole table view (for label rendering)
        clickRenderGlobal(tableView);
        return tableView;
    }

    /**
     * Disables column reordering for the given TableView.
     *
     * @param tableView The TableView to disable column reordering for.
     */
    private static void disableColOrdering(TableView<CellData[]> tableView) {
        tableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener
                    ((o, oldVal, newVal) -> header.setReordering(false));
        });
    }

    /**
     * Populates the cell data grid for the TableView.
     *
     * @param tableView The TableView object used for rendering cells within a sheet.
     * @return The ObservableList of CellData arrays representing the cell data grid.
     */
    private ObservableList<CellData[]> populateCellDataGrid(TableView<CellData[]> tableView) {
        ObservableList<CellData[]> cellDataGrid = FXCollections.observableArrayList();
        for (int i = 1; i < view.getRows() + 1; i++) {
            CellData[] row = new CellData[view.getColumns() + 1];

            for (int j = 1; j < row.length; j++) {
                ViewElement value = view.valueAt(i - 1, j - 1);
                ViewElement formula = view.formulaAt(i - 1, j - 1);
                row[j] = new CellData(value, formula, i - 1, j - 1);
            }

            cellDataGrid.add(row);
        }

        tableView.setItems(cellDataGrid);
        return cellDataGrid;
    }

    /**
     * Converts a given number to its corresponding letter representation.
     *
     * @param number The number to convert to letter.
     * @return The letter representation of the given number.
     */
    private String numberToLetter(int number) {
        return String.valueOf((char) (number + 'A'));
    }

    /**
     * Sets the preferred width of the given TableColumn  specified in the Configuration class.
     *
     * @param column The TableColumn object to modify.
     * @param cellDataGrid The ObservableList of CellData arrays.
     */
    private static void setFirstCol(TableColumn<CellData[], String> column,
                                     ObservableList<CellData[]> cellDataGrid) {
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

    }

    /**
     * Enables cell selection in the given TableView and handles the mouse click event.
     * When a cell is clicked, it retrieves the formula content from the corresponding CellData
     * object and displays it in a label.
     *
     * @param tableView The TableView object to enable cell selection and handle click event.
     */
    private void clickRenderGlobal(TableView<CellData[]> tableView) {
        tableView.getSelectionModel().setCellSelectionEnabled(true);

        // Handle the mouse click event directly in the view now.
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                ObservableList<TablePosition> selectedCells =
                        tableView.getSelectionModel().getSelectedCells();
                for (TablePosition tablePosition : selectedCells) {
                    int rowIndex = tablePosition.getRow();
                    int colIndex = tablePosition.getColumn();

                    CellData cellData = tableView.getItems().get(rowIndex)[colIndex];
                    if (cellData != null) {
                        // Print the formula content for debugging
                        System.out.println("Formula: " + cellData.getFormula().getContent());
                        formulaLabel.setText(Objects.requireNonNullElse(
                                cellData.getFormula().getContent(), ""));
                    }
                }
            }
        });
    }

    /**
     *
     * Sets the cell value factory for a TableColumn.
     *
     * @param column The TableColumn object to set the cell value factory for.
     * @param finalColumnIndex The final column index used to retrieve the value from the CellData array.
     */
    private static void setCellValueFactoryCol(TableColumn<CellData[], String> column,
                                               int finalColumnIndex) {
        column.setCellValueFactory(cellData -> {
            if (cellData.getValue()[finalColumnIndex] != null) {
                return new SimpleStringProperty(
                        cellData.getValue()[finalColumnIndex].getValue().getContent());
            } else {
                return new SimpleStringProperty("");
            }
        });
    }


    /**
     * Sets the cell factory for a TableColumn.
     *
     * @param column            The TableColumn object to set the cell factory for.
     * @param tableView         The TableView object in which the TableColumn is present.
     * @param finalColumnIndex  The column index used to retrieve the value from the CellData.
     */
    private void setCellFactoryCol(TableColumn<CellData[], String> column,
                                   TableView<CellData[]> tableView, int finalColumnIndex) {
        column.setCellFactory(col -> new CustomTableCell(tableView, finalColumnIndex));
    }

    /**
     * CustomTableCell class used to create custom table cells for the TableView.
     */
    private class CustomTableCell extends TableCell<CellData[], String> {

        private final TableView<CellData[]> tableView;

        private final int columnIndex;

        TextField textField= new TextField();

        ChangeListener<Boolean> focusLostListener;

        /**
         * Constructs a CustomTableCell object with the given TableView and finalColumnIndex.
         *
         * @param tableView        The TableView object in which the CustomTableCell is present.
         * @param columnIndex The column index used to retrieve the value from the CellData.
         */
        public CustomTableCell(TableView<CellData[]> tableView, int columnIndex) {
            this.tableView = tableView;
            this.columnIndex = columnIndex;
            setupTextField();
            setOnMouseClickedHandler();

        }

        protected void setupTextField() {
            textField.setOnAction(event -> {
                commitEdit(textField.getText());
            });
        }

        protected void setOnMouseClickedHandler() {
            setOnMouseClicked(event -> {
                if (!isEmpty() && event.getClickCount() == 1) {
                    startEdit();
                }
            });
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            setText(empty ? null : item);
            setStyle("-fx-background-color: #fff"
                    + "; -fx-text-fill: #000"
                    + "; -fx-alignment: CENTER;"
                    + "; -fx-font-size: 14px;");
        }

        @Override
        public void startEdit() {
            super.startEdit();

            CellData cellData = getCellData();

            ViewElement formula = cellData.getFormula();
            String formulaContent = formula != null ? formula.getContent() : "";
            textField.setText(formulaContent);

            setText(null);
            setGraphic(textField);

            textField.selectAll();
            textField.requestFocus();
            // Add focus lost listener
            focusLostListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                    commitEdit(textField.getText());
                }
            };
            textField.focusedProperty().addListener(focusLostListener);
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            // Get focus lost listener and remove it
            if (focusLostListener != null) {
                textField.focusedProperty().removeListener(focusLostListener);
            }

            CellData cellData = getCellData();

            if (cellData != null) {
                // Change this part to render value instead of formula
                ViewElement value = cellData.getValue();
                String valueContent = value != null ? value.getContent() : "";
                setText(valueContent);
            } else {
                setText("");
            }
            setGraphic(null);
        }

        @Override
        public void commitEdit(String newValue) {
            super.commitEdit(newValue);

            if (focusLostListener != null) {
                textField.focusedProperty().removeListener(focusLostListener);
            }

            CellData cellData = getCellData();

            System.out.println("New value: " + newValue);

            // Update cell value according to the formula.
            int rowIndex = cellData.getRowIndex();
            int columnIndex = cellData.getColumnIndex();
            UpdateResponse response = sheetUpdate.update(rowIndex, columnIndex,
                    newValue);
            if (response.isSuccess()) {
                refreshView();
               // Set label value to the updated formula
                formulaLabel.setText(Objects.requireNonNullElse(newValue, ""));
            } else {
                // If update failed, handle the error
                showAlert("Update Failed", "Invalidate formula: " + response.getMessage());
            }

            // Remove focus from the TextField
            Platform.runLater(() -> {
                tableView.requestFocus(); // request focus on another node
                tableView.getSelectionModel().clearSelection(); // clear selection
                cancelEdit(); // cancel editing
            });
        }

        private CellData getCellData() {
            try {
                int rowIndex = getIndex();
                CellData[] row = getTableView().getItems().get(rowIndex);
                return row[columnIndex];
            } catch (IndexOutOfBoundsException e) {
//                System.out.println("Caught an exception: " + e.getMessage());
            }
            return null;
        }

        private void refreshView() {
            for (CellData[] row : sheetTableView.getItems()) {
                for (CellData cellData : row) {
                    if (cellData != null) {
                        int rowIndex = cellData.getRowIndex();
                        int columnIndex = cellData.getColumnIndex();
                        ViewElement value = view.valueAt(rowIndex, columnIndex);
                        ViewElement formula = view.formulaAt(rowIndex, columnIndex);
                        cellData.setValue(value);
                        cellData.setFormula(formula);
                    }
                }
            }
            sheetTableView.refresh();
        }
    }

    private void showAlert(String updateFailed, String failMessage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(updateFailed);
        alert.setHeaderText(null);
        alert.setContentText(failMessage);

        Platform.runLater(alert::showAndWait);
    }
}
