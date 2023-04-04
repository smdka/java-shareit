package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;

import java.util.Collection;

public interface BookingService {
    OutcomingBookingDto add(Long userId, IncomingBookingDto incomingBookingDto);

    OutcomingBookingDto changeStatus(Long bookingId, Boolean approved, Long itemOwnerId);

    OutcomingBookingDto getById(Long bookingId, Long userId);

    Collection<OutcomingBookingDto> getAllByUserId(Long userId, State state, Integer from, Integer size);

    Collection<OutcomingBookingDto> getAllForItemOwnerId(Long itemOwnerId, State state, Integer from, Integer size);
}
