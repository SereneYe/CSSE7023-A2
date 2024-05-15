package sheep.ui.graphical.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sheep.core.SheetUpdate;
import sheep.core.SheetView;
import sheep.ui.UI;
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
    private JFXPrompt prompt;
    private final Map<String, Map<String, JFXUI.Feature>> features;


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
    }

    private MenuItem createMenuItem(String function, String category) {
        MenuItem menuItem = new MenuItem(function);
        menuItem.setOnAction(e -> {
            UI.Feature feature = features.get(category).get(function);
            if (feature != null) {
                feature.action().perform(0, 0, prompt, view, updater);
            }
        });
        return menuItem;
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
        prompt = new JFXPrompt(stage);
        stage.setTitle(TITLE);

        SheepView sheepView = new SheepView(view, updater);

        // Your existing methods to create MenuItems, assuming they are public
        MenuItem openMenu = createMenuItem("open", "File");
        MenuItem saveMenu = createMenuItem("save", "File");

        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(openMenu, saveMenu);

        MenuBar menuBar  = new MenuBar();
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
        try {
            // Create a new sheep application with the new view and updater
            SheepApplication newWindowApp = new SheepApplication(view, updater, features);

            // Start a new JavaFX application thread
            Platform.runLater(() -> {
                try {
                    Stage newStage = new Stage();
                    newWindowApp.start(newStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}