package ru.practicum.shareit.booking.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String s) {
        super(s);
    }
}
