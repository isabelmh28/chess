/**
 * QueenMoves Module
 * Calculates the available moves for a Queen
 * Author: Isabel Hinton
 */

package chess.moves;

import chess.*;

import java.util.Collection;
import java.util.ArrayList;

public class QueenMoves {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> queenMoves = new ArrayList<>();
        // The Queen can move in the following directions:
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
            queenMoves.addAll(piece.rayMoves(board, myPosition, currDirection[0], currDirection[1], true));
        }
        return queenMoves;
    }
}
