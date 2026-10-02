package app.exceptions;

public class DatabaseIdException extends RuntimeException {
    public DatabaseIdException(String message) {
        super(message);
    }

    public DatabaseIdException(String message, Throwable cause) {
        super(message, cause);
    }
}