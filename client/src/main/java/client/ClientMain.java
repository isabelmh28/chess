/**
 * ClientMain Module
 * Instances REPL and connects to the running ServerMain
 * Make sure to start your ServerMain before attempting to run Client
 * Author: Isabel Hinton
 */

package client;

import ui.EscapeSequences;

import chess.*;
import model.transactions.result.*;
import model.transactions.request.*;

public class ClientMain {

    /**
     * Welcomes the user and gets the port from the argument in main.
     * Runs the REPL on the retrieved port.
     */
    public static void main(String[] args) {
        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_YELLOW + "♕" +
                        EscapeSequences.SET_TEXT_COLOR_MAGENTA + " 240 Chess Client: Welcome!\n" +
                        EscapeSequences.RESET_TEXT_COLOR);

        int port = 8080;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        REPL repl = new REPL(port);
        repl.run();
    }
}