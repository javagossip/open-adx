package top.openadexchange.commons.exception;

public class OaxException extends RuntimeException {

    public OaxException(String message) {
        super(message);
    }

    public OaxException(Throwable cause) {
        super(cause);
    }

    public OaxException(String message, Throwable cause) {
        super(message, cause);
    }
}
