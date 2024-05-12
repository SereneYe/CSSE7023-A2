package sheep.ui.graphical.javafx;

import sheep.core.ViewElement;

public class CellData {
    private ViewElement value;
    private ViewElement formula;
    private int rowIndex;
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
