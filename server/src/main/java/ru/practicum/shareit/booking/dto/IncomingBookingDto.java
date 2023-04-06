package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

@Value
public class IncomingBookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Booking.Status status;
    Long bookerId;
    Long itemId;
}
