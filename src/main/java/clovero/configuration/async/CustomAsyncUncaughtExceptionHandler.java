package clovero.configuration.async;

import clovero.logger.CloverLogger;
import clovero.logger.CloverLoggerFactory;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;

import java.lang.reflect.Method;

public class CustomAsyncUncaughtExceptionHandler extends SimpleAsyncUncaughtExceptionHandler {

    private static final CloverLogger logger = CloverLoggerFactory.getCloverLogger(CustomAsyncUncaughtExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        if (ex instanceof HttpStatusCodeException) {
            final HttpStatusCodeException httpEx = (HttpStatusCodeException) ex;
            logger.error(httpEx.getResponseBodyAsString(), ex);
        } else {
            super.handleUncaughtException(ex, method, params);
        }
    }
}
