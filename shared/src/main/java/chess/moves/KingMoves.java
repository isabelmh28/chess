/**
 * KingMoves Module
 * Calculates the available moves for a King
 * Author: Isabel Hinton
 */

package chess.moves;

import chess.*;

import java.util.Collection;
import java.util.ArrayList;

public class KingMoves {
    /**
     * A moves helper class containing the moves a King can make
     * @param board:      the current chessboard
     * @param myPosition: the current King's position
     * @param piece:      the King you are moving, including the piece's color
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> kingMoves = new ArrayList<>();

        // Check Parameters
        if(board == null || piece == null || myPosition == null) { return kingMoves;}
        if(!myPosition.inBounds()) { return kingMoves;}

        // Add moves to Collection per direction
        for(int[] currDirection : ChessPiece.ALL_DIRECTIONS) {
            // See moveRay method in ChessPiece module for more information
            //    - Adds all available moves in a given direction
            //    - the boolean "cont" being false means the King can only move once in any given direction; it cannot
            //      move in a ray like the Queen.
            kingMoves.addAll(piece.rayMoves(board, myPosition, currDirection[0], currDirection[1], false));
        }
        return kingMoves;
    }
}