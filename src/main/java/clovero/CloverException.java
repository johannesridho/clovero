package clovero;

import java.util.HashMap;
import java.util.Map;

public abstract class CloverException extends RuntimeException {

    protected final Map<String, String> errorParams = new HashMap<>();

    public CloverException() {
    }

    public CloverException(String message) {
        super(message);
    }

    public CloverException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloverException(Throwable cause) {
        super(cause);
    }

    public CloverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public Map<String, String> getErrorParams() {
        return errorParams;
    }

    public abstract String getErrorCode();
}
