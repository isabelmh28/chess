package client;

import model.GameData;
import ui.*;
import ui.statehandlers.*;

import java.util.ArrayList;
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
    private ArrayList<GameData> gameList;
    private final PreLogin pre;
    private final PostLogin post;

    public REPL(int port) {
        state = State.SIGNEDOUT;
        facade = new ServerFacade(port);
        scanner = new Scanner(System.in);
        authToken = null;
        gameList = new ArrayList<>();
        pre = new PreLogin(this, facade);
        post = new PostLogin(this, facade);
    }

    // Getters
    public String getAuthToken() { return authToken;}
    public State getState() { return state;}
    public ArrayList<GameData> getGameList() { return gameList;}
    // Setters
    public void setAuthToken(String authToken) { this.authToken = authToken;}
    public void setState(State state) { this.state = state;}
    public void setGameList(ArrayList<GameData> gameList) { this.gameList = gameList;}

    public void run() {
        printOutput(
                "Welcome to Chess! Type " + EscapeSequences.SET_TEXT_COLOR_GREEN + "'help' " +
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
                pre.handlePreLogin(input);
            }
            else if(state == State.SIGNEDIN) {
                post.handlePostLogin(input);
            }
        }
        printOutput(
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + "Input stream closed. Have a good day!\n" +
                EscapeSequences.RESET_TEXT_COLOR);
    }

    public void printState() {
        System.out.print(
                EscapeSequences.SET_TEXT_COLOR_YELLOW + "[" + state + "] >>> " +
                        EscapeSequences.RESET_TEXT_COLOR);
        System.out.flush();
    }

    public void printOutput(String output) {
        System.out.print(
                EscapeSequences.SET_TEXT_COLOR_BLUE + output + EscapeSequences.RESET_TEXT_COLOR + "\n");
    }

    public void printError(String message) {
        printOutput(
                EscapeSequences.SET_TEXT_COLOR_RED + message + EscapeSequences.RESET_TEXT_COLOR);
    }

    public void printHelp(String body) {
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
                body);
        printOutput(message);
    }

    public void printArgErrorNoArgs(String[] words) {
        printError(String.format("The " + EscapeSequences.SET_TEXT_COLOR_GREEN +
                "'" + words[0] + "'" + EscapeSequences.SET_TEXT_COLOR_RED +
                " command does not take arguments. Please try again!"));
    }

    public void printArgErrorSpecific(String[] words, String format, String example, String message) {
        printError(String.format("""
                %s command must be in the following format:
                %s
                EX: %s
                %s
                Please try again!
                """,
                EscapeSequences.SET_TEXT_COLOR_GREEN + words[0] +
                        EscapeSequences.SET_TEXT_COLOR_RED,
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + format +
                        EscapeSequences.SET_TEXT_COLOR_RED,
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + example +
                        EscapeSequences.SET_TEXT_COLOR_RED,
                message));
    }
}
