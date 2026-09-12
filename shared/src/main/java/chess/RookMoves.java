/**
 * RookMoves Module
 * Calculates the available moves for a Rook
 * Author: Isabel Hinton
 */

package chess;

import java.util.Collection;
import java.util.ArrayList;

public class RookMoves {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        Collection<ChessMove> rookMoves = new ArrayList<>();
        int[][] directions = {
                {0, -1}, // Down
                {0, 1},  // Up
                {-1, 0}, // Left
                {1, 0}   // Right
        };
        // Add moves to Collection per direction
        for(int[] currDirection : directions) {
            rookMoves.addAll(ChessPiece.moveRay(board, myPosition, myColor, currDirection[0], currDirection[1]));
        }
        return rookMoves;
    }
}
