package ui.statehandlers;

import chess.ChessBoard;
import chess.ChessGame;
import client.REPL;
import client.ResponseException;
import client.ServerFacade;
import model.transactions.request.CreateGameRequest;
import model.transactions.request.JoinGameRequest;
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
        if(words.length != 1){
            repl.printArgErrorNoArgs(words);
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
            repl.printArgErrorNoArgs(words);
        return;
        }
        repl.setState(REPL.State.QUIT);
        repl.printOutput(
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + "Thank you for playing!\n" +
                        EscapeSequences.RESET_TEXT_COLOR);
    }

    private void logoutPostLogin(String[] words) {
        if(words.length != 1) {
            repl.printArgErrorNoArgs(words);
        return;
        }
        String authToken = repl.getAuthToken();
        try {
            facade.logout(authToken);
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
            repl.printArgErrorSpecific(words, "create <GAME_NAME>",
                    "create myNewGame", "");
            return;
        }
        String gameName = words[1];
        CreateGameRequest create = new CreateGameRequest(gameName);
        try {
            CreateGameResult gameID = facade.createGame(repl.getAuthToken(), create);
            repl.printOutput(String.format("""
                    Successfully created game! New game:
                    Name: %s
                    """, gameName));
        } catch (ResponseException e) {
            repl.printError(e.getMessage());
        }
    }

    private void listGamesPostLogin(String[] words) {
        if(words.length != 1) {
            repl.printArgErrorNoArgs(words);
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
            repl.printArgErrorSpecific(words, "join <NUMBER> <WHITE | BLACK>",
                    "join 12345 WHITE", "");
            return;
        }
        String player = words[2];
        if(        !Objects.equals(player.toUpperCase(), ChessGame.TeamColor.WHITE.name())
                && !Objects.equals(player.toUpperCase(), ChessGame.TeamColor.BLACK.name())) {
            repl.printArgErrorSpecific(words, "join <NUMBER> <WHITE | BLACK>",
                    "join 12345 WHITE", "player options are only WHITE or BLACK:");
            return;
        }
        if(!words[1].matches("\\d+")) {
            repl.printArgErrorSpecific(words, "join <NUMBER> <WHITE | BLACK>",
                    "join 12345 WHITE", "game number argument MUST be an INTEGER");
            return;
        }
        int gameNumber = Integer.parseInt(words[1]) - 1;
        if(gameNumber < 0 || gameNumber >= repl.getGameList().size()) {
            repl.printArgErrorSpecific(words, "join <NUMBER> <WHITE | BLACK>",
                    "join 12345 WHITE",
                    "Any input for the NUMBER argument that is not in the list is invalid.\n" +
                            "See the " + EscapeSequences.SET_TEXT_COLOR_GREEN +
                            "'list'" + EscapeSequences.SET_TEXT_COLOR_RED +
                    " command for more game information.");
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
            repl.printArgErrorSpecific(words, "observe <NUMBER>",
                    "observe 12345", "");
            return;
        }
        if(!words[1].matches("\\d+")) {
            repl.printArgErrorSpecific(words, "observe <NUMBER>", "observe 12345", "");
            return;
        }
        int gameNumber = Integer.parseInt(words[1]) - 1;
        if(gameNumber < 0 || gameNumber >= repl.getGameList().size()) {
            repl.printArgErrorSpecific(words, "observe <NUMBER>", "observe 12345",
                    "Any input for the NUMBER argument that is not in the list is invalid.\n" +
                            "See the " + EscapeSequences.SET_TEXT_COLOR_GREEN +
                            "'list'" + EscapeSequences.SET_TEXT_COLOR_RED +
                            " command for more game information.");
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
