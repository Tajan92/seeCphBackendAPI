package app.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class SecurityValidationException extends RuntimeException {
    private int code;

    public SecurityValidationException(int code, String msg){
        super(msg);
        this.code = code;
        log.error("SecurityException (code={}): {}", code, msg);
    }
}
