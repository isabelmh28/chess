/**
 * ChessPosition Module
 * Represents the coordinates (row, col) of a piece.
 * Author: Isabel Hinton off Template
 */

package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() { return row;}

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() { return col;}

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public boolean equals(Object obj) {
        // Check reference equality
        if(this == obj) return true;
        // Check null and see if Classes match
        if((obj == null) || (getClass() != obj.getClass())) return false;
        // Cast and Compare data
        ChessPosition that = (ChessPosition) obj;
        return ((row == that.row) && (col == that.col));
    }
}
