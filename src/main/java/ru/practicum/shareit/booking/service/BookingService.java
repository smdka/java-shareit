package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.repository.State;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {
    Booking add(Long userId, Booking toBooking);

    Booking changeStatus(Long bookingId, Boolean approved, Long userId);

    Booking getById(Long bookingId, Long userId);

    Collection<Booking> getAllByUserId(Long userId, State state);

    Collection<Booking> getAllForItemOwnerId(Long itemOwnerId, State state);
}
