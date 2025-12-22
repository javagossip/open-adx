package top.openadexchange.commons.exception;

import lombok.Getter;

@Getter
public class ValidateException extends RuntimeException {

    private int code;

    public ValidateException(int code, String message) {
        super(message);
        this.code = code;
    }
}
