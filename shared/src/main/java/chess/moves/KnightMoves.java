/**
 * KnightMoves Module
 * Calculates the available moves for a Knight
 * Author: Isabel Hinton
 */

package chess.moves;

import chess.*;

import java.util.Collection;
import java.util.ArrayList;

public class KnightMoves {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> queenMoves = new ArrayList<>();
        // The Knight can move in the following combinations:
        int[][] directions = {
                // Horizontal 1, Vertical 2
                {-2, -1},  // Left 1 Down 2
                {2, -1},   // Left 1 Up 2
                {2, 1},    // Right 1 Up 2
                {-2, 1},   // Right 1 Down 2
                // Horizontal 2, Vertical 1
                {-1, -2},  // Left 2 Down 1
                {1, -2},   // Left 2 Up 1
                {1, 2},    // Right 2 Up 1
                {-1, 2},   // Right 2 Down 1
        };
        // Add moves to Collection per direction
        for(int[] currDirection : directions) {
            // See moveRay method in ChessPiece module for more information
            //    - Adds all available moves in a given direction
            queenMoves.addAll(piece.rayMoves(board, myPosition, currDirection[0], currDirection[1], false));
        }
        return queenMoves;
    }
}
