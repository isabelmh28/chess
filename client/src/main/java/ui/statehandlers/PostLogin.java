package ui.statehandlers;

import chess.ChessBoard;
import chess.ChessGame;
import client.REPL;
import client.ResponseException;
import client.ServerFacade;
import model.transactions.request.CreateGameRequest;
import model.transactions.request.JoinGameRequest;
import model.transactions.request.LogoutRequest;
import model.transactions.result.CreateGameResult;
import ui.BoardGenerator;
import ui.EscapeSequences;
import model.GameData;
import model.transactions.result.ListGamesResult;

import java.util.ArrayList;
import java.util.Objects;

public class PostLogin {
    private final REPL repl;
    private final ServerFacade facade;

    public PostLogin(REPL repl, ServerFacade facade) {
        this.repl = repl;
        this.facade = facade;
    }

    public void handlePostLogin(String input) {
        var words = input.split("\\s+");
        var command = words[0].toLowerCase();
        switch(command) {
            case "help" :
                helpPostLogin(words);
                break;
            case "quit" :
                quit(words);
                break;
            case "logout" :
                logoutPostLogin(words);
                break;
            case "create" :
                createGamePostLogin(words);
                break;
            case "list" :
                listGamesPostLogin(words);
                break;
            case "join" :
                joinGamePostLogin(words);
                break;
            case "observe" :
                observeGamePostLogin(words);
                break;
            default :
                repl.printError(
                        "Invalid command. Type " + EscapeSequences.SET_TEXT_COLOR_GREEN + "'help' " +
                                EscapeSequences.SET_TEXT_COLOR_RED +
                                "to see valid commands. Please try again!");
        }
    }

    private void helpPostLogin(String[] words) {
        if(words.length != 1) {
            repl.printError("The " + EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "'help' " + EscapeSequences.SET_TEXT_COLOR_RED +
                    " command does not take arguments. Please try again!");
            return;
        }
        String output = String.format("""
                %s: return to pre-login menu
                
                %s: create a new game.
                      %s
                
                %s: list all existing games
                
                %s: join a game by providing the game's list number and your player color(White or Black).
                      %s
                
                %s: watch a game by providing the game's list number
                      %s
                """,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "logout" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "create" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + "create <GAME_NAME>" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "list" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "join" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + "join <GAME_NUMBER> <WHITE | BLACK>" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "observe" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + "observe <GAME_NUMBER>" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE);
        repl.printHelp(output);
    }

    private void quit(String[] words) {
        if(words.length != 1) {
            repl.printError("The " + EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "'quit' " + EscapeSequences.SET_TEXT_COLOR_RED +
                    " command does not take arguments. Please try again!");
            return;
        }
        repl.setState(REPL.State.QUIT);
        repl.printOutput(
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + "Thank you for playing!\n" +
                        EscapeSequences.RESET_TEXT_COLOR);
    }

    private void logoutPostLogin(String[] words) {
        if(words.length != 1) {
            repl.printError("The " + EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "'logout' " + EscapeSequences.SET_TEXT_COLOR_RED +
                    " command does not take arguments. Please try again!");
            return;
        }
        LogoutRequest logout = new LogoutRequest(repl.getAuthToken());
        try {
            facade.logout(logout);
            repl.setState(REPL.State.SIGNEDOUT);
            repl.printOutput(String.format("""
                    Successfully Logged out!
                    Type %s for pre-login options
                    """,
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "'help'" +
                            EscapeSequences.SET_TEXT_COLOR_BLUE));
        } catch (ResponseException e) {
            repl.printError(e.getMessage());
        }
    }

    private void createGamePostLogin(String[] words) {
        if(words.length != 2) {
            repl.printError(String.format("""
                    %s command must be in the following format:
                    %s
                    EX: create myNewGame
                    Please try again!
                    """,
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "create" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "create <GAME_NAME>" +
                            EscapeSequences.SET_TEXT_COLOR_RED));
            return;
        }
        String gameName = words[1];
        CreateGameRequest create = new CreateGameRequest(gameName);
        try {
            CreateGameResult gameID = facade.createGame(repl.getAuthToken(), create);
            repl.printOutput(String.format("""
                    Successfully created game! New game:
                    Name: %s
                    ID:   %d
                    """, gameName, gameID.gameID()));
        } catch (ResponseException e) {
            repl.printError(e.getMessage());
        }
    }

    private void listGamesPostLogin(String[] words) {
        if(words.length != 1) {
            repl.printError("The " + EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "'list' " + EscapeSequences.SET_TEXT_COLOR_RED +
                    " command does not take arguments. Please try again!");
            return;
        }
        try {
            ListGamesResult list = facade.listGames(repl.getAuthToken());
            var gamesList = list.games();
            ArrayList<GameData> gameList = repl.getGameList();
            gameList.clear();
            if((gamesList == null) || (gamesList.isEmpty())) {
                repl.printOutput(String.format("""
                        There are no active games. Create one with the %s command.
                        Try %s for more information.
                        """,
                        EscapeSequences.SET_TEXT_COLOR_GREEN + "'create'" +
                                EscapeSequences.SET_TEXT_COLOR_BLUE,
                        EscapeSequences.SET_TEXT_COLOR_GREEN + "'help'" +
                                EscapeSequences.SET_TEXT_COLOR_BLUE));
                return;
            }
            int index = 1;
            StringBuilder listOutput = new StringBuilder("Game List: \n");
            for(var game : gamesList) {
                gameList.add(game);
                String whiteUsername = (game.whiteUsername() != null) ? game.whiteUsername() : "<null>";
                String blackUsername = (game.blackUsername() != null) ? game.blackUsername() : "<null>";
                listOutput.append(String.format("%d. Name: %s | White: %s | Black: %s\n",
                        index, game.gameName(), whiteUsername, blackUsername));
                index++;
            }
            repl.setGameList(gameList);
            repl.printOutput(listOutput.toString());
        } catch (ResponseException e) {
            repl.printError(e.getMessage());
        }
    }

    private void joinGamePostLogin(String[] words) {
        if(words.length != 3) {
            repl.printError(String.format("""
                    %s command must be in the following format:
                    %s
                    EX: %s
                    Please try again!
                    """,
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "'join'" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "join <NUMBER> <WHITE | BLACK>" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "join 12345 WHITE" +
                            EscapeSequences.SET_TEXT_COLOR_RED));
            return;
        }
        String player = words[2];
        if(        !Objects.equals(player.toUpperCase(), ChessGame.TeamColor.WHITE.name())
                && !Objects.equals(player.toUpperCase(), ChessGame.TeamColor.BLACK.name())) {
            repl.printError(String.format("""
                    %s player options are only WHITE or BLACK:
                    %s
                    EX: %s
                    Any input for the player argument besides WHITE or BLACK is invalid.
                    Please try again!
                    """,
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "join" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "join <NUMBER> <WHITE | BLACK>" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "join 12345 WHITE" +
                            EscapeSequences.SET_TEXT_COLOR_RED));
            return;
        }
        if(!words[1].matches("\\d+")) {
            repl.printError(String.format("""
                    %s game number argument MUST be an INTEGER
                    %s
                    EX: %s
                    Any input for the NUMBER argument that is not an integer is invalid.
                    Please try again!
                    """,
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "join" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "join <NUMBER> <WHITE | BLACK>" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "join 12345 WHITE" +
                            EscapeSequences.SET_TEXT_COLOR_RED));
            return;
        }
        int gameNumber = Integer.parseInt(words[1]) - 1;
        if(gameNumber < 0 || gameNumber >= repl.getGameList().size()) {
            repl.printError(String.format("""
                    %s game number argument MUST be more than 0 and less than the list size.
                    %s
                    EX: %s
                    Any input for the NUMBER argument that is not in the list is invalid.
                    See the %s command for more game information.
                    Please try again!
                    """,
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "join" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "join <NUMBER> <WHITE | BLACK>" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "join 12345 WHITE" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "'list'" +
                            EscapeSequences.SET_TEXT_COLOR_RED));
            return;
        }
        GameData game = repl.getGameList().get(gameNumber);
        JoinGameRequest join = new JoinGameRequest(player.toUpperCase(), game.gameID());
        try {
            facade.joinGame(repl.getAuthToken(), join);
            repl.printOutput("""
                    Successfully joined game!
                    """);
            ChessGame.TeamColor team = ChessGame.TeamColor.valueOf(player.toUpperCase());
            ChessBoard board = game.game().getBoard();
            BoardGenerator gen = new BoardGenerator(team, board);
            gen.drawBoard();
        } catch (ResponseException e) {
            repl.printError(e.getMessage());
        }
    }

    private void observeGamePostLogin(String[] words) {
        if(words.length != 2) {
            repl.printError(String.format("""
                    %s command must be in the following format:
                    %s
                    EX: %s
                    Please try again!
                    """,
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "observe" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "observe <NUMBER>" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "observe 12345" +
                            EscapeSequences.SET_TEXT_COLOR_RED));
            return;
        }
        if(!words[1].matches("\\d+")) {
            repl.printError(String.format("""
                    %s game number argument MUST be an INTEGER
                    %s
                    EX: %s
                    Any input for the NUMBER argument that is not an integer is invalid.
                    Please try again!
                    """,
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "observe" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "observe <NUMBER>" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "observe 12345" +
                            EscapeSequences.SET_TEXT_COLOR_RED));
            return;
        }
        int gameNumber = Integer.parseInt(words[1]) - 1;
        if(gameNumber < 0 || gameNumber >= repl.getGameList().size()) {
            repl.printError(String.format("""
                    %s game number argument MUST be more than 0 and less than the list size.
                    %s
                    EX: %s
                    Any input for the NUMBER argument that is not in the list is invalid.
                    See the %s command for more game information.
                    Please try again!
                    """,
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "observe" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "observe <NUMBER>" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "observe 12345" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "'list'" +
                            EscapeSequences.SET_TEXT_COLOR_RED));
            return;
        }
        GameData game = repl.getGameList().get(gameNumber);
        repl.printOutput("""
                Successfully observing the game!
                """);
        BoardGenerator gen = new BoardGenerator(ChessGame.TeamColor.WHITE, game.game().getBoard());
        gen.drawBoard();
    }
}
