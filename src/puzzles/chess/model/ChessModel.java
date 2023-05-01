package puzzles.chess.model;

import javafx.geometry.Pos;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.ArrayList;
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
            ChessConfig temp = new ChessConfig(filename);
            currentConfig = temp;
            this.filename = filename;
            alertObservers("Loaded: " + filename);
        } catch(IOException e) {
            alertObservers("Failed to load: " + filename);
        }
    }

    public void hint() {
        Solver solve = new Solver(currentConfig);
        List<Configuration> path = solve.solve();
        if (path.isEmpty()) {
            alertObservers("No solution.");
        } else {
            // get next part of solution
            if (!path.isEmpty()) {
                currentConfig = (ChessConfig) path.get(1);
                alertObservers("Next step!");
            } else if (getPieces().size() == 1) {
                alertObservers("Already solved!");
            }
        }
    }

    public void select(int row, int col) {
        if (!isSelection) {
            if (currentConfig.isValidPos(row, col) &&
                    !currentConfig.isEmpty(row, col)) {
                currSelection = new Position(row, col,
                        currentConfig.getCellPiece(row, col));
                alertObservers("Selected " + currSelection.toString());
//                System.out.println(currentConfig.toString());
                isSelection = true;
            } else {
                alertObservers("Invalid selection (" + row + ", " + col + ")");
//                System.out.println(currentConfig.toString());
                isSelection = false;
            }
        } else {
                Position endCell = new Position(row, col,
                        currentConfig.getCellPiece(row, col));
                // Make new ChessConfig assuming the capture is valid
                ChessConfig endConfig = new ChessConfig(currentConfig,
                        currSelection.getRow(), currSelection.getCol(),
                        endCell.getRow(), endCell.getCol());

                ArrayList<Configuration> validMoves = new ArrayList<>();
                char currPiece = currSelection.getPiece();
                if (currPiece == ChessConfig.PAWN) {
                    validMoves.addAll(currentConfig.pawnMoves(currSelection));
                } else if (currPiece == ChessConfig.BISHOP) {
                    validMoves.addAll(currentConfig.bishopMoves(currSelection));
                } else if (currPiece == ChessConfig.KING) {
                    validMoves.addAll(currentConfig.kingMoves(currSelection));
                } else if (currPiece == ChessConfig.KNIGHT) {
                    validMoves.addAll(currentConfig.knightMoves(currSelection));
                } else if (currPiece == ChessConfig.ROOK) {
                    validMoves.addAll(currentConfig.rookMoves(currSelection));
                } else if (currPiece == ChessConfig.QUEEN) {
                    validMoves.addAll(currentConfig.queenMoves(currSelection));
                }

                boolean valid = false;
                for (Configuration c : validMoves) {
                    if (endConfig.equals(c)
                            && !(currSelection.equals(endCell))) {
                        valid = true;
                        currentConfig = endConfig;
                        alertObservers("Captured from "
                                + currSelection.toString() + " to "
                                + endCell.toString());
                        isSelection = false;
                    }
                }
                if (!valid) {
                    alertObservers("Can't capture from "
                            + currSelection.toString() + " to "
                            + endCell.toString());
                    isSelection = false;
                }
        }
    }

    public void reset() {
        try {
            currentConfig = new ChessConfig(filename);
            alertObservers("Puzzle reset!");
        } catch(IOException ex) {
            alertObservers("Reset error with " + filename);
        }
    }

    public ArrayList<Position> getPieces() {
        return currentConfig.getPieces();
    }

    public int getRows() {
        return currentConfig.getRows();
    }

    public int getCols() {
        return currentConfig.getCols();
    }

    public char getCellPiece(int row, int col) {
        return currentConfig.getCellPiece(row, col);
    }

    public String getFilename() {
        return filename;
    }

    public String toString() {
        // TODO chess borders
        return currentConfig.toString();
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

    public ChessConfig getCurrentConfig() {
        return currentConfig;
    }

    public ChessModel(String filename) throws IOException {
        this.filename = filename;
        this.currentConfig = new ChessConfig(filename);
//        alertObservers("Loaded: " + filename);
    }
}
