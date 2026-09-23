package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;


public class PawnMoves {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos, ChessPiece piece) {
        // Depends on the Team Color
        int startRow = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 2 : 7;
        int promRow = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 8 : 1;
        int rowDelta = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        int[][] capture = {
                {rowDelta, 1},
                {rowDelta, -1}
        };
        // Not dependent on Team Color
        Collection<ChessMove> pawnMoves = new ArrayList<>();
        int currRow = pos.getRow();
        int currCol = pos.getColumn();

        // Move Options for Pawns //

        // Move Forward Once
        int singleFor = currRow + rowDelta;
        ChessPosition singlePos = new ChessPosition(singleFor, currCol);
        if(singlePos.inBounds()) {
            if(board.getPiece(singlePos) == null) {
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
        }
        // Can capture diagonally
        for(int[] capt : capture) {
            ChessPosition captPos = new ChessPosition(currRow + capt[0], currCol + capt[1]);
            if(captPos.inBounds()) {
                if(board.getPiece(captPos) != null) {
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
        }
        return pawnMoves;
    }
}
