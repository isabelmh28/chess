/**
 * InvalidRequestException Exception Class
 * Indicates an invalid request from the user.
 * Author: Isabel Hinton
 */

package dataaccess.exceptions;

public class InvalidRequestException extends Exception {
    public InvalidRequestException(String message) {
        super(message);
    }
}
