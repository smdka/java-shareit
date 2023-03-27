package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Long>, Serializable {
    List<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, Booking.Status status, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime ldt, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime ldt, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime now, LocalDateTime now1, Pageable pageable);

    List<Booking> findAllByItemIdIn(Collection<Long> itemIds, Pageable pageable);

    List<Booking> findAllByItemIdInAndStatus(Collection<Long> ids, Booking.Status status, Pageable pageable);

    List<Booking> findAllByItemIdInAndEndBefore(Collection<Long> ids, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemIdInAndStartAfter(Collection<Long> ids, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemIdInAndStartBeforeAndEndAfter(Collection<Long> ids, LocalDateTime now, LocalDateTime now1, Pageable pageable);
}
