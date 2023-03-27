package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;

@Component
@AllArgsConstructor
public class BookingsGetter {
    private BookingRepository bookingRepository;

    private final Map<State, BiFunction<Long, Pageable, Collection<Booking>>> forUser =
            Map.of(
                    State.ALL, (bookerId, pageable) -> bookingRepository.findAllByBookerId(bookerId, pageable),
                    State.WAITING, (bookerId, pageable) -> bookingRepository.findAllByBookerIdAndStatus(bookerId, Booking.Status.WAITING, pageable),
                    State.REJECTED, (bookerId, pageable) -> bookingRepository.findAllByBookerIdAndStatus(bookerId, Booking.Status.REJECTED, pageable),
                    State.PAST, (bookerId, pageable) -> bookingRepository.findAllByBookerIdAndEndBefore(bookerId, LocalDateTime.now(), pageable),
                    State.FUTURE, (bookerId, pageable) -> bookingRepository.findAllByBookerIdAndStartAfter(bookerId, LocalDateTime.now(), pageable),
                    State.CURRENT, (bookerId, pageable) -> bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(bookerId, LocalDateTime.now(), LocalDateTime.now(), pageable)
            );

    private final Map<State, BiFunction<Collection<Long>, Pageable,Collection<Booking>>> forItemOwner =
            Map.of(
                    State.ALL, (ids, pageable) -> bookingRepository.findAllByItemIdIn(ids, pageable),
                    State.WAITING, (ids, pageable) -> bookingRepository.findAllByItemIdInAndStatus(ids, Booking.Status.WAITING, pageable),
                    State.REJECTED, (ids, pageable) -> bookingRepository.findAllByItemIdInAndStatus(ids, Booking.Status.REJECTED, pageable),
                    State.PAST, (ids, pageable) -> bookingRepository.findAllByItemIdInAndEndBefore(ids, LocalDateTime.now(), pageable),
                    State.FUTURE, (ids, pageable) -> bookingRepository.findAllByItemIdInAndStartAfter(ids, LocalDateTime.now(), pageable),
                    State.CURRENT, (ids, pageable) -> bookingRepository.findAllByItemIdInAndStartBeforeAndEndAfter(ids, LocalDateTime.now(), LocalDateTime.now(), pageable)
            );

    public Collection<Booking> forUser(long userId, State state, Pageable pageable) {
        return forUser.get(state).apply(userId, pageable);
    }

    public Collection<Booking> forItemOwner(Collection<Long> itemIds, State state, Pageable pageable) {
        return forItemOwner.get(state).apply(itemIds, pageable);
    }
}
