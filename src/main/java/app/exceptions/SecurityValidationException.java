package app.exceptions;

import lombok.Getter;

@Getter
public class SecurityValidationException extends RuntimeException {
    private final int code;

    public SecurityValidationException(int code, String msg){
        super(msg);
        this.code = code;
    }
}
