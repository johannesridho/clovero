package clovero.logger;

import org.slf4j.LoggerFactory;

public final class CloverLoggerFactory {

    private CloverLoggerFactory() {
    }

    public static CloverLogger getCloverLogger(Class clazz) {
        return new CloverLogger(LoggerFactory.getLogger(clazz));
    }
}
