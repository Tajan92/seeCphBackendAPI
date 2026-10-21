package app.exceptions;

import lombok.Getter;

@Getter
public class SyncException extends RuntimeException {
    private final int code;

    public SyncException(int code, String message) {
        super(message);
        this.code = code;
    }

    public SyncException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}