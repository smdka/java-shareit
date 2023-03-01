package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Long> {
    Collection<Booking> findAllByItemIdAndBookerId(Long itemId, Long bookerId);
    Collection<Booking> findAllByBookerId(Long bookerId, Sort sort);
    Collection<Booking> findAllByBookerIdAndStatus(Long bookerId, Booking.Status status, Sort sort);
    Collection<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime ldt, Sort sort);
    Collection<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime ldt, Sort sort);
    Collection<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime now, LocalDateTime now1, Sort sort);
    Collection<Booking> findAllByItemIdIn(Collection<Long> itemIds, Sort sort);
    Collection<Booking> findAllByItemIdInAndStatus(Collection<Long> ids, Booking.Status status, Sort sort);
    Collection<Booking> findAllByItemIdInAndEndBefore(Collection<Long> ids, LocalDateTime now, Sort sort);
    Collection<Booking> findAllByItemIdInAndStartAfter(Collection<Long> ids, LocalDateTime now, Sort sort);
    Collection<Booking> findAllByItemIdInAndStartBeforeAndEndAfter(Collection<Long> ids, LocalDateTime now, LocalDateTime now1, Sort sort);
}
