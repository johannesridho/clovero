package clovero.util;

import clovero.configuration.CustomJackson2ObjectMapperBuilder;
import clovero.logger.CloverLogger;
import clovero.logger.CloverLoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class JsonUtils {

    private static final CloverLogger logger = CloverLoggerFactory.getCloverLogger(JsonUtils.class);

    private static final ObjectMapper objectMapper = new CustomJackson2ObjectMapperBuilder().build();

    private JsonUtils() {}

    public static String toJson(final Object src) {
        try {
            return objectMapper.writeValueAsString(src);
        } catch (final JsonProcessingException e) {
            logger.error("exception when executing toJson with src={} exception={}", src, e.getMessage(), e);
            return "";
        }
    }

    public static <T> T fromJson(final String json, Class<T> theClass) throws IOException {
        return objectMapper.readValue(json, theClass);
    }
}
