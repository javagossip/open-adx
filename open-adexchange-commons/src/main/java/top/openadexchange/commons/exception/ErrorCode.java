package top.openadexchange.commons.exception;

public interface ErrorCode {

    String getCode();

    String getMessage();

    default ValidateException toException() {
        throw new ValidateException(getCode(), getMessage());
    }
}
