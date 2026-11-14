package chess;

public class InvalidArgumentsException extends RuntimeException {
    public InvalidArgumentsException(String message) {
        super(message + " was given an invalid argument");
    }
}