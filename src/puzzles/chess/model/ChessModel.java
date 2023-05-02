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

    /** current cell selection */
    private Position currSelection;
    /** current filename */
    private String filename;

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<ChessModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * Load selected chess configuration file.
     * Save newly loaded file to temp config to make sure any IOExceptions
     * are caught without crashing the program.
     *
     * @param filename the name of the chess configuration
     */
    public void load(String filename) {
        try {
            ChessConfig temp = new ChessConfig(filename);
            currentConfig = temp;
            this.filename = filename;
            alertObservers("Loaded: " + filename);
        } catch(IOException e) {
            alertObservers("Failed to load: " + filename);
        }
    }

    /**
     * Give the next step of the solution for the config
     *
     * If there is no solution, alert observers that there is no solution
     * If the puzzle is already solved, alert observers that it is solved
     */
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

    /**
     * Select a cell in the chessboard
     * Empty cells cannot be selected
     *
     * If there is no cell already selected, select the chosen cell
     * and set isSelection to true.
     *
     * If there is a cell already selected, see if the piece at that cell
     * can be captured by the piece at the originally selected cell.
     *
     * @param row row of the selected cell
     * @param col column of the selected cell
     */
    public void select(int row, int col) {
        if (!isSelection) {
            if (currentConfig.isValidPos(row, col) &&
                    !currentConfig.isEmpty(row, col)) {
                currSelection = new Position(row, col,
                        currentConfig.getCellPiece(row, col));
                alertObservers("Selected " + currSelection.toString());
                isSelection = true;
            } else {
                alertObservers("Invalid selection (" + row + ", " + col + ")");
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

    /**
     * Reset the puzzle
     */
    public void reset() {
        try {
            currentConfig = new ChessConfig(filename);
            alertObservers("Puzzle reset!");
        } catch(IOException ex) {
            alertObservers("Reset error with " + filename);
        }
    }

    /**
     * Get the list of pieces from currentConfig
     * For the ChessModel
     *
     * @return the list of pieces of the current configuration
     */
    public ArrayList<Position> getPieces() {
        return currentConfig.getPieces();
    }

    /**
     * Get the row number of the current configuration
     * For the ChessModel, uses ChessConfig's getRows function
     *
     * @return the row number of the current configuration
     */
    public int getRows() {
        return currentConfig.getRows();
    }

    /**
     * Get the columns of the current configuration
     * For the ChessMode, uses ChessConfig's getCols function
     *
     * @return the column number of the current configuration
     */
    public int getCols() {
        return currentConfig.getCols();
    }

    /**
     * Get the chess piece at a certain cell
     * For the ChessModel, uses ChessConfig's getCellPiece function
     *
     * @param row the row of the piece
     * @param col the column of the piece
     * @return the piece at the specified cell.
     */
    public char getCellPiece(int row, int col) {
        return currentConfig.getCellPiece(row, col);
    }

    /**
     * Get the filename of the current configuration
     *
     * @return the filename of the current configuration
     */
    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n" + "   ");
        for (int i = 0; i < getCols(); i++) {
            sb.append(i + " ");
        }
        sb.append("\n" + "  ").append("-".repeat(2*getCols())).append("\n");
        for (int i = 0; i < getRows(); i++) {
            sb.append(i + "| ");
            for (int j = 0; j < getCols(); j++) {
                sb.append(getCellPiece(i, j));
                sb.append(" ");
                // If at last column in the row, append new line
                if (j == getCols() - 1) {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
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


    /**
     * Create a new ChessModel with the given filename
     *
     * @param filename the filename of the chess config to use
     * @throws IOException
     */
    public ChessModel(String filename) throws IOException {
        this.filename = filename;
        this.currentConfig = new ChessConfig(filename);
    }
}
