/**
 * AlreadyTakenException Exception Class
 * Indicates a user attempted to join a game in a role that was already filled.
 * Author: Isabel Hinton
 */

package dataaccess.exceptions;

public class AlreadyTakenException extends Exception {
    public AlreadyTakenException(String message) {
        super(message);
    }
}
