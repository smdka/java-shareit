package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.dto.UserDtoForBooking;

import java.time.LocalDateTime;

@Value
public class OutcomingBookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Booking.Status status;
    UserDtoForBooking booker;
    ItemDtoForBooking item;
}
