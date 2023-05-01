package puzzles.hoppers.gui;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import puzzles.common.Observer;
import puzzles.hoppers.model.HoppersConfig;
import puzzles.hoppers.model.HoppersModel;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

/**
 *
 */
public class HoppersGUI extends Application implements Observer<HoppersModel, String> {
    /** The size of all icons, in square dimension */
    private final static int ICON_SIZE = 75;
    /** the font size for labels and buttons */
    private final static int FONT_SIZE = 12;

    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";
    private final Image redFrog = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "red_frog.png")));
    private final Image greenFrog = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "green_frog.png")));
    private final Image lilyPad = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "lily_pad.png")));
    private final Image water = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "water.png")));
    private HoppersModel model;
    private VBox root;
    private GridPane gridPane;
    private FileChooser fileChooser;

    private String currentPath;
    private Label messageLabel = new Label();
    private Stage stage;

    /**
     * Initializes the gui by creating the model and adding the gui as an observer
     * @throws IOException
     */
    public void init() throws IOException {
        String filename = getParameters().getRaw().get(0);
        this.model = new HoppersModel(filename);
        this.model.addObserver(this);
    }

    /**
     * Make a grid of buttons to represent the current model's grid
     * @return a GridPane representing the current model's grid
     */
    private GridPane makeGrid() {
        GridPane pane = new GridPane();
        int rows = model.getCurrentConfig().getRows();
        int cols = model.getCurrentConfig().getColumns();
        char[][] grid = model.getCurrentConfig().getGrid();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Button currentButton = new Button();
                currentButton.setMaxSize(ICON_SIZE, ICON_SIZE);
                currentButton.setMinSize(ICON_SIZE, ICON_SIZE);
                int finalR = r;
                int finalC = c;
                currentButton.setOnAction(e -> model.select(finalR, finalC));
                switch (grid[r][c]) {
                    case HoppersConfig.INVALID -> currentButton.setGraphic(new ImageView(water));
                    case HoppersConfig.RED_FROG -> currentButton.setGraphic(new ImageView(redFrog));
                    case HoppersConfig.GREEN_FROG -> currentButton.setGraphic(new ImageView(greenFrog));
                    case HoppersConfig.EMPTY -> currentButton.setGraphic(new ImageView(lilyPad));
                }
                pane.add(currentButton, c, r);
            }
        }
        return pane;
    }

    /**
     * Make the load, reset, and hint buttons and put them in a hbox
     * @return An Hbox holding the load, reset and hint buttons
     */
    private HBox makeLowerButtons() {
        Button loadButton = new Button("Load");
        loadButton.setOnAction(e -> {
            model.load(fileChooser.showOpenDialog(stage).getPath().replace(currentPath + File.separator, ""));
        });
        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> model.reset());
        Button hintButton = new Button("Hint");
        hintButton.setOnAction(e -> model.getHint());

        return new HBox(loadButton, resetButton, hintButton);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        gridPane = makeGrid();
        messageLabel = new Label("Loaded: " + getParameters().getRaw().get(0));
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        HBox lowerButtons = makeLowerButtons();
        lowerButtons.setAlignment(Pos.CENTER);

        fileChooser = new FileChooser();
        currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        String dataPath = currentPath + File.separator + "data" + File.separator + "hoppers";
        fileChooser.setInitialDirectory(new File(dataPath));

        root = new VBox(messageLabel, gridPane, lowerButtons);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Hoppers GUI");
        stage.show();
    }

    @Override
    public void update(HoppersModel hoppersModel, String msg) {
        messageLabel.setText(msg);
        root.getChildren().remove(gridPane);
        gridPane = makeGrid();
        root.getChildren().add(1, gridPane);
        this.stage.sizeToScene();  // when a different sized puzzle is loaded
    }

    /**
     * Launch the gui
     * @param args java HoppersPTUI filename
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            Application.launch(args);
        }
    }
}
