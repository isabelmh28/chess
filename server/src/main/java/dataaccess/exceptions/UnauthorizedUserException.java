/**
 * UnauthorizedUserException Exception Class
 * Indicates an unauthenticated user.
 * Author: Isabel Hinton
 */

package dataaccess.exceptions;

public class UnauthorizedUserException extends Exception {
    public UnauthorizedUserException(String message) {
        super(message);
    }
}
