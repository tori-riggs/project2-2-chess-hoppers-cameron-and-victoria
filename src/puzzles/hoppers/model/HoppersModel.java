package puzzles.hoppers.model;

import puzzles.common.Coordinates;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.hoppers.solver.Hoppers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class HoppersModel {
    /** the collection of observers of this model */
    private final List<Observer<HoppersModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private HoppersConfig currentConfig;

    private String currentFileName;

    private Coordinates currentSelection = null;

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<HoppersModel, String> observer) {
        this.observers.add(observer);
    }

    public void load(String filename) {
        try {
            currentConfig = new HoppersConfig(filename);
            currentFileName = filename;
            alertObservers("Successfully loaded file: " + filename);
        } catch (IOException e) {
            alertObservers("Could not find file " + filename + ", returning to previously loaded file");
        }
    }

    public void getHint() {
        Solver solver = new Solver(currentConfig);
        List<Configuration> path = solver.solve();
        if (path.isEmpty()) {
            alertObservers("No solution.");
        } else {
            alertObservers(path.get(1).toString());
        }
    }

    public void select(int row, int col) {
            char gridAt = currentConfig.getGrid()[row][col];
            if (currentSelection == null) {
                if (gridAt != HoppersConfig.EMPTY) {
                    currentSelection = new Coordinates(row, col);
                    alertObservers("Selected (" + row + ", " + col + ")" + currentConfig.prettyToString());
                    return;
                }
            } else {
                if (currentConfig.makeMove(currentSelection.row(), currentSelection.col(), row, col)) {
                    alertObservers("Jumped from " + currentSelection + " to (" + row + ", " + col + ")" +
                            currentConfig.prettyToString());
                } else {
                    alertObservers("Invalid Move!" + currentConfig.prettyToString());
                }
                currentSelection = null;
                return;
            }
        alertObservers("No frog at (" + row + ", " + col + ")" + currentConfig.prettyToString());
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

    public HoppersModel(String filename) throws IOException {
        this.currentFileName = filename;
        this.currentConfig = new HoppersConfig(currentFileName);
    }
}
