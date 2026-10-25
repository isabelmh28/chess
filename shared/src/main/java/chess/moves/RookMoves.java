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
        // Add moves to Collection per direction
        for(int[] currDirection : ChessPiece.STRAIGHTS_DIRECTIONS) {
            // See moveRay method in ChessPiece module for more information
            //    - Adds all available moves in a given direction
            rookMoves.addAll(piece.rayMoves(board, myPosition, currDirection[0], currDirection[1], true));
        }
        return rookMoves;
    }
}
