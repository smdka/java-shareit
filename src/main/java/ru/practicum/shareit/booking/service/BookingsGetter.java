package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class BookingsGetter {
    private static final Sort SORT_BY_START = Sort.by(Sort.Direction.DESC, "start");

    private BookingRepository bookingRepository;

    private final Map<State, Function<Long, Collection<Booking>>> forUser =
            Map.of(
                    State.ALL, bookerId -> bookingRepository.findAllByBookerId(bookerId, SORT_BY_START),
                    State.WAITING, bookerId -> bookingRepository.findAllByBookerIdAndStatus(bookerId, Booking.Status.WAITING, SORT_BY_START),
                    State.REJECTED, bookerId -> bookingRepository.findAllByBookerIdAndStatus(bookerId, Booking.Status.REJECTED, SORT_BY_START),
                    State.PAST, bookerId -> bookingRepository.findAllByBookerIdAndEndBefore(bookerId, LocalDateTime.now(), SORT_BY_START),
                    State.FUTURE, bookerId -> bookingRepository.findAllByBookerIdAndStartAfter(bookerId, LocalDateTime.now(), SORT_BY_START),
                    State.CURRENT, bookerId -> bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(bookerId, LocalDateTime.now(), LocalDateTime.now(), SORT_BY_START)
            );

    private final Map<State, Function<Collection<Long>, Collection<Booking>>> forItemOwner =
            Map.of(
                    State.ALL, ids -> bookingRepository.findAllByItemIdIn(ids, SORT_BY_START),
                    State.WAITING, ids -> bookingRepository.findAllByItemIdInAndStatus(ids, Booking.Status.WAITING, SORT_BY_START),
                    State.REJECTED, ids -> bookingRepository.findAllByItemIdInAndStatus(ids, Booking.Status.REJECTED, SORT_BY_START),
                    State.PAST, ids -> bookingRepository.findAllByItemIdInAndEndBefore(ids, LocalDateTime.now(), SORT_BY_START),
                    State.FUTURE, ids -> bookingRepository.findAllByItemIdInAndStartAfter(ids, LocalDateTime.now(), SORT_BY_START),
                    State.CURRENT, ids -> bookingRepository.findAllByItemIdInAndStartBeforeAndEndAfter(ids, LocalDateTime.now(), LocalDateTime.now(), SORT_BY_START)
            );

    public Collection<Booking> forUser(long userId, State state) {
        return forUser.get(state).apply(userId);
    }

    public Collection<Booking> forItemOwner(Collection<Long> itemIds, State state) {
        return forItemOwner.get(state).apply(itemIds);
    }
}
