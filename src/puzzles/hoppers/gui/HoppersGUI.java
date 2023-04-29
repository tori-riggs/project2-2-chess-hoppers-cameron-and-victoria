package puzzles.hoppers.gui;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import puzzles.common.Observer;
import puzzles.hoppers.model.HoppersConfig;
import puzzles.hoppers.model.HoppersModel;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HoppersGUI extends Application implements Observer<HoppersModel, String> {
    /** The size of all icons, in square dimension */
    private final static int ICON_SIZE = 75;
    /** the font size for labels and buttons */
    private final static int FONT_SIZE = 12;

    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";

    // for demonstration purposes
    private final Image redFrog = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "red_frog.png")));
    private final Image greenFrog = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "green_frog.png")));
    private final Image lilyPad = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "lily_pad.png")));
    private final Image water = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "water.png")));
    private HoppersModel model;
    private Button[][] buttonGrid;
    private Label messageLabel = new Label();
    private Stage stage;

    public void init() throws IOException {
        String filename = getParameters().getRaw().get(0);
        this.model = new HoppersModel(filename);
        this.model.addObserver(this);
        this.buttonGrid = new Button[model.getCurrentConfig().getRows()][model.getCurrentConfig().getColumns()];
    }

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
                buttonGrid[r][c] = currentButton;
                pane.add(currentButton, c, r);
            }
        }
        return pane;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        GridPane grid = makeGrid();
        messageLabel = new Label("Loaded: " + getParameters().getRaw().get(0));
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        VBox vBox = new VBox(messageLabel, grid);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.setTitle("Hoppers GUI");
        stage.show();
    }

    @Override
    public void update(HoppersModel hoppersModel, String msg) {
        messageLabel.setText(msg);
        for (int r = 0; r < buttonGrid.length; r++) {
            for (int c = 0; c < buttonGrid[r].length; c++) {
                switch (model.getCurrentConfig().getGrid()[r][c]) {
                    case HoppersConfig.INVALID -> buttonGrid[r][c].setGraphic(new ImageView(water));
                    case HoppersConfig.RED_FROG -> buttonGrid[r][c].setGraphic(new ImageView(redFrog));
                    case HoppersConfig.GREEN_FROG -> buttonGrid[r][c].setGraphic(new ImageView(greenFrog));
                    case HoppersConfig.EMPTY -> buttonGrid[r][c].setGraphic(new ImageView(lilyPad));
                }
            }
        }
        this.stage.sizeToScene();  // when a different sized puzzle is loaded
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            Application.launch(args);
        }
    }
}
