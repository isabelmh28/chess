/**
 * DataAccessException Exception Class
 * Indicates a connection or instruction issue with the database.
 * Author: Isabel Hinton
 */

package dataaccess.exceptions;

public class DataAccessException extends Exception{
    public DataAccessException(String message) {
        super(message);
    }
    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
    }
}
