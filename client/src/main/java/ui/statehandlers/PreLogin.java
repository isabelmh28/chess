/**
 * PreLogin Module
 * REPL logic for the Pre-Login SIGNEDOUT state
 * Services the following endpoints:
 *     help
 *     quit
 *     register
 *     login
 * Author: Isabel Hinton
 */

package ui.statehandlers;

import ui.EscapeSequences;

import client.REPL;
import client.ServerFacade;
import client.ResponseException;

import model.GameData;
import model.transactions.result.LoginResult;
import model.transactions.result.RegisterResult;
import model.transactions.result.ListGamesResult;
import model.transactions.request.LoginRequest;
import model.transactions.request.RegisterRequest;

import java.util.ArrayList;

public class PreLogin {
    // Private Members
    private final REPL repl;
    private final ServerFacade facade;
    // Constructor
    public PreLogin(REPL repl, ServerFacade facade) {
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
    public void handlePreLogin(String input) {
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
                repl.printError(
                        "Invalid command. Type " + EscapeSequences.SET_TEXT_COLOR_GREEN + "'help' " +
                                EscapeSequences.SET_TEXT_COLOR_RED + "to see valid commands. Please try again!");
                break;
        }
    }

    // Endpoint Functions
    /**
     * Logic for implementing the 'help' command in the SIGNEDOUT state.
     * Prints all the SIGNEDOUT commands including their correct format.
     * NOTE: some of the 'help' information is the same.
     *       See the printHelp function in the REPL class.
     * @param words: detected user command from the REPL.
     */
    private void helpPreLogin(String[] words) {
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
                %s: to create an account
                      %s
                
                %s: to log into an existing account
                      %s
                """,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "register" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + "register <USERNAME> <PASSWORD> <EMAIL>" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "login" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + "login <USERNAME> <PASSWORD>" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE);
        repl.printHelp(output);
    }
    /**
     * Logic for implementing the 'quit' command in the SIGNEDOUT state
     * Transitions to QUIT state to exit the program
     * @param words: detected user command from the REPL
     */
    private void quit(String[] words) {
        // Check Parameters
        // THIS SHOULD NEVER BE MET if the input words are properly gated
        if((words == null) || (words.length == 0)) {
            repl.printArgErrorSpecific("quit", "quit", "quit",
                    "Invalid command attempted to call the 'help' case.");
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
     * Logic for implementing the 'register' command in the SIGNEDOUT state
     * Transitions to SIGNEDIN state if successful
     * @param words: detected user command from the REPL
     */
    private void registerPreLogin(String[] words) {
        // Check Parameters
        // THIS SHOULD NEVER BE MET if the input words are properly gated
        if((words == null) || (words.length == 0)) {
            repl.printArgErrorSpecific("register", "register <USERNAME> <PASSWORD> <EMAIL>",
                    "register myName new_password myEmail@company.com",
                    "Invalid command attempted to call the 'register' case.");
            return;
        }
        // Check user input: 'register' command takes 3 arguments
        if(words.length != 4) {
            repl.printArgErrorSpecific(words[0], "register <USERNAME> <PASSWORD> <EMAIL>",
                    "register myName new_password myEmail@company.com", "");
            return;
        }
        // Retrieve input arguments
        String username = words[1];
        RegisterRequest register = new RegisterRequest(username, words[2], words[3]);
        try {
            RegisterResult authData = facade.register(register);
            repl.setAuthToken(authData.authToken());
            repl.setState(REPL.State.SIGNEDIN);
            repl.printOutput(String.format("""
                    Successfully Signed in with your credentials!
                    See %s to see available logged in activities.
                    Welcome %s!
                    """, EscapeSequences.SET_TEXT_COLOR_GREEN + "'help'" + EscapeSequences.SET_TEXT_COLOR_BLUE,
                    username));
            // Update gameList if authToken created
            ListGamesResult list = facade.listGames(repl.getAuthToken());
            ArrayList<GameData> games =   ((list == null) || (list.games() == null)) ? new ArrayList<>()
                                        :                                              new ArrayList<>(list.games());

            repl.setGameList(games);
        } catch (ResponseException e) {
            repl.printError(e.getMessage());
        }
    }
    /**
     * Logic for implementing the 'login' command in the SIGNEDOUT state
     * Transitions to SIGNEDIN state if successful
     * @param words: detected user command from the REPL
     */
    private void loginPreLogin(String[] words) {
        // Check Parameters
        // THIS SHOULD NEVER BE MET if the input words are properly gated
        if((words == null) || (words.length == 0)) {
            repl.printArgErrorSpecific("login", "login <USERNAME> <PASSWORD>",
                    "login myName new_password",
                    "Invalid command attempted to call the 'login' case.");
            return;
        }
        // Check user input: 'login' command takes 2 arguments
        if(words.length != 3){
            repl.printArgErrorSpecific(words[0], "login <USERNAME> <PASSWORD>",
                "login myName new_password", "");
            return;
        }
        // Retrieve input arguments
        String username = words[1];
        LoginRequest login = new LoginRequest(username, words[2]);
        try {
            LoginResult authData = facade.login(login);
            repl.setAuthToken(authData.authToken());
            repl.setState(REPL.State.SIGNEDIN);
            repl.printOutput(String.format("""
                    Successfully Logged in with your credentials! Welcome %s!
                    Type %s to see available Logged-In commands.
                    """, username,
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "'help'" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE));
            // Update gameList if authToken created
            ListGamesResult list = facade.listGames(repl.getAuthToken());
            ArrayList<GameData> games =   ((list == null) || (list.games() == null)) ? new ArrayList<>()
                                        :                                              new ArrayList<>(list.games());

            repl.setGameList(games);
        } catch (ResponseException e) {
            repl.printError(e.getMessage());
        }
    }
}