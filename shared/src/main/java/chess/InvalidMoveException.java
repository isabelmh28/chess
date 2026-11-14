/**
 * InvalidMoveException Exception Class
 * Indicates an invalid move was made in a game.
 * Author: Isabel Hinton
 */

package chess;

public class InvalidMoveException extends Exception {
    public InvalidMoveException(String message) {
        super(message);
    }
}
