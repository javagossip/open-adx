package top.openadexchange.commons.exception;

import lombok.Getter;

@Getter
public class ValidateException extends RuntimeException {

    private String code;

    public ValidateException(String code, String message) {
        super(message);
        this.code = code;
    }
}
