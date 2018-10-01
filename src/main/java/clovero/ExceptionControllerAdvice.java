package clovero;

import clovero.logger.CloverLogger;
import clovero.logger.CloverLoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {

    private static final CloverLogger logger = CloverLoggerFactory.getCloverLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CloverException> handleNotFoundException(NotFoundException exc) {
        logger.error("Not found exception", exc);
        return new ResponseEntity<>(exc, HttpStatus.NOT_FOUND);
    }

}
