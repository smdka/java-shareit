package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.ItemNotAvailableException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.PageableUtil;

import javax.validation.ValidationException;
import java.util.Collection;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private static final String USER_NOT_FOUND_MSG = "Пользователь с id = %d не найден";
    private static final String ITEM_NOT_AVAILABLE_MSG = "Предмет с id = %d не доступен";
    private static final String BOOKING_NOT_FOUND_MSG = "Бронирование с id = %d не найдено";
    private static final String ITEM_NOT_FOUND_MSG = "Вещь с id = %d не найдена";
    private static final String NO_PERMISSION_MSG =
            "У пользователя с id = %d нет прав на изменение/получение бронирования с id = %d";
    private static final String BOOKING_DATETIME_ERROR_MSG =
            "Время окончания бронирования не может быть раньше или равно началу бронирования";
    private static final String CANT_CHANGE_STATUS_MSG = "Нельзя изменить статус с APPROVED";
    private static final String CANT_BOOK_BY_ITEM_OWNER_MSG = "Хозяин вещи не может создать бронь своей вещи";
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingsGetter bookingsGetter;

    @Transactional
    public OutcomingBookingDto add(Long bookerId, IncomingBookingDto incomingBookingDto) {
        Booking booking = BookingMapper.toBooking(incomingBookingDto, bookerId);

        validateBookingDatesOrThrow(booking);

        User booker = userRepository.findById(bookerId).orElseThrow(
                () -> new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, bookerId)));

        Long itemId = booking.getItem().getId();
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MSG, itemId)));

        ifItemNotAvailableThrow(itemId, item);

        ifUserEqualsOwnerThrow(bookerId, item);

        booking.setBooker(booker);
        booking.setItem(item);

        return BookingMapper.toOutcomingDto(bookingRepository.save(booking));
    }

    private void ifUserEqualsOwnerThrow(Long bookerId, Item item) {
        if (item.getOwner().getId().equals(bookerId)) {
            throw new BookingNotFoundException(CANT_BOOK_BY_ITEM_OWNER_MSG);
        }
    }

    private void ifItemNotAvailableThrow(Long itemId, Item item) {
        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new ItemNotAvailableException(String.format(ITEM_NOT_AVAILABLE_MSG, itemId));
        }
    }

    private void validateBookingDatesOrThrow(Booking booking) {
        if (booking.getEnd().isBefore(booking.getStart()) ||
                booking.getEnd().isEqual(booking.getStart())) {
            throw new ValidationException(BOOKING_DATETIME_ERROR_MSG);
        }
    }

    @Override
    @Transactional
    public OutcomingBookingDto changeStatus(Long bookingId, Boolean approved, Long itemOwnerId) {
        ifUserDoesntExistThrow(itemOwnerId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException(String.format(BOOKING_NOT_FOUND_MSG, bookingId)));

        ifBookingIsApprovedThrow(booking);

        Item item = booking.getItem();
        Long ownerId = item.getOwner().getId();

        ifUserNotEqualsOwnerThrow(itemOwnerId, item, ownerId);

        booking.setStatus(Boolean.TRUE.equals(approved) ? Booking.Status.APPROVED : Booking.Status.REJECTED);
        return BookingMapper.toOutcomingDto(bookingRepository.save(booking));
    }

    private void ifUserNotEqualsOwnerThrow(Long userId, Item item, Long ownerId) {
        if (!userId.equals(ownerId)) {
            throw new BookingNotFoundException(String.format(NO_PERMISSION_MSG, userId, item.getId()));
        }
    }

    private void ifBookingIsApprovedThrow(Booking booking) {
        if (booking.getStatus().equals(Booking.Status.APPROVED)) {
            throw new IllegalArgumentException(CANT_CHANGE_STATUS_MSG);
        }
    }

    private void ifUserDoesntExistThrow(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId));
        }
    }

    @Override
    public OutcomingBookingDto getById(Long bookingId, Long userId) {
        ifUserDoesntExistThrow(userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException(String.format(BOOKING_NOT_FOUND_MSG, bookingId)));

        Long bookerId = booking.getBooker().getId();
        Long itemOwnerId = booking.getItem().getOwner().getId();
        if (bookerId.equals(userId) || itemOwnerId.equals(userId)) {
            return BookingMapper.toOutcomingDto(booking);
        }
        throw new BookingNotFoundException(String.format(NO_PERMISSION_MSG, userId, bookingId));
    }

    @Override
    public Collection<OutcomingBookingDto> getAllByUserId(Long userId, State state, Integer from, Integer size) {
        ifUserDoesntExistThrow(userId);
        return BookingMapper.toOutcomingDtoAll(bookingsGetter.forUser(userId, state, PageableUtil.getPageRequestSortByStart(from, size)));
    }

    @Override
    public Collection<OutcomingBookingDto> getAllForItemOwnerId(Long itemOwnerId, State state, Integer from, Integer size) {
        ifUserDoesntExistThrow(itemOwnerId);
        Collection<Long> itemIds = itemRepository.findIdsByOwnerId(itemOwnerId);
        return BookingMapper.toOutcomingDtoAll(bookingsGetter.forItemOwner(itemIds, state, PageableUtil.getPageRequestSortByStart(from, size)));
    }
}
