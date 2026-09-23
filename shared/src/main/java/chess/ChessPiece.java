/**
 * ChessPiece Module
 * Represents a chess piece including color and type
 * Author: Isabel Hinton off Template
 */

package chess;

import chess.moves.*;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    // Private Members
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType pieceType;
    // Constructor
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    // Overridden Methods
    @Override
    public String toString() {
        return "ChessPiece{" +
                pieceColor + ", " +
                pieceType +
                '}';
    }
    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(pieceColor) + 51 * Objects.hashCode(pieceType);
    }
    @Override
    public boolean equals(Object obj) {
        // Check reference equality
        if(this == obj) return true;
        // Check null and see if Classes match
        if((obj == null) || (getClass() != obj.getClass())) return false;
        // Cast and Compare data
        ChessPiece that = (ChessPiece) obj;
        return (Objects.equals(pieceColor, that.pieceColor) && Objects.equals(pieceType, that.pieceType));
    }

    // Getter Functions
    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() { return pieceColor;}

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() { return pieceType;}

    // Chess Piece Moves
    /**
     * Ray Movement: given a direction with col/row delta, move until:
     *    a) you hit your own team's piece (you stop right next to it)
     *    b) you hit the end of the board (you stop right next to the edge)
     *    c) you hit an enemy piece (you replace the enemy piece)
     * Parameters: hor and vert can be -1, 0, or 1. This provides the direction
     *    ex: (1, 0) would be right, (-1, 1) would be forward left diagonal
     * Used for Rooks, Bishops, and Queens
     */
    public Collection<ChessMove> rayMoves(ChessBoard board, ChessPosition pos, int rowDelta, int colDelta, boolean cont) {
        Collection<ChessMove> ray = new ArrayList<>();
        int currRow = pos.getRow();
        int currCol = pos.getColumn();
        while(true) {
            currRow += rowDelta;
            currCol += colDelta;
            ChessPosition newPos = new ChessPosition(currRow, currCol);
            if(newPos.inBounds()) {
                if(board.getPiece(newPos) == null) {
                    ray.add(new ChessMove(pos, newPos, null));
                }
                else {
                    if(board.getPiece(newPos).getTeamColor() != pieceColor) {
                        ray.add(new ChessMove(pos, newPos, null));
                    }
                    break;
                }
            }
            else{ break;}
            if(!cont) { break;}
        }
        return ray;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     * See moves directory for individual pieceMoves methods
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (pieceType) {
            case KING   -> KingMoves.pieceMoves(board,   myPosition, this);
            case QUEEN  -> QueenMoves.pieceMoves(board,  myPosition, this);
            case KNIGHT -> KnightMoves.pieceMoves(board, myPosition, this);
            case BISHOP -> BishopMoves.pieceMoves(board, myPosition, this);
            case ROOK   -> RookMoves.pieceMoves(board,   myPosition, this);
            case PAWN   -> PawnMoves.pieceMoves(board,   myPosition, this);
        };
    }
}
