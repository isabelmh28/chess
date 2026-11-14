package ui.statehandlers;

import client.REPL;
import client.ResponseException;
import client.ServerFacade;
import ui.EscapeSequences;
import model.GameData;
import model.transactions.request.LoginRequest;
import model.transactions.request.RegisterRequest;
import model.transactions.result.ListGamesResult;

import java.util.ArrayList;

public class PreLogin {
    private final REPL repl;
    private final ServerFacade facade;

    public PreLogin(REPL repl, ServerFacade facade) {
        this.repl = repl;
        this.facade = facade;
    }

    public void handlePreLogin(String input) {
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
                repl.printError(
                        "Invalid command. Type " + EscapeSequences.SET_TEXT_COLOR_GREEN + "'help' " +
                                EscapeSequences.SET_TEXT_COLOR_RED + "to see valid commands. Please try again!");
                break;
        }
    }

    private void helpPreLogin(String[] words) {
        if(words.length != 1) {
            repl.printArgErrorNoArgs(words);
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

    private void registerPreLogin(String[] words) {
        if(words.length != 4) {
            repl.printArgErrorSpecific(words, "register <USERNAME> <PASSWORD> <EMAIL>",
                    "register myName new_password myEmail@company.com", "");
            return;
        }
        String username = words[1];
        RegisterRequest register = new RegisterRequest(username, words[2], words[3]);
        try {
            var authData = facade.register(register);
            repl.setAuthToken(authData.authToken());
            repl.setState(REPL.State.SIGNEDIN);
            repl.printOutput(String.format("""
                    Successfully Signed in with your credentials!
                    See %s to see available logged in activities.
                    Welcome %s!
                    """, EscapeSequences.SET_TEXT_COLOR_GREEN + "'help" + EscapeSequences.SET_TEXT_COLOR_BLUE,
                    username));
        } catch (ResponseException e) {
            repl.printError(e.getMessage());
        }
    }

    private void loginPreLogin(String[] words) {
        if(words.length != 3){
            repl.printArgErrorSpecific(words, "login <USERNAME> <PASSWORD>",
                "login myName new_password", "");
            return;
        }
        String username = words[1];
        LoginRequest login = new LoginRequest(username, words[2]);
        try {
            var authData = facade.login(login);
            repl.setAuthToken(authData.authToken());
            repl.setState(REPL.State.SIGNEDIN);
            repl.printOutput(String.format("""
                    Successfully Logged in with your credentials! Welcome %s!
                    Type %s to see available Logged-In commands.
                    """, username,
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "'help" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE));
            ListGamesResult list = facade.listGames(repl.getAuthToken());
            ArrayList<GameData> gameList = repl.getGameList();
            gameList.clear();
            gameList.addAll(list.games());
            repl.setGameList(gameList);
        } catch (ResponseException e) {
            repl.printError(e.getMessage());
        }
    }
}