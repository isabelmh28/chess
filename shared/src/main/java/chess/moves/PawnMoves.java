package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;


public class PawnMoves {
    /**
     * A moves helper class containing the moves a Pawn can make
     * @param board: the current chessboard
     * @param pos:   the current Pawn's position
     * @param piece: the Pawn you are moving, including the piece's color
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos,
                                                   ChessPiece piece) throws InvalidArgumentsException {
        // Check Parameters
        if(board == null || piece == null || pos == null) {
            throw new InvalidArgumentsException("pieceMoves in PawnMoves");
        }
        if(!pos.inBounds()) {
            throw new InvalidArgumentsException("pieceMoves in PawnMoves");
        }

        Collection<ChessMove> pawnMoves = new ArrayList<>();

        // Depends on the Team Color
        int startRow = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 2 : 7;
        int promRow = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 8 : 1;
        int rowDelta = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        int[][] capture = {
                {rowDelta, 1},
                {rowDelta, -1}
        };
        // Not dependent on Team Color
        int currRow = pos.getRow();
        int currCol = pos.getColumn();

        // Move Options for Pawns //

        // Move Forward Once
        int singleFor = currRow + rowDelta;
        ChessPosition singlePos = new ChessPosition(singleFor, currCol);
        if(singlePos.inBounds() && (board.getPiece(singlePos) == null)) {
            // If on the Promotion Row, provide promotion options for pawn
            if(singleFor == promRow) {
                pawnMoves.add(new ChessMove(pos, singlePos, ChessPiece.PieceType.ROOK));
                pawnMoves.add(new ChessMove(pos, singlePos, ChessPiece.PieceType.KNIGHT));
                pawnMoves.add(new ChessMove(pos, singlePos, ChessPiece.PieceType.BISHOP));
                pawnMoves.add(new ChessMove(pos, singlePos, ChessPiece.PieceType.QUEEN));
            }
            else {
                // If not on promotion row, add a normal move
                pawnMoves.add(new ChessMove(pos, singlePos, null));
                // If in the start row, can move forward two
                if(currRow == startRow) {
                    int doubleFor = singleFor + rowDelta;
                    ChessPosition doublePos = new ChessPosition(doubleFor, currCol);
                    if(board.getPiece(doublePos) == null) {
                        pawnMoves.add(new ChessMove(pos, doublePos, null));
                    }
                }
            }
        }
        // Can capture diagonally
        for(int[] capt : capture) {
            ChessPosition captPos = new ChessPosition(currRow + capt[0], currCol + capt[1]);
            if(captPos.inBounds() && (board.getPiece(captPos) != null)) {
                if(board.getPiece(captPos).getTeamColor() != piece.getTeamColor()) {
                    // If capture occurs on the promotion row, add move and promote
                    if(captPos.getRow() == promRow) {
                        pawnMoves.add(new ChessMove(pos, captPos, ChessPiece.PieceType.ROOK));
                        pawnMoves.add(new ChessMove(pos, captPos, ChessPiece.PieceType.KNIGHT));
                        pawnMoves.add(new ChessMove(pos, captPos, ChessPiece.PieceType.BISHOP));
                        pawnMoves.add(new ChessMove(pos, captPos, ChessPiece.PieceType.QUEEN));
                    }
                    else {
                        pawnMoves.add(new ChessMove(pos, captPos, null));
                    }
                }
            }
        }
        return pawnMoves;
    }
}