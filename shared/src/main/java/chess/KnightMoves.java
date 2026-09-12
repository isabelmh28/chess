/**
 * KnightMoves Module
 * Calculates the available moves for a Knight
 * Author: Isabel Hinton
 */

package chess;

import java.util.Collection;
import java.util.ArrayList;

public class KnightMoves {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        Collection<ChessMove> knightMoves = new ArrayList<>();
        int[][] directions = {
                // Horizontal 1, Vertical 2
                {-1, -2},  // Left 1 Down 2
                {-1, 2},   // Left 1 Up 2
                {1, 2},    // Right 1 Up 2
                {1, -2},   // Right 1 Down 2
                // Horizontal 2, Vertical 1
                {-2, -1},  // Left 2 Down 1
                {-2, 1},   // Left 2 Up 1
                {2, 1},    // Right 2 Up 1
                {2, -1},   // Right 2 Down 1
        };

        for(int[] currDirection : directions) {
            int currRow = myPosition.getRow();
            int currCol = myPosition.getColumn();
            // Move to the next position
            currRow += currDirection[0];
            currCol += currDirection[1];
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
            knightMoves.add(new ChessMove(myPosition, nextPosition, null));
        }
        return knightMoves;
    }
}