package sheep.ui.graphical.javafx;

import javafx.application.Application;
import javafx.stage.Stage;
import sheep.core.SheetUpdate;
import sheep.core.SheetView;
import sheep.ui.UI;

import java.util.Map;

/**
 * The SheeP JavaFX application.
 * @stage0
 */
public class SheepApplication extends Application {
    /**
     * Construct a new SheeP Application with a sheet preloaded.
     * The application has a menu bar with each feature available.
     *
     * @param view A view of the primary sheet.
     * @param updater An updater for the primary sheet.
     * @param features A mapping of all the menu bar features.
     */
    public SheepApplication(SheetView view, SheetUpdate updater, Map<String, Map<String, UI.Feature>> features) {

    }

    /**
     * Start the SheeP Application.
     * Creates a new window to display and modify the sheet.
     * The scene has a menu bar with all the features.
     *
     * @param stage the primary stage.
     * @throws Exception if the application fails to run.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // This is the main entry point for your javafx code.
    }

    /**
     * Create a new window, with a new sheet attached.
     *
     * @param view a view of the new sheet.
     * @param updater an updater for the new sheet.
     */
    public void createWindow(SheetView view, SheetUpdate updater) {
    }
}