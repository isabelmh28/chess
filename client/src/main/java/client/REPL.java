package client;

import model.transactions.request.*;
import model.transactions.result.*;

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
    private ArrayList<Integer> gameIDList;


    public REPL(int port) {
        state = State.SIGNEDOUT;
        facade = new ServerFacade(port);
        scanner = new Scanner(System.in);
        authToken = null;
        gameIDList = new ArrayList<>();
    }

    public void run() {
        printOutput("Welcome to Chess! Type 'Help' to get started.");

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

        System.out.println("Input stream closed. Have a good day!\n");
    }

    private void printState() {
        printOutput("[" + state + "] >>> \n");
    }

    private void printOutput(String output) {
        System.out.print(output);
    }

    private void printError(String message) {
        printOutput("ERROR: " + message + "\n");
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
                printError("Invalid command. Type 'help' to see valid commands. Please try again!");
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
                printError("Invalid command. Please type 'help' to see available commands. Please try again!");
        }
    }

    private void helpPreLogin(String[] words) {
        if(words.length != 1) {
            printError("The 'help' command does not take arguments. Please try again!");
            return;
        }
        String output = """
                Below are the available commands.
                NOTE: misspelling a command or providing the incorrect input will require a new command.
                      The angle brackets <> contain arguments that you will need to provide for that command.
                      Do not include the brackets when you provide your information.
                
                help: list possible commands
                
                quit: exit the game
                
                register: to create an account
                      register <USERNAME> <PASSWORD> <EMAIL>
                
                login: to log into an existing account
                      login <USERNAME> <PASSWORD>
                """;
        printOutput(output);
    }

    private void quit(String[] words) {
        if(words.length != 1) {
            printError("The 'quit' command does not take arguments. Please try again!");
            return;
        }
        state = State.QUIT;
    }

    private void registerPreLogin(String[] words) {
        if(words.length !=4) {
            printError("""
                    register command must be in the following format:
                    register <USERNAME> <PASSWORD> <EMAIL>
                    EX: register myName myPassword myEmail@email.com
                    Please try again!
                    """);
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
            printError("""
                    login command must be in the following format:
                    login <USERNAME> <PASSWORD>
                    EX: login myName myPassword
                    Please try again!
                    """);
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
        } catch (ResponseException e) {
            printError(e.getMessage());
        }
    }

    private void helpPostLogin(String[] words) {
        if(words.length != 1) {
            printError("The 'help' command does not take arguments. Please try again!");
            return;
        }
        String output = """
                Below are the available commands.
                NOTE: misspelling a command or providing the incorrect input will require a new command.
                      The angle brackets <> contain arguments that you will need to provide for that command.
                      Do not include the brackets when you provide your information.
                
                help: list possible commands
                
                quit: exit the game
                
                logout: return to pre-login menu
                
                create: create a new game.
                      create <GAME_NAME>
                
                list: list all existing games
                
                join: join a game by providing the game's list number and your desired player color(White or Black).
                      join <NUMBER> <WHITE | BLACK>
                
                observe: watch a game by providing the game's list number
                      observe <NUMBER>
                """;
        printOutput(output);
    }

    private void logoutPostLogin(String[] words) {
        if(words.length != 1) {
            printError("The 'logout' command does not take arguments. Please try again!");
            return;
        }

        LogoutRequest logout = new LogoutRequest(authToken);

        try {
            facade.logout(logout);
            state = State.SIGNEDOUT;
            printOutput(String.format("""
                    Successfully Logged out!
                    Type 'help' for pre-login options
                    """));
        } catch (ResponseException e) {
            printError(e.getMessage());
        }
    }

    private void createGamePostLogin(String[] words) {
        if(words.length != 2) {
            printError("""
                    create command must be in the following format:
                    create <GAME_NAME>
                    EX: create myNewGame
                    Please try again!
                    """);
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
            printError("The 'list' command does not take arguments. Please try again!");
            return;
        }
        try {
            ListGamesResult list = facade.listGames(authToken);
            var gamesList = list.games();
            gameIDList.clear();

            if((gamesList == null) || (gamesList.isEmpty())) {
                printOutput("""
                        There are no active games. Create one with the 'create' command."
                        See 'help' for more information.
                        """);
                return;
            }
            int index = 1;
            StringBuilder listOutput = new StringBuilder("Game List: \n");
            for(var game : gamesList) {
                gameIDList.add(game.gameID());
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
            printError("""
                    join command must be in the following format:
                    join <NUMBER> <WHITE | BLACK>
                    EX: join 12345 WHITE
                    Please try again!
                    """);
            return;
        }
        String player = words[2];
        if(!Objects.equals(player.toUpperCase(), "WHITE") && !Objects.equals(player.toUpperCase(), "BLACK")) {
            printError("""
                    join player options are only WHITE or BLACK:
                    join <NUMBER> <WHITE | BLACK>
                    EX: join 12345 WHITE
                    Any input for the player argument besides WHITE or BLACK is invalid.
                    Please try again!
                    """);
            return;
        }
        if(!words[1].matches("\\d+")) {
            printError("""
                    join game number argument MUST be an INTEGER
                    join <NUMBER> <WHITE | BLACK>
                    EX: join 12345 WHITE
                    Any input for the NUMBER argument that is not an integer is invalid.
                    Please try again!
                    """);
            return;
        }
        int gameNumber = Integer.parseInt(words[1]) - 1;
        if(gameNumber < 0 || gameNumber >= gameIDList.size()) {
            printError("""
                    join game number argument MUST be more than 0 and less than the list size.
                    join <NUMBER> <WHITE | BLACK>
                    EX: join 12345 WHITE
                    Any input for the NUMBER argument that is not in the list is invalid.
                    See the 'list' command for more game information.
                    Please try again!
                    """);
            return;
        }
        int gameID = gameIDList.get(gameNumber);
        JoinGameRequest join = new JoinGameRequest(player, gameID);

        try {
            facade.joinGame(authToken, join);
            printOutput("""
                    Successfully joined game!
                    """);
        } catch (ResponseException e) {
            printError(e.getMessage());
        }
    }

    private void observeGamePostLogin(String[] words) {
        if(words.length != 2) {
            printError("""
                    observe command must be in the following format:
                    observe <NUMBER>
                    EX: observe 12345
                    Please try again!
                    """);
            return;
        }
        if(!words[1].matches("\\d+")) {
            printError("""
                    observe game number argument MUST be an INTEGER
                    observe <NUMBER>
                    EX: observe 12345
                    Any input for the NUMBER argument that is not an integer is invalid.
                    Please try again!
                    """);
            return;
        }
        int gameNumber = Integer.parseInt(words[1]) - 1;
        if(gameNumber < 0 || gameNumber >= gameIDList.size()) {
            printError("""
                    observe game number argument MUST be more than 0 and less than the list size.
                    observe <NUMBER>
                    EX: observe 12345
                    Any input for the NUMBER argument that is not in the list is invalid.
                    See the 'list' command for more game information.
                    Please try again!
                    """);
            return;
        }
        int gameID = gameIDList.get(gameNumber);
        printOutput("""
                Successfully observing the game!
                """);
    }

}
