/**
 * BishopMoves Module
 * Calculates the available moves for a Bishop
 * Author: Isabel Hinton
 */

package chess;

import java.util.Collection;
import java.util.ArrayList;

public class BishopMoves {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        Collection<ChessMove> bishopMoves = new ArrayList<>();
        int[][] directions = {
                {-1, -1}, // Down Left
                {-1, 1},  // Up Left
                {1, 1},   // Up Right
                {1, -1}   // Down Right
        };
        // Add moves to Collection per direction
        for(int[] currDirection : directions) {
            bishopMoves.addAll(ChessPiece.moveRay(board, myPosition, myColor, currDirection[0], currDirection[1]));
        }
        return bishopMoves;
    }
}
