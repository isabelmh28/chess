/**
 * TokenNotFoundException Exception Class
 * Indicates an authToken that was either invalid or not found in the database.
 * Author: Isabel Hinton
 */

package dataaccess.exceptions;

public class TokenNotFoundException extends Exception {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
