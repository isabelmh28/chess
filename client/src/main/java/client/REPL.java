package client;

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


    public REPL(int port) {
        state = State.SIGNEDOUT;
        facade = new ServerFacade(port);
        scanner = new Scanner(System.in);
        authToken = null;
    }

}
