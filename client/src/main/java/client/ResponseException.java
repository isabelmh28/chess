package client;

public class ResponseException extends RuntimeException {
    private final int statusCode;
    public ResponseException(int code, String message) {
        statusCode = code;
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
