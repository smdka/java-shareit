package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.booking.validate.ValidDates;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Value
@ValidDates
public class IncomingBookingDto {
    Long id;

    @FutureOrPresent
    LocalDateTime start;

    @Future
    LocalDateTime end;

    Status status;
    Long bookerId;
    Long itemId;
}
