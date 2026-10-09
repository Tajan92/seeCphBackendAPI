package app.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ApiException extends RuntimeException {
    private int code;

    public ApiException(int code, String msg){
        super(msg);
        this.code = code;
        log.error("ApiException (code={}): {}", code, msg);
    }
}