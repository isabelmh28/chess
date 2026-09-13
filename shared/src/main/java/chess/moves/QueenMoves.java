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
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        Collection<ChessMove> queenMoves = new ArrayList<>();
        int[][] directions = {
                // Straights
                {0, -1},  // Down
                {0, 1},   // Up
                {-1, 0},  // Left
                {1, 0},   // Right
                // Diagonals
                {-1, -1}, // Down Left
                {-1, 1},  // Up Left
                {1, 1},   // Up Right
                {1, -1}   // Down Right
        };
        // Add moves to Collection per direction
        for(int[] currDirection : directions) {
            queenMoves.addAll(ChessPiece.moveRay(board, myPosition, myColor, currDirection[0], currDirection[1]));
        }
        return queenMoves;
    }
}
