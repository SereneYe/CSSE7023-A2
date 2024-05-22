package sheep.ui.graphical.javafx;

import sheep.core.ViewElement;

public class CellData {
    /**
     * The value of a cell in a spreadsheet.
     */
    private ViewElement value;

    /**
     * The formula of a cell in a spreadsheet.
     */
    private ViewElement formula;

    /**
     * The row index of a cell in a spreadsheet.
     */
    private int rowIndex;

    /**
     * The column index of a cell in a spreadsheet.
     */
    private int columnIndex;

    public CellData(ViewElement value, ViewElement formula, int rowIndex, int columnIndex) {
        this.value = value;
        this.formula = formula;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }

    public ViewElement getValue() {
        return value;
    }

    public ViewElement getFormula() {
        return formula;
    }

    public void setValue(ViewElement newValue) {
        this.value = newValue;
    }

    public void setFormula(ViewElement newFormula) {
        this.formula = newFormula;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }
}
