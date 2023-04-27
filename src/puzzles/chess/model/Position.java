package puzzles.chess.model;

import javafx.geometry.Pos;
import puzzles.chess.model.ChessConfig;

/**
 * @author Victoria Rigoglioso (tori-riggs)
 */
public class Position implements Comparable<Position> {
    private int row;
    private int col;
    private char piece;
    public Position(int row, int col, char piece) {
        this.row = row;
        this.col = col;
        this.piece = piece;
    }

    public Position(String row, String col) {
        this.row = Integer.parseInt(row);
        this.col = Integer.parseInt(col);
    }

    public char getPiece() {
        return piece;
    }

    public void setPiece(char newPiece) {
        this.piece = newPiece;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object other) {
        // TODO should I also check for the same chess piece?
        if (other instanceof Position) {
            Position o = (Position) other;
            if ((this.getRow() == o.getRow()) && (this.col == o.col) && this.getPiece() == o.getPiece()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(Position other) {
        return this.getRow() - other.getRow();
    }
}
