package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class BoardGenerator {
    private static final String[] COLS =
            {" a\u2003", " b\u2003", " c\u2003", " d\u2003", " e\u2003",
                    " f\u2003", " g\u2003", " h\u2003"};
    private final ChessGame.TeamColor color;
    private final boolean isWhite;
    private final ChessBoard board;
    public static final int DIMENSION = 8;

    public BoardGenerator(ChessGame.TeamColor color, ChessBoard board) {
        this.color = color;
        isWhite = (color == ChessGame.TeamColor.WHITE);
        this.board = board;
    }

    private static void printSquare(String bg, String textColor, String content) {
        System.out.print(bg + textColor + content + EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
    }

    private static void printBlankSquare(String bg) {
        printSquare(bg, EscapeSequences.SET_TEXT_COLOR_WHITE, " \u2003 ");
    }

    private String getPiece(ChessPiece piece) {
        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        return switch (piece.getPieceType()) {
            case KING -> (pieceColor == ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN -> (pieceColor == ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case BISHOP -> (pieceColor == ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> (pieceColor == ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ROOK -> (pieceColor == ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case PAWN -> (pieceColor == ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }

    private void printColBorder() {
        printBlankSquare(EscapeSequences.SET_BG_COLOR_BLUE);
        for(int i = 0; i < DIMENSION; i++) {
            int index = isWhite ? i : ((DIMENSION - 1) - i);
            String letter = COLS[index];
            printSquare(EscapeSequences.SET_BG_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_WHITE, String.valueOf(letter));
        }
        printBlankSquare(EscapeSequences.SET_BG_COLOR_BLUE);
        System.out.print("\n");
    }

    private void printGameRow(int row) {
        int index = isWhite ? (DIMENSION + 1 - row) : row;

        String rowVal = String.format("\u2003" + index + " ");
        printSquare(EscapeSequences.SET_BG_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_WHITE, rowVal);
        for(int i = 1; i <= DIMENSION; i++) {
            String bg =   (((i + index) % 2) == 0) ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                        :                          EscapeSequences.SET_BG_COLOR_DARK_GREY;
            ChessPiece piece = board.getPiece(new ChessPosition(index, i));
            if(piece != null) {
                String value = getPiece(piece);
                String pieceColor = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                        EscapeSequences.SET_TEXT_COLOR_WHITE : EscapeSequences.SET_TEXT_COLOR_BLACK;
                printSquare(bg, pieceColor, value);
            }
            else { printBlankSquare(bg);}
        }
        rowVal = String.format(" " + index + "\u2003");
        printSquare(EscapeSequences.SET_BG_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_WHITE, rowVal);
        System.out.print("\n");
    }

    public void drawBoard() {
        printColBorder();
        for (int i = 1; i <= DIMENSION; i++) {
            printGameRow(i);
        }
        printColBorder();
    }
}
