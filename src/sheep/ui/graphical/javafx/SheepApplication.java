package sheep.ui.graphical.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sheep.core.SheetUpdate;
import sheep.core.SheetView;
import sheep.expression.TypeError;
import sheep.parsing.ParseException;
import sheep.sheets.Sheet;
import sheep.sheets.SheetBuilder;
import sheep.ui.UI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static sheep.ui.graphical.Configuration.TITLE;

/**
 * The SheeP JavaFX application.
 *
 * @stage0
 */
public class SheepApplication extends Application {
    private SheetView view;
    private SheetUpdate updater;
    private Sheet sheet;
    private SheetBuilder sheetBuilder;
    private JFXUI ui;
    private final Map<String, Map<String, UI.Feature>> features;


    /**
     * Construct a new SheeP Application with a sheet preloaded.
     * The application has a menu bar with each feature available.
     *
     * @param view     A view of the primary sheet.
     * @param updater  An updater for the primary sheet.
     * @param features A mapping of all the menu bar features.
     */
    public SheepApplication(SheetView view,
                            SheetUpdate updater,
                            Map<String, Map<String, UI.Feature>> features) {
        this.view = view;
        this.updater = updater;
        this.features = features;
        this.ui = new JFXUI(view, updater);
    }

    private MenuItem createOpenMenu() {
        MenuItem menuItem = new MenuItem("Open");
        menuItem.setOnAction(e -> {
            try {
                openSpreadsheet();
            } catch (FileNotFoundException |TypeError |ParseException ex) {
                throw new RuntimeException(ex);
            }
        });
        return menuItem;
    }

    private MenuItem createSaveMenu() {
        MenuItem menuItem = new MenuItem("Save As");
        menuItem.setOnAction(e -> {
            saveAs();
        });

        return menuItem;
    }

    private void openSpreadsheet() throws FileNotFoundException, TypeError, ParseException {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Sheep files", "*.sheep");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show open file dialog
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                this.sheet = sheetBuilder.load(file.getAbsolutePath());
            } catch (FileNotFoundException | ParseException | TypeError e) {
                e.printStackTrace();
            }
        }
    }

    private void saveAs() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Sheep files", "*.sheep");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                String data = this.sheet.encode();
                Files.writeString(file.toPath(), data);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        stage.setTitle(TITLE);

        SheepView sheepView = new SheepView(view, updater);

        // Your existing methods to create MenuItems, assuming they are public
        MenuItem openMenu = createOpenMenu();
        MenuItem saveMenu = createSaveMenu();

        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(openMenu, saveMenu);

        MenuBar menuBar ;

        // Create a MenuBar and add the File menu
        menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);

        VBox topContainer = new VBox();
        topContainer.getChildren().addAll(menuBar, sheepView.getScene().getRoot());

        BorderPane rootPane = new BorderPane();
        rootPane.setTop(topContainer);
        rootPane.setCenter(sheepView.getTableView());

        Scene scene = new Scene(rootPane);

        stage.setScene(scene);
        stage.show();
    }
    /**
     * Create a new window, with a new sheet attached.
     *
     * @param view    a view of the new sheet.
     * @param updater an updater for the new sheet.
     */
    public void createWindow(SheetView view, SheetUpdate updater) {

    }
}