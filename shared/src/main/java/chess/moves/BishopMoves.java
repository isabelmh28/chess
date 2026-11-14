/**
 * BishopMoves Module
 * Calculates the available moves for a Bishop
 * Author: Isabel Hinton
 */

package chess.moves;

import chess.*;

import java.util.Collection;
import java.util.ArrayList;

public class BishopMoves {
    /**
     * A moves helper class containing the moves a Bishop can make
     * @param board:      the current chessboard
     * @param myPosition: the current Bishop's position
     * @param piece:      the Bishop you are moving, including the piece's color
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition,
                                                   ChessPiece piece) throws InvalidArgumentsException {
        // Check Parameters
        if(board == null || piece == null || myPosition == null) {
            throw new InvalidArgumentsException("pieceMoves in BishopMoves");
        }
        if(!myPosition.inBounds()) {
            throw new InvalidArgumentsException("pieceMoves in BishopMoves");
        }

        Collection<ChessMove> bishopMoves = new ArrayList<>();

        // Add moves to Collection per direction
        for(int[] currDirection : ChessPiece.DIAGONALS_DIRECTIONS) {
            // See moveRay method in ChessPiece module for more information
            //    - Adds all available moves in a given direction
            bishopMoves.addAll(piece.rayMoves(board, myPosition, currDirection[0], currDirection[1], true));
        }
        return bishopMoves;
    }
}