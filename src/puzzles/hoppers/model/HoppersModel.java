package puzzles.hoppers.model;

import puzzles.common.Coordinates;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class HoppersModel {
    private final static String MADE_MOVE = "Jumped from %1$s to %2$s";
    private final static String SELECTED = "Selected %1$s";
    private final  static String INVALID_SELECT = "No frog at %1$s";
    private final static String INVALID_MOVE = "Can't jump from %1$s to %2$s.";
    private final static String LOADED_FILE = "Loaded: %1$s";
    private final static String FAILED_LOADING = "Failed to load: %1$s";
    private final static String NO_SOLUTION = "No solution.";
    private final static String NEXT_STEP = "Next step!";

    public final static String RESET = "Puzzle reset!";


    /** the collection of observers of this model */
    private final List<Observer<HoppersModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private HoppersConfig currentConfig;

    private String currentFileName;

    private Coordinates currentSelection = null;
    private Coordinates lastSelection = null;

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<HoppersModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * Load a hoppers input file into a new config and set it as the current one
     * @param filename the name of the file to load
     */
    public void load(String filename) {
        try {
            currentConfig = new HoppersConfig(filename);
            currentFileName = filename;
            alertObservers(String.format(LOADED_FILE, filename));
        } catch (IOException e) {
            alertObservers(String.format(FAILED_LOADING, filename));
        }
    }

    /**
     * Reset the model back to the original file's state
     */
    public void reset() {
        try {
            currentConfig = new HoppersConfig(currentFileName);
            currentSelection = null;
            lastSelection = null;
            alertObservers(RESET);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Find the correct next move
     */
    public void getHint() {
        Solver solver = new Solver(currentConfig);
        List<Configuration> path = solver.solve();
        if (path.size() <= 1) {
            alertObservers(NO_SOLUTION);
        } else {
            currentConfig = (HoppersConfig) path.get(1);
            alertObservers(NEXT_STEP);
        }
    }

    /**
     * Getter for the current config
     * @return the model's current config
     */
    public HoppersConfig getCurrentConfig() {
        return currentConfig;
    }

    /**
     * Select a position in the grid. If there is already one selected, attempt to move it to the new position selected
     * @param row the row of the position to select
     * @param col the column of the position to select
     */
    public void select(int row, int col) {
        char gridAt = currentConfig.getGrid()[row][col];
        if (currentSelection == null) {
            currentSelection = new Coordinates(row, col);
            if (gridAt != HoppersConfig.EMPTY && gridAt != HoppersConfig.INVALID) {
                alertObservers(String.format(SELECTED, currentSelection));
            } else {
                alertObservers(String.format(INVALID_SELECT, currentSelection));
                currentSelection = null;
            }
            lastSelection = currentSelection;
        } else {
            currentSelection = new Coordinates(row, col);
            if (currentConfig.makeMove(lastSelection.row(), lastSelection.col(), row, col)) {
                alertObservers(String.format(MADE_MOVE, lastSelection, currentSelection));
            } else {
                alertObservers(String.format(INVALID_MOVE, lastSelection, currentSelection));
            }
            currentSelection = null;
        }
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String msg) {
        for (var observer : observers) {
            observer.update(this, msg);
        }
    }

    /**
     * Create a hoppers model with a specified file to start with
     * @param filename the name of the file to load first
     * @throws IOException
     */
    public HoppersModel(String filename) throws IOException {
        this.currentFileName = filename;
        this.currentConfig = new HoppersConfig(currentFileName);
    }
}
