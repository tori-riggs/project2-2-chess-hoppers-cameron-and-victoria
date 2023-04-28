package puzzles.chess.model;

import javafx.geometry.Pos;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ChessModel {
    /** the collection of observers of this model */
    private final List<Observer<ChessModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private ChessConfig currentConfig;

    /** is there a current cell selection? */
    private boolean isSelection;
    private Position currSelection;
    private String filename;

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<ChessModel, String> observer) {
        this.observers.add(observer);
    }

    public void load(String filename) {
        try {
            // TODO reload init?
            currentConfig = new ChessConfig(filename);
            alertObservers("Loaded: " + filename);
        } catch(IOException e) {
            alertObservers("Could not find file " + filename
                    + ", previous file will remain loaded.");
        }
    }

    public void hint() {
        Solver solve = new Solver(currentConfig);
        List<Configuration> path = solve.solve();
        if (path.isEmpty()) {
            alertObservers("No solution.");
        } else {
            // get next part of solution
            alertObservers(path.get(1).toString());
        }
    }

    public void select(int row, int col) {
        if (!isSelection) {
            if (currentConfig.isValidPos(row, col)) {
                currSelection = new Position(row, col,
                        currentConfig.getCellPiece(row, col));
                isSelection = true;
            } else {
                alertObservers("Invalid selection");
                isSelection = false;
            }
        } else {
            if (currentConfig.isValidPos(row, col)
                    && currentConfig.isCapture(row, col)) {
                ChessConfig endConfig = new ChessConfig(currentConfig,
                        currSelection.getRow(), currSelection.getCol(),
                        row, col);
                currentConfig = endConfig;
                isSelection = false;
            } else {
                alertObservers("Invalid move");
            }
        }
    }

    public void reset() {
        try {
            currentConfig = new ChessConfig(filename);
            alertObservers("Loaded: " + filename);
        } catch(IOException ex) {
            alertObservers("Reset error with" + filename);
        }
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }

    public ChessModel(String filename) throws IOException {
        this.currentConfig = new ChessConfig(filename);
        alertObservers("Loaded: " + filename);
    }
}
