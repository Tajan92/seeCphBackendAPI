package app.exceptions;

public class SyncException extends RuntimeException {
    public SyncException(String message, Throwable cause) {
        super(message, cause);
    }
}