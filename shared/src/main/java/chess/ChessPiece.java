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
    // Direction Macros for Chess Piece moving
    public static final int[][] STRAIGHTS_DIRECTIONS = {
            {-1, 0}, // Down
            {1, 0},  // Up
            {0, -1}, // Left
            {0, 1}   // Right
    };
    public static final int[][] DIAGONALS_DIRECTIONS = {
            {-1, 1},  // Down Right
            {1, 1},   // Up Right
            {-1, -1}, // Down Left
            {1, -1}   // Up Left
    };
    public static final int[][] ALL_DIRECTIONS = {
            {-1, 0}, // Down
            {1, 0},  // Up
            {0, -1}, // Left
            {0, 1},  // Right
            {-1, 1},  // Down Right
            {1, 1},   // Up Right
            {-1, -1}, // Down Left
            {1, -1}   // Up Left
    };

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
        if(this == obj) {return true;}
        // Check null and see if Classes match
        if((obj == null) || (getClass() != obj.getClass())) {return false;}
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
     * @param board:    the chessBoard
     * @param pos:      starting position on the board
     * @param rowDelta: indicates the ray direction horizontally
     * @param colDelta: indicates the ray direction vertically
     *                  EX: rowDelta = 1, colDelta = 0 would be right
     *                      rowDelta = -1, colDelta = 1 would be up left diagonal
     * Used for Rooks, Bishops, Queens, Kings, and Knights
     */
    public Collection<ChessMove> rayMoves(ChessBoard board, ChessPosition pos, int rowDelta, int colDelta,
                                          boolean cont) throws InvalidArgumentsException {
        // Check Parameters
        if((board == null) || (pos == null) || !pos.inBounds()) {
            throw new InvalidArgumentsException("rayMoves in ChessPiece");
        }
        
        Collection<ChessMove> ray = new ArrayList<>();

        int currRow = pos.getRow();
        int currCol = pos.getColumn();
        while(true) {
            currRow += rowDelta;
            currCol += colDelta;
            ChessPosition newPos = new ChessPosition(currRow, currCol);
            if(newPos.inBounds()) {
                // if new position doesn't have a piece, add a move
                if(board.getPiece(newPos) == null) {
                    ray.add(new ChessMove(pos, newPos, null));
                }
                else { // if the new position has an enemy piece, add a move and stop
                    if(board.getPiece(newPos).getTeamColor() != pieceColor) {
                        ray.add(new ChessMove(pos, newPos, null));
                    }
                    break;
                }
            }
            else { break;}
            if(!cont) { break;}
        }
        return ray;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     * NOTE: parameter checks occur within each pieceMoves class
     * See moves directory for individual pieceMoves methods
     * @param board:      the chessBoard
     * @param myPosition: position of the piece on the board
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
