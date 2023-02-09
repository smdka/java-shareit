package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.user.exception.UserEmailAlreadyExist;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handleEmailConflict(final UserEmailAlreadyExist e) {
        log.warn(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleUserNotFound(final UserNotFoundException e) {
        log.warn(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleValidationErrors(final ValidationException e) {
        log.warn(e.getMessage());
    }
}
