package puzzles.hoppers.model;

import puzzles.common.Observer;
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
        List<String> path = solver.solve();
        if (!path.isEmpty()) {
            alertObservers(path.get(0));
        } else {
            alertObservers("No solution!");
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

    public HoppersModel(String filename) throws IOException {
        this.currentFileName = filename;
        this.currentConfig = new HoppersConfig(currentFileName);
    }
}
