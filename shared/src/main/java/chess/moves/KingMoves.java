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
        // The King can move in the following directions:
        int[][] directions = {
                // Straights
                {-1, 0},  // Down
                {1, 0},   // Up
                {0, -1},  // Left
                {0, 1},   // Right
                // Diagonals
                {-1, -1}, // Down Left
                {1, -1},  // Up Left
                {1, 1},   // Up Right
                {-1, 1}   // Down Right
        };
        // Add moves to Collection per direction
        for(int[] currDirection : directions) {
            // See moveRay method in ChessPiece module for more information
            //    - Adds all available moves in a given direction
            //    - the boolean "cont" being false means the King can only move once in any given direction; it cannot
            //      move in a ray like the Queen.
            kingMoves.addAll(piece.rayMoves(board, myPosition, currDirection[0], currDirection[1], false));
        }
        return kingMoves;
    }
}
