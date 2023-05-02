package puzzles.chess.model;

import javafx.geometry.Pos;
import puzzles.chess.solver.Chess;
import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * The configuration class of the Chess puzzle
 */
public class ChessConfig implements Configuration {
    public static char BISHOP = 'B';
    public static char KING = 'K';
    public static char KNIGHT = 'N';
    public static char PAWN = 'P';
    public static char QUEEN = 'Q';
    public static char ROOK = 'R';
    public static char EMPTY = '.';
    private static int ROWS;
    private static int COLS;
    private char[][] board;
    private ArrayList<Configuration> successors;
    private int numPieces;
    private ArrayList<Position> pieces;

    /**
     * Create a new ChessConfig using the given filename, starting config
     *
     * @param filename file to read and create the ChessConfig from
     * @throws IOException
     */
    public ChessConfig(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line = in.readLine();
        String[] field = line.split("\\s+");
        this.ROWS = Integer.parseInt(field[0]);
        this.COLS = Integer.parseInt(field[1]);

        this.board = new char[ROWS][COLS];
        pieces = new ArrayList<>();
        for (int r = 0; r < ROWS; r++) {
            line = in.readLine();
            field = line.split("\\s+");
            for (int c = 0; c < COLS; c++) {
                if (field[c].charAt(0) == BISHOP) {
                    board[r][c] = BISHOP;
                    pieces.add(new Position(r, c, BISHOP));
                } else if (field[c].charAt(0) == KING) {
                    board[r][c] = KING;
                    pieces.add(new Position(r, c, KING));
                } else if (field[c].charAt(0) == KNIGHT) {
                    board[r][c] = KNIGHT;
                    pieces.add(new Position(r, c, KNIGHT));
                } else if (field[c].charAt(0) == PAWN) {
                    board[r][c] = PAWN;
                    pieces.add(new Position(r, c, PAWN));
                } else if (field[c].charAt(0) == QUEEN) {
                    board[r][c] = QUEEN;
                    pieces.add(new Position(r, c, QUEEN));
                } else if (field[c].charAt(0) == ROOK) {
                    board[r][c] = ROOK;
                    pieces.add(new Position(r, c, ROOK));
                } else if (field[c].charAt(0) == EMPTY) {
                    board[r][c] = EMPTY;
                }
            }
            this.numPieces = pieces.size();
        }
    }

    /**
     * The ChessConfig constructor to create the configs of the neighbors
     * of a ChessConfig
     * Generated when a piece is captured
     *
     * @param other other ChessConfig (parent)
     * @param startRow starting row of the current chess piece
     * @param startCol starting column of the current chess piece
     * @param endRow row of the piece being captured,
     *               end row of the current piece
     * @param endCol column of the piece being captured,
     *               end column of the current piece
     */
    public ChessConfig(ChessConfig other, int startRow,
                       int startCol, int endRow, int endCol) {
        board = new char[ROWS][COLS];
        this.pieces = new ArrayList<>();
        for (int i = 0; i < ROWS; i++) {
            System.arraycopy(other.board[i], 0, this.board[i], 0, COLS);
        }
        // piece replaces captured piece's position
        board[endRow][endCol] = board[startRow][startCol];
        // original position of piece is empty
        board[startRow][startCol] = EMPTY;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (this.board[i][j] != EMPTY) {
                    pieces.add(new Position(i, j, this.board[i][j]));
                }
            }
        }
        this.numPieces = this.pieces.size();

    }

    @Override
    public boolean isSolution() {
        if (numPieces == 1) {
            return true;
        }
        return false;
    }

    @Override
    public Collection<Configuration> getNeighbors() {
        successors = new ArrayList<>();
        // Win condition: only one piece on the board
            for (int i = 0; i < pieces.size(); i++) {
                Position p = pieces.get(i);
                if (p.getPiece() == PAWN) {
                    successors.addAll(pawnMoves(p));
                }
                if (p.getPiece() == BISHOP) {
                    successors.addAll(bishopMoves(p));
                }
                if (p.getPiece() == KING) {
                    successors.addAll(kingMoves(p));
                }
                if (p.getPiece() == KNIGHT) {
                    successors.addAll(knightMoves(p));
                }
                if (p.getPiece() == ROOK) {
                    successors.addAll(rookMoves(p));
                }

                if (p.getPiece() == QUEEN) {
                    successors.addAll(queenMoves(p));
                }
            }
        return successors;
    }

    /**
     * Create configurations for a selected pawn piece
     * Pawns capture diagonally forward left or right
     *
     * @param p current position of the pawn
     * @return the configurations of the possible moves for the pawn
     */
    public Collection<Configuration> pawnMoves(Position p) {
        // Only generating captures, not moves.
        // Pawns capture diagonally
        ArrayList<Configuration> moves = new ArrayList<>();
        int r = p.getRow() - 1;
        int cLeft = p.getCol() - 1;
        int cRight = p.getCol() + 1;
        if (isValidPos(r, cLeft)) {
            if (!(board[r][cLeft] == EMPTY)) {
                ChessConfig child1 =
                        new ChessConfig(this, p.getRow(),
                                p.getCol(), r, cLeft);
                moves.add(child1);
            }
        }
        if (isValidPos(r, cRight)) {
            if (!(board[r][cRight] == EMPTY)) {
                ChessConfig child2 =
                        new ChessConfig(this, p.getRow(),
                                p.getCol(), r, cRight);
                moves.add(child2);
            }
        }
        return moves;
    }

    /**
     * Configurations for the selected bishop
     * Bishops capture diagonally
     *
     * @param p position of the current bishop
     * @return list of the configs for the moves the bishop can make
     */
    public Collection<Configuration> bishopMoves(Position p) {
        // For bishop top left, row and column decrement by 1
        // (-1, -1), (-2, -2), (-3, -3)
        // Going top right, -1 +1
        // Going bottom left, +1 -1
        // Going bottom right, +1 +1
        ArrayList<Configuration> moves = new ArrayList<>();

        // Did we already make a capture in this direction?
        boolean topLeftCapture = false;
        boolean topRightCapture = false;
        boolean bottLeftCapture = false;
        boolean bottRightCapture = false;

        for (int i = 1; i < ROWS; i++) {
            int upRow = p.getRow() - i;
            int leftCol = p.getCol() - i;
            int downRow = p.getRow() + i;
            int rightCol = p.getCol() + i;

            if (!(topLeftCapture) && isValidPos(upRow, leftCol)
                    && isCapture(upRow, leftCol)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), upRow, leftCol);
                moves.add(child);
                topLeftCapture = true;
            }
            // going top right
            if (!topRightCapture && isValidPos(upRow, rightCol)
                    && isCapture(upRow, rightCol)) {
                    ChessConfig child = new ChessConfig(this, p.getRow(),
                            p.getCol(), upRow, rightCol);
                    moves.add(child);
                    topRightCapture = true;
            }

            // going down left
            if (!bottLeftCapture && isValidPos(downRow, leftCol)
                    && isCapture(downRow, leftCol)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), downRow, leftCol);
                moves.add(child);
                bottLeftCapture = true;
            }

            // going down right
            if (!bottRightCapture && isValidPos(downRow, rightCol)
                    && isCapture(downRow, rightCol)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), downRow, rightCol);
                moves.add(child);
                bottRightCapture = true;
            }
        }
        return moves;
    }

    /**
     * Configurations for the selected king
     * Kings can capture in any direction but only move one square
     *
     * @param p position of the king piece
     * @return list of the configs for the moves the king can make
     */
    public Collection<Configuration> kingMoves(Position p) {
        ArrayList<Configuration> moves = new ArrayList<>();

        int topRow = p.getRow() - 1;
        int bottRow = p.getRow() + 1;
        int leftCol = p.getCol() - 1;
        int rightCol = p.getCol() + 1;

            // Top left
        if (isValidPos(topRow, leftCol)) {
            if ((board[topRow][leftCol] != EMPTY)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), topRow, leftCol);
                moves.add(child);
            }
        }
        // Top
        if (isValidPos(topRow, p.getCol())) {
            if ((board[topRow][p.getCol()] != EMPTY)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), topRow, p.getCol());
                moves.add(child);
            }
        }

        // Top right
        if (isValidPos(topRow, rightCol)) {
            if ((board[topRow][rightCol] != EMPTY)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), topRow, rightCol);
                moves.add(child);
            }
        }

        // Left
        if (isValidPos(p.getRow(), leftCol)) {
            if ((board[p.getRow()][leftCol] != EMPTY)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), p.getRow(), leftCol);
                moves.add(child);
            }
        }

        // Right
        if (isValidPos(p.getRow(), rightCol)) {
            if ((board[p.getRow()][rightCol] != EMPTY)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), p.getRow(), rightCol);
                moves.add(child);
            }
        }

        // Down left
        if (isValidPos(bottRow, leftCol)) {
            if ((board[bottRow][leftCol] != EMPTY)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), bottRow, leftCol);
                moves.add(child);
            }
        }

        // Down
        if (isValidPos(bottRow, p.getCol())) {
            if ((board[bottRow][p.getCol()] != EMPTY)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), bottRow, p.getCol());
                moves.add(child);
            }
        }

        // Down right
        if (isValidPos(bottRow, rightCol)) {
            if ((board[bottRow][rightCol] != EMPTY)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), bottRow, rightCol);
                moves.add(child);
            }
        }
        return moves;
    }

    /**
     * Configurations for the moves the selected knight can make
     * Knights capture in an L- or inverted L-shape
     *
     * @param p position of the knight piece
     * @return list of the configs for the moves the knight can make
     */
    public Collection<Configuration> knightMoves(Position p) {
        ArrayList<Configuration> moves = new ArrayList<>();
        int downTwo = p.getRow() + 2;
        int upTwo = p.getRow() - 2;
        int upOne = p.getRow() - 1;
        int downOne = p.getRow() + 1;
        int leftTwoCol = p.getCol() - 2;
        int rightTwoCol = p.getCol() + 2;
        int rightCol = p.getCol() + 1;
        int leftCol = p.getCol() - 1;

        // L shape, down two right one
        if (isValidPos(downTwo, rightCol)
                && isCapture(downTwo, rightCol)) {
            ChessConfig child = new ChessConfig(this, p.getRow(),
                    p.getCol(), downTwo, rightCol);
            moves.add(child);
        }
        // Down two left column
        if (isValidPos(downTwo, leftCol)
                && isCapture(downTwo, leftCol)) {
            ChessConfig child = new ChessConfig(this, p.getRow(),
                    p.getCol(), downTwo, leftCol);
            moves.add(child);
        }
        // Up two left column
        if (isValidPos(upTwo, leftCol) && isCapture(upTwo, leftCol)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), upTwo, leftCol);
                moves.add(child);
        }

        // Up two right column
        if (isValidPos(upTwo, rightCol)
                && isCapture(upTwo, rightCol)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), upTwo, rightCol);
                moves.add(child);
        }
        // Right two cols, Down one row (horizontal downwards L).
        if ((isValidPos(downOne, rightTwoCol))
                && (isCapture(downOne, rightTwoCol))) {
            ChessConfig child = new ChessConfig(this, p.getRow(),
                    p.getCol(), downOne, rightTwoCol);
            moves.add(child);
        }

        // Right two cols, up one row
        if ((isValidPos(upOne, rightTwoCol))
                && (isCapture(upOne, rightTwoCol))) {
            ChessConfig child = new ChessConfig(this, p.getRow(),
                    p.getCol(), upOne, rightTwoCol);
            moves.add(child);
        }

        // Left two cols, up one row
        if (isValidPos(upOne, leftTwoCol)
                && isCapture(upOne, leftTwoCol)) {
            ChessConfig child = new ChessConfig(this, p.getRow(),
                    p.getCol(), upOne, leftTwoCol);
            moves.add(child);
        }

        // Left two cols, down one row
        if (isValidPos(downOne, leftTwoCol)
                && isCapture(downOne, leftTwoCol)) {
            ChessConfig child = new ChessConfig(this, p.getRow(),
                    p.getCol(), downOne, leftTwoCol);
            moves.add(child);
        }

        // Up one, left two cols
        if (isValidPos(upOne, leftTwoCol)
                && isCapture(upOne, leftTwoCol)) {
            ChessConfig child = new ChessConfig(this, p.getRow(),
                    p.getCol(), upOne, leftTwoCol);
            moves.add(child);
        }
        return moves;
    }

    /**
     * Configurations for the moves the rook can make
     * Rooks can capture after moving horizontally or vertically
     * any number of empty squares
     *
     * @param p position of the rook piece
     * @return list of the configs for the moves the rook can make
     */
    public Collection<Configuration> rookMoves(Position p) {
        ArrayList<Configuration> moves = new ArrayList<>();
        // Did we already capture in this direction?
        boolean captureRight = false;
        boolean captureLeft = false;
        boolean captureUp = false;
        boolean captureDown = false;

        for (int i = 1; i < ROWS; i++) {
            int upRow = p.getRow() - i;
            int downRow = p.getRow() + i;

            if (!captureUp && isValidPos(upRow, p.getCol())
                    && isCapture(upRow, p.getCol())) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), upRow, p.getCol());
                moves.add(child);
                captureUp = true;
            }

            if (!captureDown && isValidPos(downRow, p.getCol())
                    && isCapture(downRow, p.getCol())) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), downRow, p.getCol());
                moves.add(child);
                captureDown = true;
            }
        }

        for (int i = 1; i < COLS; i++) {
            int leftCol = p.getCol() - i;
            int rightCol = p.getCol() + i;

            if (!captureLeft && isValidPos(p.getRow(), leftCol)
                    && isCapture(p.getRow(), leftCol)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), p.getRow(), leftCol);
                moves.add(child);
                captureLeft = true;
            }

            if (!captureRight && isValidPos(p.getRow(), rightCol)
                    && isCapture(p.getRow(), rightCol)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), p.getRow(), rightCol);
                moves.add(child);
                captureRight = true;
            }
        }
        return moves;
    }

    /**
     * Configurations for the moves the queen can make
     * Queens can capture after moving any number of vacant squares
     * horizontally, vertically, or diagonally.
     *
     * @param p position of the queen piece
     * @return list of the configs for the moves the queen can make
     */
    public Collection<Configuration> queenMoves(Position p) {
        ArrayList<Configuration> moves = new ArrayList<>();

        boolean captureTopLeft = false;
        boolean captureTopRight = false;
        boolean captureDownLeft = false;
        boolean captureDownRight = false;
        boolean captureRight = false;
        boolean captureLeft = false;
        boolean captureUp = false;
        boolean captureDown = false;

        for (int i = 1; i < ROWS; i++) {
            int upRow = p.getRow() - i;
            int downRow = p.getRow() + i;
            int leftCol = p.getCol() - i;
            int rightCol = p.getCol() + i;

            if (!captureUp && isValidPos(upRow, p.getCol())
                    && isCapture(upRow, p.getCol())) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), upRow, p.getCol());
                moves.add(child);
                captureUp = true;
            }

            if (!captureDown && isValidPos(downRow, p.getCol())
                    && isCapture(downRow, p.getCol())) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), downRow, p.getCol());
                moves.add(child);
                captureDown = true;
            }

            if (!captureTopLeft && isValidPos(upRow, leftCol) && isCapture(upRow, leftCol)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), upRow, leftCol);
                moves.add(child);
                captureTopLeft = true;
            }

            if (!captureTopRight && isValidPos(upRow, rightCol) && isCapture(upRow, rightCol)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), upRow, rightCol);
                moves.add(child);
                captureTopRight = true;
            }

            if (!captureTopLeft && isValidPos(upRow, leftCol) && isCapture(upRow, leftCol)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), upRow, leftCol);
                moves.add(child);
                captureTopLeft = true;
            }

            // going down right
            if (!captureDownRight && isValidPos(downRow, rightCol)
                    && isCapture(downRow, rightCol)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), downRow, rightCol);
                moves.add(child);
                captureDownRight = true;
            }

            // going down left
            if (!captureDownLeft && isValidPos(downRow, leftCol)
                    && isCapture(downRow, leftCol)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), downRow, leftCol);
                moves.add(child);
                captureDownLeft = true;
            }
        }

        for (int i = 1; i < COLS; i++){
            int leftCol = p.getCol() - i;
            int rightCol = p.getCol() + i;

            if (!captureLeft && isValidPos(p.getRow(), leftCol)
                    && isCapture(p.getRow(), leftCol)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), p.getRow(), leftCol);
                moves.add(child);
                captureLeft = true;
            }

            if (!captureRight && isValidPos(p.getRow(), rightCol)
                    && isCapture(p.getRow(), rightCol)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), p.getRow(), rightCol);
                moves.add(child);
                captureRight = true;
            }
        }

        return moves;
    }

    /**
     * Check if the cell exists on the board
     *
     * @param row row of the given cell
     * @param col column of the given cell
     * @return whether the cell exists on the board
     */
    public boolean isValidPos(int row, int col) {
        if (col >= 0 && col < COLS && row >= 0 && row < ROWS) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if the piece can capture a piece on the board
     * Cannot move if the cell is empty
     *
     * @param row row of the given cell
     * @param col column of the given cell
     * @return whether a piece can capture a cell
     */
    public boolean isCapture(int row, int col) {
        if ((board[row][col] != EMPTY)) {
            return true;
        }
        return false;
    }

    /**
     * Check if the current config equals another config
     * @param other the other config
     * @return true if the configs are equal, false if not.
     */
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof ChessConfig) {
            ChessConfig o = (ChessConfig) other;
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    if (this.board[i][j] == o.board[i][j]) {
                        result = true;
                    } else {
                        result = false;
                        return result;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Get the chess piece at a certain cell
     *
     * @param row row of the cell to check
     * @param col column of the cell to check
     * @return the chess piece of the cell
     */
    public char getCellPiece(int row, int col) {
        return board[row][col];
    }

    /**
     * Check if the cell is empty
     * @param row the row of the cell
     * @param col the column of the cell
     * @return whether the cell is empty
     */
    public boolean isEmpty(int row, int col) {
        if (board[row][col] == EMPTY) {
            return true;
        }
        return false;
    }

    /**
     * Get list of chess pieces
     *
     * @return list of pieces in the configuration
     */
    public ArrayList<Position> getPieces() {
        return pieces;
    }

    /**
     * Get the number of rows of the chessboard
     *
     * @return the number of rows of the chessboard
     */
    public int getRows() {
        return ROWS;
    }

    /**
     * Get the number of columns of the chessboard
     *
     * @return the number of columns of the chessboard
     */
    public int getCols() {
        return COLS;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ROWS; i++) {
            sb.append("\n");
            for (int j = 0; j < COLS; j++) {
                sb.append(board[i][j]);
                sb.append(" ");
            }
        }
        sb.append("\n");
        return sb.toString();
    }
}