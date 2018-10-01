package clovero.logger;

import clovero.util.JsonUtils;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.Marker;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CloverLogger {

    public static final String LOGGER_PREFIX = "clover_";

    private final Logger logger;

    public CloverLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * <p>Arguments should be in format {@code 'key=value'}.
     * Example: {@code 'foo=123'}</p>
     *
     * @param format
     * @param transactionAmount
     * @param arguments
     */
    public void infoCompletedProcess(String format, BigDecimal transactionAmount, String... arguments) {
        final Map values = new HashMap();

        if (MDC.getCopyOfContextMap() != null) {
            values.putAll(MDC.getCopyOfContextMap());
        }

        values.put(LOGGER_PREFIX + "transaction_amount", transactionAmount.abs());
        for (String argument : arguments) {
            final String tmps[] = argument.split("=");
            if (tmps.length > 1) {
                values.put(LOGGER_PREFIX + tmps[0], tmps[1]);
            }
        }

        logger.info(Markers.appendEntries(values), format);
    }

    public void info(String format, Object... arguments) {
        logger.info(getMDCOnly(), format, toJson(arguments));
    }

    public void error(String format, Throwable throwable) {
        logger.error(getMDCOnly(), format, throwable);
    }

    public void error(String format, Object... arguments) {
        logger.error(getMDCOnly(), format, toJson(arguments));
    }

    public void debug(String format, Object... arguments) {
        logger.debug(getMDCOnly(), format, toJson(arguments));
    }

    public void warn(String format, Object... arguments) {
        logger.warn(getMDCOnly(), format, toJson(arguments));
    }

    private Object[] toJson(Object[] objects) {
        return Arrays.stream(objects).map(JsonUtils::toJson).toArray();
    }

    private Marker getMDCOnly() {
        final Map values = new HashMap();

        if (MDC.getCopyOfContextMap() != null) {
            values.putAll(MDC.getCopyOfContextMap());
        }

        return Markers.appendEntries(values);
    }
}
