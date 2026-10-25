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
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> kingMoves = new ArrayList<>();
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
