package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exception.ItemNotAvailableException;
import ru.practicum.shareit.booking.repository.State;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserHasNoPermissionException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private static final String USER_NOT_FOUND_MSG = "Пользователь с id = %d не найден";
    private static final String ITEM_NOT_AVAILABLE_MSG = "Предмет с id = %d не доступен";
    private static final String BOOKING_NOT_FOUND_MSG = "Бронирование с id = %d не найдено";
    private static final String ITEM_NOT_FOUND_MSG = "Вещь с id = %d не найдена";
    private static final String NO_PERMISSION_MSG =
            "У пользователя с id = %d нет прав на изменение/получение бронирования с id = %d";
    private static final String BOOKING_DATETIME_ERROR_MSG =
            "Время окончания бронирования не может быть раньше начала бронирования";
    private static final Sort SORT_BY_END = Sort.by(Sort.Direction.DESC, "end");
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Booking add(Long bookerId, Booking booking) {
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException(BOOKING_DATETIME_ERROR_MSG);
        }

        User booker = userRepository.findById(bookerId).orElseThrow(
                () -> new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, bookerId)));

        Long itemId = booking.getItem().getId();
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MSG, itemId)));

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(String.format(ITEM_NOT_AVAILABLE_MSG, itemId));
        }

        Booking savedBooking = bookingRepository.save(booking);

        savedBooking.setBooker(booker);
        savedBooking.setItem(item);
        return savedBooking;
    }

    @Override
    public Booking changeStatus(Long bookingId, Boolean approved, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId));
        }

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException(String.format(BOOKING_NOT_FOUND_MSG, bookingId)));

        if (booking.getStatus().equals(Booking.Status.APPROVED)) {
            throw new IllegalArgumentException("Нельзя изменить статус с APPROVED");
        }

        Item item = booking.getItem();
        Long ownerId = item.getOwner().getId();

        if (!userId.equals(ownerId)) {
            throw new UserHasNoPermissionException(String.format(NO_PERMISSION_MSG, userId, item.getId()));
        }
        if (approved) {
            booking.setStatus(Booking.Status.APPROVED);
        } else {
            booking.setStatus(Booking.Status.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getById(Long bookingId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId));
        }

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException(String.format(BOOKING_NOT_FOUND_MSG, bookingId)));
        Long bookerId = booking.getBooker().getId();
        Long itemOwnerId = booking.getItem().getOwner().getId();
        if (bookerId.equals(userId) || itemOwnerId.equals(userId)) {
            return booking;
        }
        throw new UserHasNoPermissionException(String.format(NO_PERMISSION_MSG, userId, bookingId));
    }

    @Override
    public Collection<Booking> getAllByUserId(Long userId, State state) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId));
        }
        Map<State, Function<Long, Collection<Booking>>> suppliers =
                Map.of(
                        State.ALL, bookerId -> bookingRepository.findAllByBookerId(bookerId, SORT_BY_END),
                        State.WAITING, bookerId -> bookingRepository.findAllByBookerIdAndStatus(bookerId, Booking.Status.WAITING, SORT_BY_END),
                        State.REJECTED, bookerId -> bookingRepository.findAllByBookerIdAndStatus(bookerId, Booking.Status.REJECTED, SORT_BY_END),
                        State.PAST, bookerId -> bookingRepository.findAllByBookerIdAndEndBefore(bookerId, LocalDateTime.now(), SORT_BY_END),
                        State.FUTURE, bookerId -> bookingRepository.findAllByBookerIdAndStartAfter(bookerId, LocalDateTime.now(), SORT_BY_END),
                        State.CURRENT, bookerId -> bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(bookerId, LocalDateTime.now(), LocalDateTime.now(), SORT_BY_END)
                );
        return suppliers.get(state).apply(userId);
    }

    @Override
    public Collection<Booking> getAllForItemOwnerId(Long itemOwnerId, State state) {
        if (!userRepository.existsById(itemOwnerId)) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, itemOwnerId));
        }
        Collection<Long> itemIds = itemRepository.findIdsByOwnerId(itemOwnerId);
        Map<State, Function<Collection<Long>, Collection<Booking>>> suppliers =
                Map.of(
                        State.ALL, ids -> bookingRepository.findAllByItemIdIn(itemIds, SORT_BY_END),
                        State.WAITING, ids -> bookingRepository.findAllByItemIdInAndStatus(ids, Booking.Status.WAITING, SORT_BY_END),
                        State.REJECTED, ids -> bookingRepository.findAllByItemIdInAndStatus(ids, Booking.Status.REJECTED, SORT_BY_END),
                        State.PAST, ids -> bookingRepository.findAllByItemIdInAndEndBefore(ids, LocalDateTime.now(), SORT_BY_END),
                        State.FUTURE, ids -> bookingRepository.findAllByItemIdInAndStartAfter(ids, LocalDateTime.now(), SORT_BY_END),
                        State.CURRENT, ids -> bookingRepository.findAllByItemIdInAndStartBeforeAndEndAfter(ids, LocalDateTime.now(), LocalDateTime.now(), SORT_BY_END)
                );
        return suppliers.get(state).apply(itemIds);
    }
}
