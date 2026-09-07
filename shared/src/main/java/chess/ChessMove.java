/**
 * ChessMove Module
 * Represents the chess piece's move, including start/end position.
 * Author: Isabel Hinton off Template
 */

package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition start;
    private final ChessPosition end;
    private final ChessPiece.PieceType promoteType;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        start = startPosition;
        end = endPosition;
        promoteType = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() { return start;}

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() { return end;}

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() { return promoteType;}

    @Override
    public int hashCode() {
        return Objects.hash(start, end, promoteType);
    }

    @Override
    public boolean equals(Object obj) {
        // Check reference equality
        if(this == obj) return true;
        // Check null and see if Classes match
        if((obj == null) || (getClass() != obj.getClass())) return false;
        // Cast and Compare data
        ChessMove that = (ChessMove) obj;
        return ((start == that.start) && (end == that.end) && (promoteType == that.promoteType));
    }
}
