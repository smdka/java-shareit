package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.State;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingServiceIntegrationTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private final UserDto userDto1 = new UserDto(
            null,
            "Keklord",
            "lolek@gmail.com");

    private final UserDto userDto2 = new UserDto(
            null,
            "John Carmack",
            "quake@gmail.com");

    private final ItemDto itemDto1 = new ItemDto(
            null,
            "Item",
            "Description",
            true,
            1L);

    private final ItemDto itemDto2 = new ItemDto(
            null,
            "Item",
            "Description",
            true,
            1L);

    private final IncomingBookingDto bookingInputDto1 = new IncomingBookingDto(
            2L,
            LocalDateTime.now().plusMinutes(30),
            LocalDateTime.now().plusHours(1),
            null,
            2L,
            1L);

    private final IncomingBookingDto bookingInputDto2 = new IncomingBookingDto(
            2L,
            LocalDateTime.now().plusMinutes(30),
            LocalDateTime.now().plusHours(1),
            null,
            1L,
            2L);

    @Test
    void getAllBookingsByItemOwner() {
        UserDto createdUser1 = userService.add(userDto1);
        ItemDto createdItem1 = itemService.add(itemDto1, createdUser1.getId());
        UserDto createdUser2 = userService.add(userDto2);
        ItemDto createdItem2 = itemService.add(itemDto2, createdUser2.getId());
        OutcomingBookingDto bookingOutputDto1 = bookingService.add(createdUser2.getId(), bookingInputDto1);
        OutcomingBookingDto bookingOutputDto2 = bookingService.add(createdUser1.getId(), bookingInputDto2);

        assertEquals(1L, createdItem1.getId());
        assertEquals(2L, createdItem2.getId());
        assertEquals(1L, bookingOutputDto1.getId());
        assertEquals(createdUser2.getId(), bookingOutputDto1.getBooker().getId());
        assertEquals(1L, bookingOutputDto1.getItem().getId());
        assertEquals(Booking.Status.WAITING, bookingOutputDto1.getStatus());

        assertEquals(1L, createdItem1.getId());
        assertEquals(2L, createdItem2.getId());
        assertEquals(2L, bookingOutputDto2.getId());
        assertEquals(createdUser1.getId(), bookingOutputDto2.getBooker().getId());
        assertEquals(2L, bookingOutputDto2.getItem().getId());
        assertEquals(Booking.Status.WAITING, bookingOutputDto2.getStatus());

        OutcomingBookingDto approveOutputDto1 = bookingService
                .changeStatus(createdUser1.getId(), true, bookingOutputDto1.getId());
        OutcomingBookingDto approveOutputDto2 = bookingService
                .changeStatus(createdUser2.getId(), true, bookingOutputDto2.getId());

        assertEquals(1L, approveOutputDto1.getId());
        assertEquals(2L, approveOutputDto1.getBooker().getId());
        assertEquals(1L, approveOutputDto1.getItem().getId());
        assertEquals(Booking.Status.APPROVED, approveOutputDto1.getStatus());

        assertEquals(2L, approveOutputDto2.getId());
        assertEquals(1L, approveOutputDto2.getBooker().getId());
        assertEquals(2L, approveOutputDto2.getItem().getId());
        assertEquals(Booking.Status.APPROVED, approveOutputDto2.getStatus());

        List<OutcomingBookingDto> bookingOutputDtos = new ArrayList<>(bookingService
                .getAllForItemOwnerId(createdUser2.getId(), State.ALL, 0, 2));

        assertEquals(1, bookingOutputDtos.size());
        assertEquals(2L, bookingOutputDtos.get(0).getId());
        assertEquals(1L, bookingOutputDtos.get(0).getBooker().getId());
        assertEquals(2L, bookingOutputDtos.get(0).getItem().getId());
        assertEquals(Booking.Status.APPROVED, bookingOutputDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsWithFutureStateByBooker() {
        UserDto createdUser1 = userService.add(userDto1);
        Long user1Id = createdUser1.getId();
        ItemDto createdItem1 = itemService.add(itemDto1, user1Id);

        UserDto createdUser2 = userService.add(userDto2);
        Long user2Id = createdUser2.getId();

        IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
                null,
                LocalDateTime.now().plusSeconds(15),
                LocalDateTime.now().plusDays(1),
                Booking.Status.WAITING,
                user2Id,
                createdItem1.getId());

        OutcomingBookingDto addedBooking = bookingService.add(user2Id, incomingBookingDto);

        List<OutcomingBookingDto> bookingOutputDtos = new ArrayList<>(
                bookingService.getAllForItemOwnerId(user1Id, State.FUTURE, 0, 1));

        assertEquals(addedBooking, bookingOutputDtos.get(0));
    }

    @Test
    void approveByOwnerWithWrongBookingId() {
        Long bookingId = 2L;
        UserDto addedUser = userService.add(userDto1);

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.changeStatus(bookingId, true, addedUser.getId()));
    }

    @Test
    void getBookingByIdAndUserWrongBookingId() {
        Long bookingId = 2L;
        UserDto addedUser = userService.add(userDto1);

        assertThrows(BookingNotFoundException.class, () -> bookingService.getById(bookingId, addedUser.getId()));
    }
}
