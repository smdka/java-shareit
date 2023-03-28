package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.ItemNotAvailableException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.service.BookingsGetter;
import ru.practicum.shareit.booking.service.State;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForBooking;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingsGetter bookingsGetter;

    private final LocalDateTime start = LocalDateTime.now().plusMinutes(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(1);

    private final UserDto userDto = new UserDto(
            1L,
            "Lol",
            "kek@gmail.com");

    private final UserDtoForBooking userDtoForBooking = new UserDtoForBooking(
            2L);

    private final ItemDto itemDto = new ItemDto(
            1L,
            "Item",
            "Description",
            true,
            2L);

    private final ItemDtoForBooking itemDtoForBooking = new ItemDtoForBooking(
            1L,
            "Item");

    private final OutcomingBookingDto outcomingBookingDto = new OutcomingBookingDto(
            1L,
            start,
            end,
            Booking.Status.APPROVED,
            userDtoForBooking,
            itemDtoForBooking);

    private final IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
            1L,
            start,
            end,
            Booking.Status.WAITING,
            2L,
            1L);

    private final IncomingBookingDto incomingBookingDtoWithWrongEnd = new IncomingBookingDto(
            1L,
            start,
            end.minusDays(1),
            Booking.Status.WAITING,
            2L,
            1L);

    private final IncomingBookingDto incomingBookingDtoWithEqualStartAndEnd = new IncomingBookingDto(
            1L,
            start,
            start,
            Booking.Status.WAITING,
            2L,
            1L);

    private final User user = new User(
            1L,
            "Lol",
            "kek@gmail.com",
        Collections.emptyList());

    private final User secondUser = new User(
            2L,
            "Lol",
            "bolek@gmail.com",
            Collections.emptyList());

    private final Item item = new Item(
            1L,
            "Item",
            "Description",
            true,
            user,
            1L,
            Collections.emptyList());

    private final Item notAvailableItem = new Item(
            1L,
            "Item",
            "Description",
            false,
            user,
            1L,
            Collections.emptyList());

    private final Booking booking = new Booking(1L,
            start,
            end,
            item,
            secondUser,
            Booking.Status.APPROVED);

    private final Booking bookingWaiting = new Booking(
            1L,
            start,
            end,
            item,
            secondUser,
            Booking.Status.WAITING);

    @Test
    void addBooking() {
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        OutcomingBookingDto createdBooking = bookingService.add(secondUser.getId(), incomingBookingDto);

        assertEquals(outcomingBookingDto, createdBooking);
    }

    @Test
    void addBookingForUnavailableItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(notAvailableItem));

        Assertions.assertThrows(ItemNotAvailableException.class,
                () -> bookingService.add(userDto.getId(), incomingBookingDto));
    }

    @Test
    void addBookingWithWrongEndDate() {
        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.add(userDto.getId(), incomingBookingDtoWithWrongEnd));
    }


    @Test
    void addBookingWithEqualEndAndStart() {
        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.add(userDto.getId(), incomingBookingDtoWithEqualStartAndEnd));
    }

    @Test
    void createBookingEqualOwnerIds() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Assertions.assertThrows(BookingNotFoundException.class,
                () -> bookingService.add(userDto.getId(), incomingBookingDto));
    }

    @Test
    void changeBookingStatus() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingWaiting);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));

        OutcomingBookingDto approvedBooking = bookingService.changeStatus(1L, true, 1L);

        assertEquals(outcomingBookingDto, approvedBooking);
    }

    @Test
    void bookingAlreadyApproved() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookingService.changeStatus(1L, true, 1L));
    }

    @Test
    void approvingBookingWithWrongId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        Assertions.assertThrows(BookingNotFoundException.class,
                () -> bookingService.changeStatus(2L, true, 1L));
    }

    @Test
    void getBookingByBookingIdAndUserId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        OutcomingBookingDto foundBooking = bookingService.getById(1L, 1L);

        assertEquals(outcomingBookingDto, foundBooking);
    }

    @Test
    void getBookingWithWrongUserId() {
        Assertions.assertThrows(UserNotFoundException.class,
                () -> bookingService.getById(1L, 3L));
    }


    @Test
    void getAllByBooker() {
        when(bookingsGetter.forUser(anyLong(), eq(State.ALL), any(Pageable.class))).thenReturn(List.of(booking));
        when(userRepository.existsById(anyLong())).thenReturn(true);

        Collection<OutcomingBookingDto> bookings = bookingService.getAllByUserId(1L, State.ALL, 0, 1);

        Assertions.assertEquals(1, bookings.size());
    }

    @Test
    void getAllByBookerWithCurrentState() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingsGetter.forUser(anyLong(), eq(State.CURRENT), any(Pageable.class))).thenReturn(List.of(booking));

        Collection<OutcomingBookingDto> currentBookings = bookingService.getAllByUserId(1L, State.CURRENT, 0, 1);

        Assertions.assertEquals(1, currentBookings.size());
    }

    @Test
    void getAllByBookerWithWaitingState() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingsGetter.forUser(anyLong(), eq(State.WAITING), any(Pageable.class))).thenReturn(List.of(booking));

        Collection<OutcomingBookingDto> currentBookings = bookingService.getAllByUserId(1L, State.WAITING, 0, 1);

        Assertions.assertEquals(1, currentBookings.size());
    }

    @Test
    void getAllByBookerWithPastState() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingsGetter.forUser(anyLong(), eq(State.PAST), any(Pageable.class))).thenReturn(List.of(booking));

        Collection<OutcomingBookingDto> currentBookings = bookingService.getAllByUserId(1L, State.PAST, 0, 1);

        Assertions.assertEquals(1, currentBookings.size());
    }

    @Test
    void getAllByBookerWithFutureState() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingsGetter.forUser(anyLong(), eq(State.FUTURE), any(Pageable.class))).thenReturn(List.of(booking));

        Collection<OutcomingBookingDto> currentBookings = bookingService.getAllByUserId(1L, State.FUTURE, 0, 1);

        Assertions.assertEquals(1, currentBookings.size());
    }

    @Test
    void getAllByBookerWithRejectedState() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingsGetter.forUser(anyLong(), eq(State.REJECTED), any(Pageable.class))).thenReturn(List.of(booking));

        Collection<OutcomingBookingDto> currentBookings = bookingService.getAllByUserId(1L, State.REJECTED, 0, 1);

        Assertions.assertEquals(1, currentBookings.size());
    }

    @Test
    void getAllByItemOwner() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingsGetter.forItemOwner(anyCollection(), eq(State.ALL), any(Pageable.class))).thenReturn(List.of(booking));

        Collection<OutcomingBookingDto> currentBookings = bookingService.getAllForItemOwnerId(1L, State.ALL, 0, 1);

        Assertions.assertEquals(1, currentBookings.size());
    }

    @Test
    void getAllByItemOwnerWithCurrentState() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingsGetter.forItemOwner(anyCollection(), eq(State.CURRENT), any(Pageable.class))).thenReturn(List.of(booking));

        Collection<OutcomingBookingDto> currentBookings = bookingService.getAllForItemOwnerId(1L, State.CURRENT, 0, 1);

        Assertions.assertEquals(1, currentBookings.size());
    }

    @Test
    void getAllByItemOwnerWithWaitingState() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingsGetter.forItemOwner(anyCollection(), eq(State.WAITING), any(Pageable.class))).thenReturn(List.of(booking));

        Collection<OutcomingBookingDto> currentBookings = bookingService.getAllForItemOwnerId(1L, State.WAITING, 0, 1);

        Assertions.assertEquals(1, currentBookings.size());
    }

    @Test
    void getAllByItemOwnerWithPastState() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingsGetter.forItemOwner(anyCollection(), eq(State.PAST), any(Pageable.class))).thenReturn(List.of(booking));

        Collection<OutcomingBookingDto> currentBookings = bookingService.getAllForItemOwnerId(1L, State.PAST, 0, 1);

        Assertions.assertEquals(1, currentBookings.size());
    }

    @Test
    void getAllByItemOwnerWithFutureState() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingsGetter.forItemOwner(anyCollection(), eq(State.FUTURE), any(Pageable.class))).thenReturn(List.of(booking));

        Collection<OutcomingBookingDto> currentBookings = bookingService.getAllForItemOwnerId(1L, State.FUTURE, 0, 1);

        Assertions.assertEquals(1, currentBookings.size());
    }

    @Test
    void getAllByItemOwnerWithRejectedState() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingsGetter.forItemOwner(anyCollection(), eq(State.REJECTED), any(Pageable.class))).thenReturn(List.of(booking));

        Collection<OutcomingBookingDto> currentBookings = bookingService.getAllForItemOwnerId(1L, State.REJECTED, 0, 1);

        Assertions.assertEquals(1, currentBookings.size());
    }
}
