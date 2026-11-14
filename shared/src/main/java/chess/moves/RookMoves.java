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
    /**
     * A moves helper class containing the moves a Rook can make
     * @param board:      the current chessboard
     * @param myPosition: the current Rook's position
     * @param piece:      the Rook you are moving, including the piece's color
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition,
                                                   ChessPiece piece) throws InvalidArgumentsException {
        // Check Parameters
        if(board == null || piece == null || myPosition == null) {
            throw new InvalidArgumentsException("pieceMoves in RookMoves");
        }
        if(!myPosition.inBounds()) {
            throw new InvalidArgumentsException("pieceMoves in RookMoves");
        }

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