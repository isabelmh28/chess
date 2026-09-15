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
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        Collection<ChessMove> rookMoves = new ArrayList<>();
        // The Rook can only move in the following directions:
        int[][] directions = {
                {0, -1}, // Down
                {0, 1},  // Up
                {-1, 0}, // Left
                {1, 0}   // Right
        };
        // Add moves to Collection per direction
        for(int[] currDirection : directions) {
            // See moveRay method in ChessPiece module for more information
            //    - Adds all available moves in a given direction
            rookMoves.addAll(ChessPiece.moveRay(board, myPosition, myColor, currDirection[0], currDirection[1]));
        }
        return rookMoves;
    }
}
