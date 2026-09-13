/**
 * ChessBoard Module
 * Represents the chessboard.
 * Functionality to add a piece, get a piece at a position, and reset board.
 * Author: Isabel Hinton off Template
 */

package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    public static final int ROWS = 8;
    public static final int COLS = 8;
    private ChessPiece[][] chessBoard;

    // Chess Board is 8x8
    public ChessBoard() {
        this.chessBoard = new ChessPiece[ROWS][COLS];
    }

    // We are using the "deep" methods found in java.util.Arrays
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(chessBoard);
    }

    @Override
    public boolean equals(Object obj) {
        // Check reference equality
        if(this == obj) return true;
        // Check null and see if Classes match
        if((obj == null) || (getClass() != obj.getClass())) return false;
        // Cast and Compare data
        ChessBoard that = (ChessBoard) obj;
        return Arrays.deepEquals(chessBoard, that.chessBoard);
    }

    // Getter Functions

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     * NOTE: The board is set up with rows/columns ordered 1-8.
     *       The array is 0-7. Therefore, the input rows/columns need 1 subtracted
     *       from them to get the correct row/column from the array.
     */
    public ChessPiece getPiece(ChessPosition position) {
        return chessBoard[position.getRow() - 1][position.getColumn() - 1];
    }

    // Board Functions

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        /*
         * Make sure position is valid
         * No null position
         * Position must be on the board: 1 < row < 8; 1 < col < 8
         */
        if(position == null) {throw new RuntimeException("Null position");}
        if((position.getRow() < 1) || (position.getRow() > ROWS)) {
            int bad_row = position.getRow() - 1;
            throw new RuntimeException(String.format("Row out of bounds: %d", bad_row));
        }
        if((position.getColumn() < 1) || (position.getColumn() > COLS)) {
            int bad_col = position.getColumn() - 1;
            throw new RuntimeException(String.format("Row out of bounds: %d", bad_col));
        }

        chessBoard[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Reset all spaces to null
        chessBoard = new ChessPiece[ROWS][COLS];
        // Set White Side
        // First Row
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        // Second Row (All Pawns)
        for(int col = 1; col <= COLS; col++) {
           addPiece(new ChessPosition(2, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }
        // Set Black Side
        // First Row
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        // Second Row (All Pawns)
        for(int col = 1; col <= COLS; col++) {
            addPiece(new ChessPosition(7, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }
}
