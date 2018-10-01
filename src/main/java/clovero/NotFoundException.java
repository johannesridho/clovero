package clovero;

import java.util.Locale;

public class NotFoundException extends CloverException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Class clazz, String entityId) {
        this(clazz.getSimpleName(), entityId);
    }

    public NotFoundException(String entityName, String entityId) {
        super(String.format(Locale.ENGLISH, "%s with Id %s is not found.", entityName, entityId));
    }

    @Override
    public String getErrorCode() {
        return "not_found";
    }
}
