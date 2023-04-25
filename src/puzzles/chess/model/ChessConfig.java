package puzzles.chess.model;

import javafx.geometry.Pos;
import puzzles.chess.solver.Chess;
import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

// TODO: implement your ChessConfig for the common solver

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
    private int row;
    private int col;
    private int numPieces;
    private ArrayList<Position> pieces;

    public ChessConfig(String filename) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String line = in.readLine();
            String[] field = line.split("\\s+");
            this.ROWS = Integer.parseInt(field[0]);
            this.COLS = Integer.parseInt(field[1]);

            this.board = new char[ROWS][COLS];
            pieces = new ArrayList<>();
            this.numPieces = 0;
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
            }
            this.row = 0;
            this.col = -1;
        } catch (IOException e) {
            System.err.println("IOException");
        }
    }

    public ChessConfig(ChessConfig other, int startRow,
                       int startCol, int endRow, int endCol) {
        board = new char[ROWS][COLS];
        this.row = other.row;
        this.col = other.col;
        this.pieces = new ArrayList<>();
//        this.numPieces = other.numPieces - 1; // captured
        for (int i = 0; i < ROWS; i++) {
            System.arraycopy(other.board[i], 0, this.board[i], 0, ROWS);
        }
        // piece replaces captured piece's position
        board[endRow][endCol] = board[startRow][startCol];
        // original position of piece is empty
        board[startRow][startCol] = EMPTY;
//        for (int i = 0; i < this.pieces.size(); i++) {
//            if ((this.pieces.get(i).getRow() == startRow)
//                    && (this.pieces.get(i).getCol() == startCol)) {
//                this.pieces.remove(i);
//            }
//            if ((this.pieces.get(i).getRow() == endRow)
//                    && (this.pieces.get(i).getCol() == endCol)) {
//                this.pieces.get(i).setPiece(board[endRow][endCol]);
//            }
//        }
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
        int r = row;
        int c = col + 1;
//        if ((row == ROWS - 1) && (this.col == COLS - 1)) {
//            return successors;
        // Win condition: only one piece on the board
        if (pieces.size() == 1) {
            return successors;
        } else {
//            for (Position p : this.pieces) {
            for (int i = 0; i < pieces.size(); i++) {
                Position p = pieces.get(i);
                // TODO COncurrentModificationException
                if (p.getPiece() == PAWN) {
                    successors.addAll(pawnMoves(p));
                }
                if (p.getPiece() == BISHOP) {
                    // For bishop top left, row and column decrement by 1
                    // (-1, -1), (-2, -2), (-3, -3)
                    // Going top right, -1 +1
                    // Going bottom left, +1 -1
                    // Going bottom right, +1 +1
                    successors.addAll(bishopMoves(p));
                }
                if (p.getPiece() == KING) {
                    successors.addAll(kingMoves(p));
                }
            }
        }
        return null;
    }

    public Collection<Configuration> pawnMoves(Position p) {
        // Only generating captures, not moves.
        // Pawns capture diagonally
        ArrayList<Configuration> moves = new ArrayList<>();
        int r = p.getRow() - 1;
        int cLeft = p.getCol() - 1;
        int cRight = p.getCol() + 1;
        if (isValidPos(r, cLeft)) {
            if (!(board[r][cLeft] == EMPTY) && !(board[r][cLeft] == KING)) {
                ChessConfig child1 =
                        new ChessConfig(this, p.getRow(),
                                p.getCol(), r, cLeft);
                moves.add(child1);
            }
        }
        if (isValidPos(r, cRight)) {
            if (!(board[r][cRight] == EMPTY) && !(board[r][cRight] == KING)) {
                ChessConfig child2 =
                        new ChessConfig(this, p.getRow(), p.getCol(), r, cRight);
                moves.add(child2);
            }
        }
        return moves;
    }

    public Collection<Configuration> bishopMoves(Position p) {
        // For bishop top left, row and column decrement by 1
        // (-1, -1), (-2, -2), (-3, -3)
        // Going top right, -1 +1
        // Going bottom left, +1 -1
        // Going bottom right, +1 +1
        // TODO should it return as soon as a config is made?
        ArrayList<Configuration> moves = new ArrayList<>();

        // TODO is it a good idea to have it less than ROWS?
        for (int i = 1; i < ROWS; i++) {
            int topRow = p.getRow() - i;
            int leftCol = p.getCol() - i;
            int bottRow = p.getRow() + i;
            int rightCol = p.getCol() + i;

            // Did we already make a capture in this direction?
            boolean topLeftCapture = false;
            boolean topRightCapture = false;
            boolean bottLeftCapture = false;
            boolean bottRightCapture = false;
            if (!(topLeftCapture) && isValidPos(topRow, leftCol)) {
                if (!(board[topRow][leftCol] == EMPTY)
                        && !((board[topRow][leftCol] == KING))) {
                    ChessConfig child = new ChessConfig(this, p.getRow(),
                            p.getCol(), topRow, leftCol);
                    moves.add(child);
                    topLeftCapture = true;
                }
            }
            // going top right
            if (!topRightCapture && isValidPos(topRow, rightCol)) {
                if (!(board[topRow][rightCol] == EMPTY)
                        && !((board[topRow][rightCol] == KING))) {
                    ChessConfig child = new ChessConfig(this, p.getRow(),
                            p.getCol(), topRow, rightCol);
                    moves.add(child);
                    topRightCapture = true;
                }
            }
            // going down left
            if (!bottLeftCapture && isValidPos(bottRow, leftCol)) {
                if (!(board[bottRow][leftCol] == EMPTY)
                        && !((board[bottRow][leftCol] == KING))) {
                    ChessConfig child = new ChessConfig(this, p.getRow(),
                            p.getCol(), bottRow, leftCol);
                    moves.add(child);
                    bottLeftCapture = true;
                }
            }
            // going down right
            if (!bottRightCapture && isValidPos(bottRow, rightCol)) {
                if (!(board[bottRow][rightCol] == EMPTY)
                        && !(board[bottRow][rightCol] == KING)) {
                    ChessConfig child = new ChessConfig(this, p.getRow(),
                            p.getCol(), bottRow, rightCol);
                    moves.add(child);
                    bottRightCapture = true;
                }
            }
        }
        return moves;
    }

    public Collection<Configuration> kingMoves(Position p) {
        ArrayList<Configuration> moves = new ArrayList<>();

        int topRow = p.getRow() - 1;
        int bottRow = p.getRow() + 1;
        int leftCol = p.getCol() - 1;
        int rightCol = p.getCol() + 1;

        //        for (int i = 1; i < ROWS; i++) {
            // Top left
        if (isValidPos(topRow, leftCol)) {
            if ((board[topRow][leftCol] != EMPTY)
                    && (board[topRow][leftCol] != KING)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), topRow, leftCol);
                moves.add(child);
            }
        }
        // Top
        if (isValidPos(topRow, p.getCol())) {
            if ((board[topRow][p.getCol()] != EMPTY)
                    && (board[topRow][p.getCol()] != KING)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), topRow, p.getCol());
                moves.add(child);
            }
        }

        // Top right
        if (isValidPos(topRow, rightCol)) {
            if ((board[topRow][rightCol] != EMPTY)
                    && (board[topRow][rightCol] != KING)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), topRow, rightCol);
                moves.add(child);
            }
        }

        // Left
        if (isValidPos(p.getRow(), leftCol)) {
            if ((board[p.getRow()][leftCol] != EMPTY)
                    && (board[p.getRow()][leftCol] != KING)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), p.getRow(), leftCol);
                moves.add(child);
            }
        }

        // Right
        if (isValidPos(p.getRow(), rightCol)) {
            if ((board[p.getRow()][rightCol] != EMPTY)
                    && (board[p.getRow()][rightCol] != KING)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), p.getRow(), rightCol);
                moves.add(child);
            }
        }

        // Down left
        if (isValidPos(bottRow, leftCol)) {
            if ((board[bottRow][leftCol] != EMPTY)
                    && (board[bottRow][leftCol] != KING)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), bottRow, leftCol);
                moves.add(child);
            }
        }

        // Down
        if (isValidPos(bottRow, p.getCol())) {
            if ((board[bottRow][p.getCol()] != EMPTY)
                    && (board[bottRow][p.getCol()] != KING)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), bottRow, p.getCol());
                moves.add(child);
            }
        }

        // Down right
        if (isValidPos(bottRow, rightCol)) {
            if ((board[bottRow][rightCol] != EMPTY)
                    && (board[bottRow][rightCol] != KING)) {
                ChessConfig child = new ChessConfig(this, p.getRow(),
                        p.getCol(), bottRow, rightCol);
                moves.add(child);
            }
        }
        return moves;
    }

    public boolean isValidPos(int row, int col) {
        if (col >= 0 && col < COLS && row >= 0 && row < ROWS) {
            return true;
        } else {
            return false;
        }
    }
}