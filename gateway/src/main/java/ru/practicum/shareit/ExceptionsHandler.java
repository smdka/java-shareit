package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalStateException(final IllegalStateException e) {
        log.warn(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<HttpStatus, String> handleConstraintViolationException(final ConstraintViolationException e) {
        log.warn(e.getMessage());
        return Map.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
