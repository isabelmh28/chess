/**
 * PostLogin Module
 * REPL logic for the Post-Login SIGNEDIN state
 * Services the following endpoints:
 *     help
 *     quit
 *     logout
 *     create
 *     list
 *     join
 *     observe
 * Author: Isabel Hinton
 */

package ui.statehandlers;

import chess.ChessGame;
import chess.ChessBoard;

import ui.BoardGenerator;
import ui.EscapeSequences;

import client.REPL;
import client.ServerFacade;
import client.ResponseException;

import model.GameData;
import model.transactions.result.ListGamesResult;
import model.transactions.result.CreateGameResult;
import model.transactions.request.JoinGameRequest;
import model.transactions.request.CreateGameRequest;

import java.util.Objects;
import java.util.ArrayList;
import java.util.Collection;

public class PostLogin {
    // Private Members
    private final REPL repl;
    private final ServerFacade facade;
    // Constructor
    public PostLogin(REPL repl, ServerFacade facade) {
        this.repl = repl;
        this.facade = facade;
    }

    // State Handler
    /**
     * Identifies the attempted command in the user input
     * NOTE: only recognizes a command if it is the first word in the user input
     *       Incorrect input will prompt user to type 'help' to see available commands
     * @param input Typed user input retrieved in the REPL
     */
    public void handlePostLogin(String input) {
        // Check Parameters
        String[] words;
        if(input == null) {
            words = new String[0];
        }
        else {
            words = input.split("\\s+");
        }
        // Gate command search
        String command = (words.length > 0) ? words[0].toLowerCase() : "";
        // Identify the user's command and run each endpoint's handler
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

    // Endpoint Functions
    /**
     * Logic for implementing the 'help' command in the SIGNEDIN state.
     * Prints all the SIGNEDIN commands including their correct format.
     * NOTE: some of the 'help' information is the same.
     *       See the printHelp function in the REPL class.
     * @param words: detected user command from the REPL.
     */
    private void helpPostLogin(String[] words) {
        // Check Parameters
        // THIS SHOULD NEVER BE MET if the input words are properly gated
        if((words == null) || (words.length == 0)) {
            repl.printArgErrorSpecific("help", "help", "help",
                    "Invalid command attempted to call the 'help' case.");
            return;
        }
        // Check user input: 'help' command takes no arguments
        if(words.length > 1) {
            repl.printArgErrorNoArgs(words[0]);
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
    /**
     * Logic for implementing the 'quit' command in the SIGNEDIN state
     * Transitions to QUIT state to exit the program
     * @param words: detected user command from the REPL
     */
    private void quit(String[] words) {
        // Check Parameters
        // THIS SHOULD NEVER BE MET if the input words are properly gated
        if((words == null) || (words.length == 0)) {
            repl.printArgErrorSpecific("quit", "quit", "quit",
                    "Invalid command attempted to call the 'quit' case.");
            return;
        }
        // Check user input: 'quit' command takes no arguments
        if(words.length > 1) {
            repl.printArgErrorNoArgs(words[0]);
            return;
        }
        repl.setState(REPL.State.QUIT);
        repl.printOutput(
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + "Thank you for playing!\n" +
                        EscapeSequences.RESET_TEXT_COLOR);
    }
    /**
     * Logic for implementing the 'logout' command in the SIGNEDIN state
     * Transitions to SIGNEDOUT state
     * @param words: detected user command from the REPL
     */
    private void logoutPostLogin(String[] words) {
        // Check Parameters
        // THIS SHOULD NEVER BE MET if the input words are properly gated
        if((words == null) || (words.length == 0)) {
            repl.printArgErrorSpecific("logout", "logout", "logout",
                    "Invalid command attempted to call the 'logout' case.");
            return;
        }
        // Check user input: 'logout' command takes no arguments
        if(words.length > 1) {
            repl.printArgErrorNoArgs(words[0]);
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
    /**
     * Logic for implementing the 'create' command in the SIGNEDIN state
     * Creates a game and adds it to the database.
     * Shows the new game's gameName if successful
     * @param words: detected user command from the REPL
     */
    private void createGamePostLogin(String[] words) {
        // Check Parameters
        // THIS SHOULD NEVER BE MET if the input words are properly gated
        if((words == null) || (words.length == 0)) {
            repl.printArgErrorSpecific("create", "create <GAME_NAME>",
                    "create myNewGame",
                    "Invalid command attempted to call the 'create' case.");
            return;
        }
        // Check user input: 'create' command takes 1 argument
        if(words.length != 2) {
            repl.printArgErrorSpecific(words[0], "create <GAME_NAME>",
                    "create myNewGame", "");
            return;
        }
        // Retrieve input arguments
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
    /**
     * Logic for implementing the 'list' command in the SIGNEDIN state
     * Lists the games in the database.
     * Shows the game's place in the list, the gameName, and the players' usernames
     * @param words: detected user command from the REPL
     */
    private void listGamesPostLogin(String[] words) {
        // Check Parameters
        // THIS SHOULD NEVER BE MET if the input words are properly gated
        if((words == null) || (words.length == 0)) {
            repl.printArgErrorSpecific("list", "list", "list",
                    "Invalid command attempted to call the 'list' case.");
            return;
        }
        // Check user input: 'list' command takes no arguments
        if(words.length > 1) {
            repl.printArgErrorNoArgs(words[0]);
            return;
        }
        try {
            ListGamesResult list = facade.listGames(repl.getAuthToken());
            Collection<GameData> gamesList = list.games();
            // Empty list to add the games to
            ArrayList<GameData> gameList = new ArrayList<>();
            // Check current list to make sure it is not empty
            if((gamesList == null) || (gamesList.isEmpty())) {
                // If the current list is empty, make sure my local list is empty too
                repl.setGameList(new ArrayList<>());
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
            // Build current list if not empty
            int index = 1;
            StringBuilder listOutput = new StringBuilder(
                    """
                        NOTE: if there is '<null>' next to the WHITE or BLACK space, there is no player
                              currently in that role.
                        
                        Game List:\s
                        """);
            for(GameData game : gamesList) {
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
    /**
     * Logic for implementing the 'join' command in the SIGNEDIN state.
     * Allows the user to join a game.
     * User must provide the game number they wish to join and their desired player color.
     * @param words: detected user command from the REPL.
     */
    private void joinGamePostLogin(String[] words) {
        // Check Parameters
        // THIS SHOULD NEVER BE MET if the input words are properly gated
        if((words == null) || (words.length == 0)) {
            repl.printArgErrorSpecific("join", "join <NUMBER> <WHITE | BLACK>",
                    "join 12345 WHITE",
                    "Invalid command attempted to call the 'join' case.");
            return;
        }
        // Check user input: 'join' command takes 2 arguments
        if(words.length != 3) {
            repl.printArgErrorSpecific(words[0], "join <NUMBER> <WHITE | BLACK>",
                    "join 12345 WHITE", "");
            return;
        }
        // Retrieve and validate input arguments
        String player = words[2];
        if(        !Objects.equals(player.toUpperCase(), ChessGame.TeamColor.WHITE.name())
                && !Objects.equals(player.toUpperCase(), ChessGame.TeamColor.BLACK.name())) {
            repl.printArgErrorSpecific(words[0], "join <NUMBER> <WHITE | BLACK>",
                    "join 12345 WHITE", "player options are only WHITE or BLACK:");
            return;
        }
        if(!words[1].matches("\\d+")) {
            repl.printArgErrorSpecific(words[0], "join <NUMBER> <WHITE | BLACK>",
                    "join 12345 WHITE", "game number argument MUST be an INTEGER");
            return;
        }
        int gameNumber = Integer.parseInt(words[1]) - 1;
        if(gameNumber < 0 || gameNumber >= repl.getGameList().size()) {
            repl.printArgErrorSpecific(words[0], "join <NUMBER> <WHITE | BLACK>",
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
    /**
     * Logic for implementing the 'observe' command in the SIGNEDIN state.
     * Allows the user to observe a game in the database without joining as a player
     * User must provide the game number in the list they wish to observe
     * Observing perspective is the WHITE view
     * @param words: detected user command from the REPL.
     */
    private void observeGamePostLogin(String[] words) {
        // Check Parameters
        // THIS SHOULD NEVER BE MET if the input words are properly gated
        if((words == null) || (words.length == 0)) {
            repl.printArgErrorSpecific("observe", "observe <NUMBER>",
                    "observe 12345",
                    "Invalid command attempted to call the 'observe' case.");
            return;
        }
        // Check user input: 'observe' command takes 1 argument
        if(words.length != 2) {
            repl.printArgErrorSpecific(words[0], "observe <NUMBER>",
                    "observe 12345", "");
            return;
        }
        // Retrieve and validate input argument
        if(!words[1].matches("\\d+")) {
            repl.printArgErrorSpecific(words[0], "observe <NUMBER>", "observe 12345", "");
            return;
        }
        int gameNumber = Integer.parseInt(words[1]) - 1;
        if(gameNumber < 0 || gameNumber >= repl.getGameList().size()) {
            repl.printArgErrorSpecific(words[0], "observe <NUMBER>", "observe 12345",
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
