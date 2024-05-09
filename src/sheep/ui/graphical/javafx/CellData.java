package sheep.ui.graphical.javafx;

import sheep.core.ViewElement;

public class CellData {
    private ViewElement value;
    private ViewElement formula;

    public CellData(ViewElement value, ViewElement formula) {
        this.value = value;
        this.formula = formula;
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
}
