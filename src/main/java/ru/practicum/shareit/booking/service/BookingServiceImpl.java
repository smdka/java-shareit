package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Collection;

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
    private static final String CANT_CHANGE_STATUS_MSG = "Нельзя изменить статус с APPROVED";
    private static final String CANT_BOOK_BY_ITEM_OWNER_MSG = "Хозяин вещи не может создать бронь своей вещи";
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingsGetter bookingsGetter;

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

        if (item.getOwner().getId().equals(bookerId)) {
            throw new UserHasNoPermissionException(CANT_BOOK_BY_ITEM_OWNER_MSG);
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
            throw new IllegalArgumentException(CANT_CHANGE_STATUS_MSG);
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
       return bookingsGetter.forUser(userId, state);
    }

    @Override
    public Collection<Booking> getAllForItemOwnerId(Long itemOwnerId, State state) {
        if (!userRepository.existsById(itemOwnerId)) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, itemOwnerId));
        }
        Collection<Long> itemIds = itemRepository.findIdsByOwnerId(itemOwnerId);
        return bookingsGetter.forItemOwner(itemIds, state);
    }
}
