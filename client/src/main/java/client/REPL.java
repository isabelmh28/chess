package client;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import model.transactions.request.*;
import model.transactions.result.*;
import ui.BoardGenerator;
import ui.EscapeSequences;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

/**
 * Manages the Scanner input loop, tracks current user state,
 * processes user commands, and prints user output
 */

public class REPL {
    public enum State {
        SIGNEDOUT,
        SIGNEDIN,
        QUIT
    }

    private State state;
    private final ServerFacade facade;
    private final Scanner scanner;
    private String authToken;
    private final ArrayList<GameData> gameList;


    public REPL(int port) {
        state = State.SIGNEDOUT;
        facade = new ServerFacade(port);
        scanner = new Scanner(System.in);
        authToken = null;
        gameList = new ArrayList<>();
    }

    public void run() {
        printOutput(
                "Welcome to Chess! Type " +
                        EscapeSequences.SET_TEXT_COLOR_GREEN + "'help' " +
                EscapeSequences.SET_TEXT_COLOR_BLUE + "to get started.");

        while(state != State.QUIT) {
            printState();
            if(!scanner.hasNextLine()) {
                break; // exit if stream closes
            }
            String input = scanner.nextLine().trim();
            if(input.isEmpty()) {
                continue;
            }

            if(state == State.SIGNEDOUT) {
                handlePreLogin(input);
            }
            else if(state == State.SIGNEDIN) {
                handlePostLogin(input);
            }
        }

        printOutput(
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + "Input stream closed. Have a good day!\n" +
                EscapeSequences.RESET_TEXT_COLOR);
    }

    private void printState() {
        System.out.print(
                EscapeSequences.SET_TEXT_COLOR_YELLOW + "[" + state + "] >>> " +
                        EscapeSequences.RESET_TEXT_COLOR);
        System.out.flush();
    }

    private void printOutput(String output) {
        System.out.print(
                EscapeSequences.SET_TEXT_COLOR_BLUE + output +
                        EscapeSequences.RESET_TEXT_COLOR + "\n");
    }

    private void printError(String message) {
        printOutput(
                EscapeSequences.SET_TEXT_COLOR_RED + message +
                        EscapeSequences.RESET_TEXT_COLOR);
    }

    private void handlePreLogin(String input) {
        var words = input.split("\\s+");
        var command = words[0].toLowerCase();

        switch(command) {
            case "help" :
                helpPreLogin(words);
                break;
            case "quit" :
                quit(words);
                break;
            case "register" :
                registerPreLogin(words);
                break;
            case "login" :
                loginPreLogin(words);
                break;
            default :
                printError(
                        "Invalid command. Type " + EscapeSequences.SET_TEXT_COLOR_GREEN + "'help' " +
                        EscapeSequences.SET_TEXT_COLOR_RED + "to see valid commands. Please try again!");
                break;
        }
    }

    private void handlePostLogin(String input) {
        var words = input.split(" ");
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
                printError(
                        "Invalid command. Type " + EscapeSequences.SET_TEXT_COLOR_GREEN + "'help' " +
                                EscapeSequences.SET_TEXT_COLOR_RED +
                                "to see valid commands. Please try again!");
        }
    }

    private void helpPreLogin(String[] words) {
        if(words.length != 1) {
            printError("The " + EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "'help' " + EscapeSequences.SET_TEXT_COLOR_RED +
                    " command does not take arguments. Please try again!");
            return;
        }
        String output = String.format("""
                Below are the available commands.
                %s: misspelling a command or providing the incorrect input will require a new command.
                      The angle brackets <> contain arguments that you will need to provide for that command.
                      Do not include the brackets when you provide your information.
                
                %s: list possible commands
                
                %s: exit the game
                
                %s: to create an account
                      %s
                
                %s: to log into an existing account
                      %s
                """,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "NOTE" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "help" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "quit" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "register" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + "register <USERNAME> <PASSWORD> <EMAIL>" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "login" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + "login <USERNAME> <PASSWORD>" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE);
        printOutput(output);
    }

    private void quit(String[] words) {
        if(words.length != 1) {
            printError("The " + EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "'quit' " + EscapeSequences.SET_TEXT_COLOR_RED +
                    " command does not take arguments. Please try again!");
            return;
        }
        state = State.QUIT;
        printOutput(
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + "Thank you for playing!\n" +
                        EscapeSequences.RESET_TEXT_COLOR);
    }

    private void registerPreLogin(String[] words) {
        if(words.length !=4) {
            printError(String.format("""
                    %s command must be in the following format:
                    %s
                    EX: %s
                    Please try again!
                    """,
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "register" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "register <USERNAME> <PASSWORD> <EMAIL>" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                            "register myName new_password myEmail@company.com" +
                            EscapeSequences.SET_TEXT_COLOR_RED));
            return;
        }
        String username = words[1];
        String password = words[2];
        String email    = words[3];
        RegisterRequest register = new RegisterRequest(username, password, email);

        try {
            var authData = facade.register(register);
            authToken = authData.authToken();
            state = State.SIGNEDIN;
            printOutput(String.format("""
                    Successfully Signed in with your credentials! Welcome %s!
                    """, username));
        } catch (ResponseException e) {
            printError(e.getMessage());
        }
    }

    private void loginPreLogin(String[] words) {
        if(words.length != 3) {
            printError(String.format("""
                    %s command must be in the following format:
                    %s
                    EX: %s
                    Please try again!
                    """,
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "login" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "login <USERNAME> <PASSWORD>" +
                            EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "login myName new_password" +
                            EscapeSequences.SET_TEXT_COLOR_RED));
            return;
        }

        String username = words[1];
        String password = words[2];
        LoginRequest login = new LoginRequest(username, password);

        try {
            var authData = facade.login(login);
            authToken = authData.authToken();
            state = State.SIGNEDIN;
            printOutput(String.format("""
                    Successfully Logged in with your credentials! Welcome %s!
                    """, username));
            ListGamesResult list = facade.listGames(authToken);
            gameList.addAll(list.games());
        } catch (ResponseException e) {
            printError(e.getMessage());
        }
    }

    private void helpPostLogin(String[] words) {
        if(words.length != 1) {
            printError("The " + EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "'help' " + EscapeSequences.SET_TEXT_COLOR_RED +
                    " command does not take arguments. Please try again!");
            return;
        }
        String output = String.format("""
                Below are the available commands.
                %s: misspelling a command or providing the incorrect input will require a new command.
                      The angle brackets <> contain arguments that you need to provide for that command.
                      Do not include the brackets when you provide your information.
                
                %s: list possible commands
                
                %s: exit the game
                
                %s: return to pre-login menu
                
                %s: create a new game.
                      %s
                
                %s: list all existing games
                
                %s: join a game by providing the game's list number and your player color(White or Black).
                      %s
                
                %s: watch a game by providing the game's list number
                      %s
                """,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "NOTE" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "help" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "quit" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
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
        printOutput(output);
    }

    private void logoutPostLogin(String[] words) {
        if(words.length != 1) {
            printError("The " + EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "'logout' " + EscapeSequences.SET_TEXT_COLOR_RED +
                    " command does not take arguments. Please try again!");
            return;
        }

        LogoutRequest logout = new LogoutRequest(authToken);

        try {
            facade.logout(logout);
            state = State.SIGNEDOUT;
            printOutput(String.format("""
                    Successfully Logged out!
                    Type %s for pre-login options
                    """,
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "'help'" +
                            EscapeSequences.SET_TEXT_COLOR_BLUE));
        } catch (ResponseException e) {
            printError(e.getMessage());
        }
    }

    private void createGamePostLogin(String[] words) {
        if(words.length != 2) {
            printError(String.format("""
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
            CreateGameResult gameID = facade.createGame(authToken, create);
            printOutput(String.format("""
                    Successfully created game! New game:
                    Name: %s
                    ID:   %d
                    """, gameName, gameID.gameID()));
        } catch (ResponseException e) {
            printError(e.getMessage());
        }
    }

    private void listGamesPostLogin(String[] words) {
        if(words.length != 1) {
            printError("The " + EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "'list' " + EscapeSequences.SET_TEXT_COLOR_RED +
                    " command does not take arguments. Please try again!");
            return;
        }
        try {
            ListGamesResult list = facade.listGames(authToken);
            var gamesList = list.games();
            gameList.clear();

            if((gamesList == null) || (gamesList.isEmpty())) {
                printOutput(String.format("""
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
            printOutput(listOutput.toString());
        } catch (ResponseException e) {
            printError(e.getMessage());
        }
    }

    private void joinGamePostLogin(String[] words) {
        if(words.length != 3) {
            printError(String.format("""
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
            printError(String.format("""
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
            printError(String.format("""
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
        if(gameNumber < 0 || gameNumber >= gameList.size()) {
            printError(String.format("""
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
        GameData game = gameList.get(gameNumber);
        JoinGameRequest join = new JoinGameRequest(player, game.gameID());

        try {
            facade.joinGame(authToken, join);
            printOutput("""
                    Successfully joined game!
                    """);

            ChessGame.TeamColor team = ChessGame.TeamColor.valueOf(player.toUpperCase());
            ChessBoard board = game.game().getBoard();
            BoardGenerator gen = new BoardGenerator(team, board);
            gen.drawBoard();
        } catch (ResponseException e) {
            printError(e.getMessage());
        }
    }

    private void observeGamePostLogin(String[] words) {
        if(words.length != 2) {
            printError(String.format("""
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
            printError(String.format("""
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
        if(gameNumber < 0 || gameNumber >= gameList.size()) {
            printError(String.format("""
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
        GameData game = gameList.get(gameNumber);
        printOutput("""
                Successfully observing the game!
                """);
        BoardGenerator gen = new BoardGenerator(ChessGame.TeamColor.WHITE, game.game().getBoard());
        gen.drawBoard();
    }
}
