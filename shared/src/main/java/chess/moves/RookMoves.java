/**
 * RookMoves Module
 * Calculates the available moves for a Rook
 * Author: Isabel Hinton
 */

package chess.moves;

import chess.*;

import java.util.Collection;
import java.util.ArrayList;

public class RookMoves {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> rookMoves = new ArrayList<>();
        // The Rook can only move in the following directions:
        int[][] directions = {
                {-1, 0}, // Down
                {1, 0},  // Up
                {0, -1}, // Left
                {0, 1}   // Right
        };
        // Add moves to Collection per direction
        for(int[] currDirection : directions) {
            // See moveRay method in ChessPiece module for more information
            //    - Adds all available moves in a given direction
            rookMoves.addAll(piece.rayMoves(board, myPosition, currDirection[0], currDirection[1], true));
        }
        return rookMoves;
    }
}
