package clovero.logger;


import clovero.user.User;

import java.util.Map;

public final class CloverMDC {

    private CloverMDC() {
    }

    public static void put(User value) {
        put(CloverLogger.LOGGER_PREFIX + "session_id", value.getId());
        put(CloverLogger.LOGGER_PREFIX + "session_name", value.getName());
    }

    public static void put(String key, String value) {
        if (value != null) {
            org.slf4j.MDC.put(key, value);
        }
    }

    public static void put(String key, Number value) {
        if (value != null) {
            org.slf4j.MDC.put(key, value.toString());
        }
    }

    public static void clear() {
        org.slf4j.MDC.clear();
    }

    public static Map<String, String> getMap() {
        return org.slf4j.MDC.getCopyOfContextMap();
    }
}
