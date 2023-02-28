package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.repository.State;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class Strategy {
    private static final Sort SORT_BY_END = Sort.by(Sort.Direction.DESC, "end");

    private BookingRepository bookingRepository;

    private final Map<State, Function<Long, Collection<Booking>>> forUser =
            Map.of(
                    State.ALL, bookerId -> bookingRepository.findAllByBookerId(bookerId, SORT_BY_END),
                    State.WAITING, bookerId -> bookingRepository.findAllByBookerIdAndStatus(bookerId, Booking.Status.WAITING, SORT_BY_END),
                    State.REJECTED, bookerId -> bookingRepository.findAllByBookerIdAndStatus(bookerId, Booking.Status.REJECTED, SORT_BY_END),
                    State.PAST, bookerId -> bookingRepository.findAllByBookerIdAndEndBefore(bookerId, LocalDateTime.now(), SORT_BY_END),
                    State.FUTURE, bookerId -> bookingRepository.findAllByBookerIdAndStartAfter(bookerId, LocalDateTime.now(), SORT_BY_END),
                    State.CURRENT, bookerId -> bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(bookerId, LocalDateTime.now(), LocalDateTime.now(), SORT_BY_END)
            );

    private final Map<State, Function<Collection<Long>, Collection<Booking>>> forItemOwner =
            Map.of(
                    State.ALL, ids -> bookingRepository.findAllByItemIdIn(ids, SORT_BY_END),
                    State.WAITING, ids -> bookingRepository.findAllByItemIdInAndStatus(ids, Booking.Status.WAITING, SORT_BY_END),
                    State.REJECTED, ids -> bookingRepository.findAllByItemIdInAndStatus(ids, Booking.Status.REJECTED, SORT_BY_END),
                    State.PAST, ids -> bookingRepository.findAllByItemIdInAndEndBefore(ids, LocalDateTime.now(), SORT_BY_END),
                    State.FUTURE, ids -> bookingRepository.findAllByItemIdInAndStartAfter(ids, LocalDateTime.now(), SORT_BY_END),
                    State.CURRENT, ids -> bookingRepository.findAllByItemIdInAndStartBeforeAndEndAfter(ids, LocalDateTime.now(), LocalDateTime.now(), SORT_BY_END)
            );

    public Collection<Booking> forUser(long userId, State state) {
        return forUser.get(state).apply(userId);
    }

    public Collection<Booking> forItemOwner(Collection<Long> itemIds, State state) {
        return forItemOwner.get(state).apply(itemIds);
    }
}
