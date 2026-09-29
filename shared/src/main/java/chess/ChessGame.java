package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    public static final int ROWS = 8;
    public static final int COLS = 8;

    private TeamColor currentTeam;
    private ChessBoard board;

    public ChessGame() {
        currentTeam = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     * Move is valid if move is a "piece move" at that position AND if it doesn't let
     * the King get in check.
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // If position doesn't have a piece on it, return null
        if(board.getPiece(startPosition) == null) { return null;}
        // valid moves that are returned
        Collection<ChessMove> valid = new ArrayList<>();
        TeamColor color = board.getPiece(startPosition).getTeamColor();
        // possible moves
        Collection<ChessMove> possible = board.getPiece(startPosition).pieceMoves(board, startPosition);
        for(ChessMove move : possible){
            // create a temporary copy
            ChessBoard tempBoard = new ChessBoard(board);
            // If we are not promoting, just get the original piece and copy over
            if(move.getPromotionPiece() == null) {
                ChessPiece piece = tempBoard.getPiece(startPosition);
                // add the potential move to the temp board
                tempBoard.addPiece(move.getEndPosition(), piece);
            }
            else { // If we are promoting, get the Promotion type
                ChessPiece piece = new ChessPiece(color, move.getPromotionPiece());
                // add the potential move to the temp board
                tempBoard.addPiece(move.getEndPosition(), piece);
            }
            tempBoard.addPiece(startPosition, null);
            // If move does not leave the King in check, add move to collection
            if(!isInCheck(tempBoard, color)) {
                valid.add(move);
            }
        }
        return valid;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Invalids: Move cannot act on a null piece/empty position, and you can only move your team's pieces.
        if(board.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("makeMove move invalid; trying to move null piece.");
        }
        else if(board.getPiece(move.getStartPosition()).getTeamColor() != currentTeam) {
            throw new InvalidMoveException("makeMove move invalid; not the correct team's turn.");
        }
        // Only make a move if it is valid.
        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        if(!valid.contains(move)) {
            throw new InvalidMoveException("makeMove move invalid; not contained in valid moves.");
        }
        else {
            if(move.getPromotionPiece() == null){
                board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            }
            else {
                ChessPiece promotedPawn = new ChessPiece(currentTeam, move.getPromotionPiece());
                board.addPiece(move.getEndPosition(), promotedPawn);
            }
            board.addPiece(move.getStartPosition(), null);
        }
        // Change team after making a move.
        currentTeam = (currentTeam == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Checks if a team's king is in Check given a board
     * NOTE: This method was created to find check on a temporary
     *       board that would potentially make a move, see if it was in check,
     *       and return if that move was actually valid.
     *       The version that does not pass in the board uses this method,
     *       simply using this.board as the passed in board.
     */
    public boolean isInCheck(ChessBoard board, TeamColor color) {
        boolean check = false;
        ChessPosition kingPosition = board.kingPosition(color);
        for(int row = 1; row <= ROWS; row++) {
            for(int col = 1; col <= COLS; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if(piece != null && piece.getTeamColor() != color) {
                    Collection<ChessMove> enemyMoves = piece.pieceMoves(board, new ChessPosition(row, col));
                    for(ChessMove move : enemyMoves) {
                        if (move.getEndPosition().equals(kingPosition)) return true;
                    }
                }
            }
        }
        return check;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) { return isInCheck(this.board, teamColor);}


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(isInCheck(teamColor)) {
            for(int row = 1; row <= ROWS; row++) {
                for(int col = 1; col <= COLS; col++) {
                    ChessPosition pos = new ChessPosition(row, col);
                    if((board.getPiece(pos) != null) && (board.getPiece(pos).getTeamColor() == teamColor)){
                        if(!validMoves(pos).isEmpty()) return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(!isInCheck(teamColor)) {
            for(int row = 1; row <= ROWS; row++) {
                for(int col = 1; col <= COLS; col++) {
                    ChessPosition pos = new ChessPosition(row, col);
                    if((board.getPiece(pos) != null) && (board.getPiece(pos).getTeamColor() == teamColor)){
                        if(!validMoves(pos).isEmpty()) return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) { this.board = new ChessBoard(board);}

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() { return board;}

    @Override
    public int hashCode() { return 31 * board.hashCode() + 41 * Objects.hashCode(currentTeam);}

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        else if((obj == null) || (getClass() != obj.getClass())) return false;
        ChessGame that = (ChessGame) obj;
        return Objects.equals(board, that.board) && Objects.equals(currentTeam, that.currentTeam);
    }
}