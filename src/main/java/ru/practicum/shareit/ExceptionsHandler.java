package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserHasNoPermissionException;
import ru.practicum.shareit.user.exception.UserEmailAlreadyExist;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import javax.validation.ValidationException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<HttpStatus, String> handleEmailConflict(final UserEmailAlreadyExist e) {
        log.warn(e.getMessage());
        return Map.of(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<HttpStatus, String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        return Map.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<HttpStatus, String> handleValidationExceptions(final ValidationException e) {
        log.warn(e.getMessage());
        return Map.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<HttpStatus, String> handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn(e.getMessage());
        return Map.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalStateException(final IllegalStateException e) {
        log.warn(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<HttpStatus, String> handleItemAvailabilityExceptions(final ItemNotAvailableException e) {
        log.warn(e.getMessage());
        return Map.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<HttpStatus, String> handleUserNotFound(final UserNotFoundException e) {
        log.warn(e.getMessage());
        return Map.of(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<HttpStatus, String> handleItemNotFound(final ItemNotFoundException e) {
        log.warn(e.getMessage());
        return Map.of(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<HttpStatus, String> handleBookingNotFound(final BookingNotFoundException e) {
        log.warn(e.getMessage());
        return Map.of(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<HttpStatus, String> handleNoPermission(final UserHasNoPermissionException e) {
        log.warn(e.getMessage());
        return Map.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<HttpStatus, String> handleException(final Exception e) {
        log.warn(e.getMessage());
        return Map.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
