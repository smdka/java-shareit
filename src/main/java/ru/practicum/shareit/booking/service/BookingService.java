package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.repository.State;

import java.util.Collection;

public interface BookingService {
    OutcomingBookingDto add(Long userId, IncomingBookingDto incomingBookingDto);

    OutcomingBookingDto changeStatus(Long bookingId, Boolean approved, Long userId);

    OutcomingBookingDto getById(Long bookingId, Long userId);

    Collection<OutcomingBookingDto> getAllByUserId(Long userId, State state);

    Collection<OutcomingBookingDto> getAllForItemOwnerId(Long itemOwnerId, State state);
}
