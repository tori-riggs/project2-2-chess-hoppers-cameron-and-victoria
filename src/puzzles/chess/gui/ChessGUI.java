package puzzles.chess.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import puzzles.chess.model.ChessConfig;
import puzzles.chess.model.Position;
import puzzles.common.Observer;
import puzzles.chess.model.ChessModel;
import puzzles.hoppers.model.HoppersModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ChessGUI extends Application implements Observer<ChessModel, String> {
    private ChessModel model;

    /** The size of all icons, in square dimension */
    private final static int ICON_SIZE = 75;
    /** the font size for labels and buttons */
    private final static int FONT_SIZE = 12;

    private Stage stage;
    private BorderPane gameLayout;

    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";

    // for demonstration purposes
    private Image bishop = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"bishop.png"));
    private Image blue = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"blue.png"));
    private Image king = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"king.png"));
    private Image knight = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"knight.png"));
    private Image pawn = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"pawn.png"));
    private Image queen = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"queen.png"));
    private Image rook = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"rook.png"));

    /** a definition of light and dark and for the button backgrounds */
    private static final Background LIGHT =
            new Background( new BackgroundFill(Color.WHITE, null, null));
    private static final Background DARK =
            new Background( new BackgroundFill(Color.MIDNIGHTBLUE, null, null));
    private Label gameMessage;
    private String filename;

    /**
     *
     * @throws IOException
     */
    @Override
    public void init() throws IOException {
        // get the file name from the command line
        String filename = getParameters().getRaw().get(0);
        this.model = new ChessModel(filename);
        model.addObserver(this);
    }

    /**
     *
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.filename = model.getFilename();
        this.gameMessage = new Label("Loaded: " + filename);
        this.gameLayout = makeGameLayout();
        gameMessage.setAlignment(Pos.TOP_CENTER);
        Scene scene = new Scene(gameLayout);
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Make the main game layout
     *
     * @return the borderpane of the main game layout
     */
    private BorderPane makeGameLayout() {
        BorderPane borderPane = new BorderPane();
        // Top
        BorderPane topPane = new BorderPane();
        topPane.setCenter(gameMessage);
        borderPane.setTop(topPane);

        borderPane.setCenter(chessBoard());

        // Bottom
        BorderPane bottomPane = new BorderPane();
        HBox buttonBox = makeButtons();
        buttonBox.setAlignment(Pos.CENTER);
        bottomPane.setCenter(buttonBox);
        borderPane.setBottom(bottomPane);
        return borderPane;
    }

    /**
     * Make the load, reset, and hint buttons
     *
     * @return an hbox containing the load, reset, and hint buttons
     */
    private HBox makeButtons() {
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        Button load = new Button("Load");
        Button reset = new Button("Reset");
        Button hint = new Button("Hint");
        buttonBox.getChildren().addAll(load, reset, hint);


        FileChooser chooser = new FileChooser();
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        String datapath = currentPath + File.separator + "data" + File.separator + "chess";
        chooser.setInitialDirectory(new File(datapath));
        load.setOnAction(event ->
                model.load(chooser.showOpenDialog(stage).getPath().replace(
                        currentPath + File.separator, "")));

        reset.setOnAction(event -> model.reset());
        hint.setOnAction(event -> {
            if (model.getPieces().size() == 1) {
                gameMessage.setText("Already solved!");
                hint.disableProperty();
            } else {
                model.hint();
            }
        });
        return buttonBox;
    }

    /**
     * Make the chessboard of the GUI made up of buttons
     *
     * @return a gridpane of the chess buttons
     */
    private GridPane chessBoard() {
        GridPane board = new GridPane();
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                Button button = new Button();
                setPieceGraphic(button, r, c);

                if ((r + c) % 2 == 0) {
                    button.setBackground(LIGHT);
                } else {
                    button.setBackground(DARK);
                }
                int finalR = r;
                int finalC = c;
                button.setOnAction(e -> model.select(finalR, finalC));
                button.setMinSize(ICON_SIZE, ICON_SIZE);
                button.setMaxSize(ICON_SIZE, ICON_SIZE);
                button.setAlignment(Pos.CENTER);
                board.add(button, c, r);
            }
        }
        return board;
    }

    /**
     * Set the image for the piece
     *
     * @param button the button to change the graphic of
     * @param row the row of the button
     * @param col the column of the button
     */
    private void setPieceGraphic(Button button, int row, int col) {
        if (model.getCellPiece(row, col) == ChessConfig.BISHOP) {
            button.setGraphic(new ImageView(bishop));
        } else if (model.getCellPiece(row, col) == ChessConfig.KING) {
            button.setGraphic(new ImageView(king));
        } else if (model.getCellPiece(row, col) == ChessConfig.KNIGHT) {
            button.setGraphic(new ImageView(knight));
        } else if (model.getCellPiece(row, col) == ChessConfig.PAWN) {
            button.setGraphic(new ImageView(pawn));
        } else if (model.getCellPiece(row, col) == ChessConfig.QUEEN) {
            button.setGraphic(new ImageView(queen));
        } else if (model.getCellPiece(row, col) == ChessConfig.ROOK) {
            button.setGraphic(new ImageView(rook));
        }
    }

    @Override
    public void update(ChessModel chessModel, String msg) {
        gameMessage.setText(msg);
        gameLayout.setCenter(chessBoard());
        this.stage.sizeToScene();  // when a different sized puzzle is loaded
        if (model.getPieces().size() == 1) {
            gameMessage.setText(msg);
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
