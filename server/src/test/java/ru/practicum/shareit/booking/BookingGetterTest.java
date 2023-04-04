package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingsGetter;
import ru.practicum.shareit.booking.service.State;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingGetterTest {
    private final User firstUser = new User(
            1L,
            "User",
            "email@email.com",
            Collections.emptyList());
    private final User secondUser = new User(
            2L,
            "User",
            "another.email@email.com",
            Collections.emptyList());
    private final Item item = new Item(
            1L,
            "Name",
            "Description",
            true,
            firstUser,
            null,
            Collections.emptyList());
    private final Booking booking = new Booking(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            item,
            secondUser,
            Booking.Status.WAITING);
    @InjectMocks
    private BookingsGetter bookingsGetter;

    @Mock
    private BookingRepository bookingRepository;

    @Test
    void getBookingsForUserAllState() {
        when(bookingRepository.findAllByBookerId(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));

        List<Booking> bookings = new ArrayList<>(bookingsGetter.forUser(firstUser.getId(), State.ALL, Pageable.unpaged()));

        assertFalse(bookings.isEmpty());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void getBookingsForUserWaitingState() {
        when(bookingRepository.findAllByBookerIdAndStatus(
                anyLong(),
                eq(Booking.Status.WAITING),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<Booking> bookings = new ArrayList<>(bookingsGetter.forUser(firstUser.getId(), State.WAITING, Pageable.unpaged()));

        assertFalse(bookings.isEmpty());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void getBookingsForUserRejectedState() {
        when(bookingRepository.findAllByBookerIdAndStatus(
                anyLong(),
                eq(Booking.Status.REJECTED),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<Booking> bookings = new ArrayList<>(bookingsGetter.forUser(firstUser.getId(), State.REJECTED, Pageable.unpaged()));

        assertFalse(bookings.isEmpty());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void getBookingsForUserPastState() {
        when(bookingRepository.findAllByBookerIdAndEndBefore(
                anyLong(),
                any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<Booking> bookings = new ArrayList<>(bookingsGetter.forUser(firstUser.getId(), State.PAST, Pageable.unpaged()));

        assertFalse(bookings.isEmpty());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void getBookingsForUserFutureState() {
        when(bookingRepository.findAllByBookerIdAndStartAfter(
                anyLong(),
                any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<Booking> bookings = new ArrayList<>(bookingsGetter.forUser(firstUser.getId(), State.FUTURE, Pageable.unpaged()));

        assertFalse(bookings.isEmpty());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void getBookingsForUserCurrentState() {
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<Booking> bookings = new ArrayList<>(bookingsGetter.forUser(firstUser.getId(), State.CURRENT, Pageable.unpaged()));

        assertFalse(bookings.isEmpty());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void getBookingsForItemOwnerAllState() {
        when(bookingRepository.findAllByItemIdIn(anyCollection(), any(Pageable.class))).thenReturn(List.of(booking));

        List<Booking> bookings = new ArrayList<>(bookingsGetter.forItemOwner(List.of(item.getId()), State.ALL, Pageable.unpaged()));

        assertFalse(bookings.isEmpty());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void getBookingsForItemOwnerWaitingState() {
        when(bookingRepository.findAllByItemIdInAndStatus(
                anyCollection(),
                eq(Booking.Status.WAITING),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<Booking> bookings = new ArrayList<>(bookingsGetter.forItemOwner(List.of(item.getId()), State.WAITING, Pageable.unpaged()));

        assertFalse(bookings.isEmpty());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void getBookingsForItemOwnerRejectedState() {
        when(bookingRepository.findAllByItemIdInAndStatus(
                anyCollection(),
                eq(Booking.Status.REJECTED),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<Booking> bookings = new ArrayList<>(bookingsGetter.forItemOwner(List.of(item.getId()), State.REJECTED, Pageable.unpaged()));

        assertFalse(bookings.isEmpty());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void getBookingsForItemOwnerPastState() {
        when(bookingRepository.findAllByItemIdInAndEndBefore(
                anyCollection(),
                any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<Booking> bookings = new ArrayList<>(bookingsGetter.forItemOwner(List.of(item.getId()), State.PAST, Pageable.unpaged()));

        assertFalse(bookings.isEmpty());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void getBookingsForItemOwnerFutureState() {
        when(bookingRepository.findAllByItemIdInAndStartAfter(
                anyCollection(),
                any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<Booking> bookings = new ArrayList<>(bookingsGetter.forItemOwner(List.of(item.getId()), State.FUTURE, Pageable.unpaged()));

        assertFalse(bookings.isEmpty());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void getBookingsForItemOwnerCurrentState() {
        when(bookingRepository.findAllByItemIdInAndStartBeforeAndEndAfter(
                anyCollection(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<Booking> bookings = new ArrayList<>(bookingsGetter.forItemOwner(List.of(item.getId()), State.CURRENT, Pageable.unpaged()));

        assertFalse(bookings.isEmpty());
        assertEquals(booking, bookings.get(0));
    }

}
