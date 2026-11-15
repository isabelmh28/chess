/**
 * ResponseException Exception Class
 * Indicates an Http error in the ServerFacade
 * Author: Isabel Hinton
 */

package client;

public class ResponseException extends RuntimeException {
    // Private Members
    private final int statusCode;
    // Constructors
    public ResponseException(int code, String message) {
        super(message);
        statusCode = code;
    }

    // Getter Functions
    public int getStatusCode() {
        return statusCode;
    }
}
