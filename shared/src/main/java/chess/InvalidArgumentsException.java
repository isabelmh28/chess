/**
 * InvalidMoveException Exception Class
 * Indicates an invalid argument provided to a function.
 * Provides the function and Class.
 * Author: Isabel Hinton
 */

package chess;

public class InvalidArgumentsException extends RuntimeException {
    public InvalidArgumentsException(String message) {
        super(message + " was given an invalid argument");
    }
}