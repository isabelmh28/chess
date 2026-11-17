/**
 * REPL Module
 * CLI Loop (Read-Eval-Print Loop)
 * Uses the classes in the statehandlers package to transition
 * between the SIGNEDOUT and SIGNEDIN states
 * Each state has specific endpoints that can be accessed
 * Author: Isabel Hinton
 */

package client;

import model.GameData;

import ui.*;
import ui.statehandlers.*;

import java.util.Scanner;
import java.util.ArrayList;

/**
 * Manages the Scanner input loop, tracks current user state,
 * processes user commands, and prints user output
 */
public class REPL {
    // Private Members
    private State state;
    private final Scanner scanner;
    private String authToken;
    private ArrayList<GameData> gameList;
    private final PreLogin pre;
    private final PostLogin post;
    // State enum. Specifies user state in the REPL
    public enum State {
        SIGNEDOUT,
        SIGNEDIN,
        QUIT
    }
    // Constructor
    public REPL(int port) {
        state = State.SIGNEDOUT;
        ServerFacade facade = new ServerFacade(port);
        scanner = new Scanner(System.in);
        authToken = null;
        gameList = new ArrayList<>();
        pre = new PreLogin(this, facade);
        post = new PostLogin(this, facade);
    }
    // Getter and Setter Functions
    // Getters
    /**
     * @return user's authToken
     */
    public String getAuthToken() { return authToken;}
    /**
     * @return user's current REPL state
     */
    public State getState() { return state;}
    /**
     * @return list of current games in the database
     */
    public ArrayList<GameData> getGameList() { return gameList;}
    // Setters
    /**
     * Sets the user's authToken to an updated authToken
     * @param authToken updated authToken
     */
    public void setAuthToken(String authToken) { this.authToken = authToken;}
    /**
     * Sets the user's state
     * @param state updated user state
     */
    public void setState(State state) { this.state = state;}
    /**
     * Updates the local list of games with a new list
     * @param gameList updated list of games in the database
     */
    public void setGameList(ArrayList<GameData> gameList) { this.gameList = gameList;}

    // Helper Functions to reduce Duplicate Code
    /**
     * Prints the user state. The user will type their input next to this in the CLI.
     */
    public void printState() {
        System.out.print(
                EscapeSequences.SET_TEXT_COLOR_YELLOW + "[" + state + "] >>> " +
                        EscapeSequences.RESET_TEXT_COLOR);
        System.out.flush();
    }
    /**
     * Prints a basic output in blue.
     * @param output the desired string to be printed.
     */
    public void printOutput(String output) {
        // Check Parameters
        String text = (output == null) ? "" : output;

        System.out.print(
                EscapeSequences.SET_TEXT_COLOR_BLUE + text + EscapeSequences.RESET_TEXT_COLOR + "\n");
    }
    /**
     * Prints an error in red.
     * @param message error message to be printed.
     */
    public void printError(String message) {
        // Check Parameters
        String text = (message == null) ? "" : message;

        printOutput(
                EscapeSequences.SET_TEXT_COLOR_RED + text + EscapeSequences.RESET_TEXT_COLOR);
    }
    /**
     * Prints the help menu. The beginning is common to both active user states.
     * @param body These are state specific endpoints to be printed in the help menu
     */
    public void printHelp(String body) {
        // Check Parameters
        String text = (body == null) ? "" : body;

        String message = String.format("""
                Below are the available commands.
                %s: misspelling a command or providing the incorrect input will require a new command.
                      The angle brackets <> contain arguments that you will need to provide for that command.
                      Do not include the brackets when you provide your information.
                
                %s: list possible commands
                
                %s: exit the game
                
                %s""", EscapeSequences.SET_TEXT_COLOR_GREEN + "NOTE" +
                EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "help" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                EscapeSequences.SET_TEXT_COLOR_GREEN + "quit" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                text);
        printOutput(message);
    }
    /**
     * Prints a specific error message for user input where a command takes no inputs
     * @param command command the user was attempting to call.
     */
    public void printArgErrorNoArgs(String command) {
        // Check Parameters
        String text = (command == null) ? "unknown" : command;
        printError(String.format("The " + EscapeSequences.SET_TEXT_COLOR_GREEN +
                "'" + command + "'" + EscapeSequences.SET_TEXT_COLOR_RED +
                " command does not take arguments. Please try again!\n"));
    }
    /**
     * Prints a specific error message for user input where a command was given
     * the incorrect arguments.
     * Output prints the command, the correct format, and an example. Also prints
     * an additional message if desired.
     * @param command command the user was attempting to call
     * @param format  correct command setup with arguments
     * @param example example of a correct command
     * @param message additional message for helping the user correctly call the command
     */
    public void printArgErrorSpecific(String command, String format, String example, String message) {
        // Check Parameters
        String com  = (command == null) ? "unknown" : command;
        String form = (format == null)  ? "" : format;
        String ex   = (example == null) ? "" : example;
        String mes  = (message == null) ? "" : message;

        printError(String.format("""
                %s command must be in the following format:
                %s
                EX: %s
                %s
                Please try again!
                """,
                EscapeSequences.SET_TEXT_COLOR_GREEN + com +
                        EscapeSequences.SET_TEXT_COLOR_RED,
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + form +
                        EscapeSequences.SET_TEXT_COLOR_RED,
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + ex +
                        EscapeSequences.SET_TEXT_COLOR_RED,
                mes));
    }

    // REPL Functions
    /**
     * REPL that implements the Client CLI logic.
     * Transitions between user states in a loop, which exits on quit.
     * See statehandlers for the SIGNEDOUT and SIGNEDIN logic and endpoints
     * The logic in statehandlers can transition the user state to QUIT
     */
    public void run() {
        // Welcome Message
        printOutput(
                "Welcome to Chess! Type " + EscapeSequences.SET_TEXT_COLOR_GREEN + "'help' " +
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "to get started.");
        // Loop
        while(state != State.QUIT) {
            printState();
            // Get user input
            if(!scanner.hasNextLine()) {
                break; // exit if stream closes
            }
            String input = scanner.nextLine().trim();
            if(input.isEmpty()) {
                continue;
            }
            if(state == State.SIGNEDOUT) {
                pre.handlePreLogin(input);
            }
            else if(state == State.SIGNEDIN) {
                post.handlePostLogin(input);
            }
        }
        // Exit Message
        printOutput(
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + "Input stream closed. Have a good day!\n" +
                        EscapeSequences.RESET_TEXT_COLOR);
    }
}
