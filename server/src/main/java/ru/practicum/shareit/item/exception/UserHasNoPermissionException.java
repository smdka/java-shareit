package ru.practicum.shareit.item.exception;

public class UserHasNoPermissionException extends RuntimeException {
    public UserHasNoPermissionException(String message) {
        super(message);
    }
}
