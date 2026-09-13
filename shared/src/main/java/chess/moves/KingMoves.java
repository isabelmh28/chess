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
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        Collection<ChessMove> kingMoves = new ArrayList<>();
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
        for(int[] currDirection : directions) {
            int currRow = myPosition.getRow();
            int currCol = myPosition.getColumn();
            // Move to the next position
            currRow += currDirection[1];
            currCol += currDirection[0];
            // Check if the destination would be out of bounds
            if((currRow < 1) || (currRow > 8) || (currCol < 1) || (currCol > 8)) {
                continue;
            }
            ChessPosition nextPosition = new ChessPosition(currRow, currCol);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            // See if the destination is on one of our own pieces
            if(nextPiece != null){
                if(nextPiece.getTeamColor() == myColor) {
                    continue;
                }
            }
            kingMoves.add(new ChessMove(myPosition, nextPosition, null));
        }
        return kingMoves;
    }
}
