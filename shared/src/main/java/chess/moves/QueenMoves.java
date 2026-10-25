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
        // Add moves to Collection per direction
        for(int[] currDirection : ChessPiece.ALL_DIRECTIONS) {
            // See moveRay method in ChessPiece module for more information
            //    - Adds all available moves in a given direction
            queenMoves.addAll(piece.rayMoves(board, myPosition, currDirection[0], currDirection[1], true));
        }
        return queenMoves;
    }
}
