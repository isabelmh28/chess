/**
 * PawnMoves Module
 * Calculates the available moves for a Pawn
 * Forward: if the pawn is in the starting position,
 *          it can move forward 1/2 if those spaces are empty
 *          If the pawn is at the other end of the board, promote
 * Diagonal: if there is an enemy piece in the immediate forward diagonal
 *           of the pawn, it can capture the enemy piece
 * Author: Isabel Hinton
 */

package chess.moves;

import chess.*;

import java.util.Collection;
import java.util.ArrayList;

public class PawnMoves {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        Collection<ChessMove> pawnMoves = new ArrayList<>();

        // Values determined by Team Color
        int direction = (myColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (myColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (myColor == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int[][] diagonals = {
                {-1, direction}, // Left Diagonal
                {1, direction}   // Right Diagonal
        };
        // Not dependent on Color
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        // Forward Moves
        int moveForward = currRow + direction;
        ChessPosition forwardPosition = new ChessPosition(moveForward, currCol);
        // If directly in front is open, you can move there
        if (board.getPiece(forwardPosition) == null) {
            // If you can be promoted, you can promote to a Rook, Bishop, Knight, or Queen
            if (moveForward == promotionRow) {
                pawnMoves.add(new ChessMove(myPosition, forwardPosition, ChessPiece.PieceType.ROOK));
                pawnMoves.add(new ChessMove(myPosition, forwardPosition, ChessPiece.PieceType.BISHOP));
                pawnMoves.add(new ChessMove(myPosition, forwardPosition, ChessPiece.PieceType.KNIGHT));
                pawnMoves.add(new ChessMove(myPosition, forwardPosition, ChessPiece.PieceType.QUEEN));
            } else {
                // If you are in the starting position, you can move forward 1 or 2
                if (currRow == startRow) {
                    int doubleForwardRow = moveForward + direction;
                    ChessPosition doublePosition = new ChessPosition(doubleForwardRow, currCol);
                    if (board.getPiece(doublePosition) == null) {
                        pawnMoves.add(new ChessMove(myPosition, doublePosition, null));
                    }
                }
                pawnMoves.add(new ChessMove(myPosition, forwardPosition, null));
            }
        }
        for (int[] currDiagonal : diagonals) {
           int captureRow = currRow + currDiagonal[1];
           int captureCol = currCol + currDiagonal[0];
           // Make sure coordinates are in bounds
           if(((captureRow >= 1) && (captureRow <= 8)) && ((captureCol >= 1) && (captureCol <= 8))){
               ChessPosition capturePosition = new ChessPosition(captureRow, captureCol);
               if(board.getPiece(capturePosition) != null) {
                   if(board.getPiece(capturePosition).getTeamColor() != myColor) {
                       // You can capture and promote
                       if(captureRow == promotionRow) {
                           pawnMoves.add(new ChessMove(myPosition, capturePosition, ChessPiece.PieceType.ROOK));
                           pawnMoves.add(new ChessMove(myPosition, capturePosition, ChessPiece.PieceType.BISHOP));
                           pawnMoves.add(new ChessMove(myPosition, capturePosition, ChessPiece.PieceType.KNIGHT));
                           pawnMoves.add(new ChessMove(myPosition, capturePosition, ChessPiece.PieceType.QUEEN));
                       }
                       else{
                           pawnMoves.add(new ChessMove(myPosition, capturePosition, null));
                       }
                   }
               }
           }
        }
        return pawnMoves;
    }
}
