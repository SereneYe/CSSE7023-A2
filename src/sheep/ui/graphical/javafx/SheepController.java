package sheep.ui.graphical.javafx;
import sheep.core.ViewElement;

public class SheepController {
    SelectedCell selectedCell;
    SheepView sheepView;

    public SheepController(SheepView view) {
        selectedCell = new SelectedCell();
        sheepView = view;

        sheepView.setCellClickHandler((cellData, tableCell) -> {
            selectedCell.selectCell(tableCell);
            if (cellData != null) {
                ViewElement value = cellData.getValue();
                ViewElement formula = cellData.getFormula();

                sheepView.showFormulaInLabel(cellData);
                cellData.setValue(formula);
                cellData.setFormula(value);
                tableCell.setText(cellData.getValue().getContent());
            }
        });
    }
}