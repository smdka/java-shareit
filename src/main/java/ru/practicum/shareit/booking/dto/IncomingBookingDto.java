package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Value
public class IncomingBookingDto {
    Long id;

    @FutureOrPresent
    LocalDateTime start;

    @Future
    LocalDateTime end;
    Booking.Status status;
    Long bookerId;
    Long itemId;
}
