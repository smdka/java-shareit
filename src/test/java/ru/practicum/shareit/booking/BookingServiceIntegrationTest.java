package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.State;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceIntegrationTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private final UserDto userDto1 = new UserDto(
            null,
            "Igor",
            "igor@gmail.dom");

    private final UserDto userDto2 = new UserDto(
            null,
            "Stas",
            "stas@gmail.dom");

    private final ItemDto itemDto1 = new ItemDto(
            null,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            1L,
            null,
            null,
            null,
            null);

    private final ItemDto itemDto2 = new ItemDto(
            null,
            "Какая-то другая вещь",
            "Какое-то другое описание",
            true,
            1L,
            null,
            null,
            null,
            null);

    private final BookingInputDto bookingInputDto1 = new BookingInputDto(
            2L,
            LocalDateTime.now().plusMinutes(30),
            LocalDateTime.now().plusHours(1));

    private final BookingInputDto bookingInputDto2 = new BookingInputDto(
            2L,
            LocalDateTime.now().plusMinutes(30),
            LocalDateTime.now().plusHours(1));

    @Test
    void getAllBookingsByOwner() {
        UserDto createdUser1 = userService.create(userDto1);
        ItemDto createdItem1 = itemService.create(itemDto1, createdUser1);
        UserDto createdUser2 = userService.create(userDto2);
        ItemDto createdItem2 = itemService.create(itemDto2, createdUser2);
        BookingOutputDto bookingOutputDto1 = bookingService.create(createdUser1, createdItem2, bookingInputDto1);
        BookingOutputDto bookingOutputDto2 = bookingService.create(createdUser1, createdItem2, bookingInputDto2);

        Assertions.assertEquals(1L, createdItem1.getId());
        Assertions.assertEquals(2L, createdItem2.getId());
        Assertions.assertEquals(1L, bookingOutputDto1.getId());
        Assertions.assertEquals(1L, bookingOutputDto1.getBooker().getId());
        Assertions.assertEquals(2L, bookingOutputDto1.getItem().getId());
        Assertions.assertEquals(Status.WAITING.name(), bookingOutputDto1.getStatus());

        Assertions.assertEquals(1L, createdItem1.getId());
        Assertions.assertEquals(2L, createdItem2.getId());
        Assertions.assertEquals(2L, bookingOutputDto2.getId());
        Assertions.assertEquals(1L, bookingOutputDto2.getBooker().getId());
        Assertions.assertEquals(2L, bookingOutputDto2.getItem().getId());
        Assertions.assertEquals(Status.WAITING.name(), bookingOutputDto2.getStatus());

        BookingOutputDto approveOutputDto1 = bookingService
                .approveByOwner(createdUser2.getId(), bookingOutputDto1.getId(), true);
        BookingOutputDto approveOutputDto2 = bookingService
                .approveByOwner(createdUser2.getId(), bookingOutputDto2.getId(), true);

        Assertions.assertEquals(1L, approveOutputDto1.getId());
        Assertions.assertEquals(1L, approveOutputDto1.getBooker().getId());
        Assertions.assertEquals(2L, approveOutputDto1.getItem().getId());
        Assertions.assertEquals(Status.APPROVED.name(), approveOutputDto1.getStatus());

        Assertions.assertEquals(2L, approveOutputDto2.getId());
        Assertions.assertEquals(1L, approveOutputDto2.getBooker().getId());
        Assertions.assertEquals(2L, approveOutputDto2.getItem().getId());
        Assertions.assertEquals(Status.APPROVED.name(), approveOutputDto2.getStatus());

        List<BookingOutputDto> bookingOutputDtos = bookingService
                .findAllByOwner(createdUser2.getId(), State.ALL, 0, 2);

        Assertions.assertEquals(2, bookingOutputDtos.size());
        Assertions.assertEquals(2L, bookingOutputDtos.get(0).getId());
        Assertions.assertEquals(1L, bookingOutputDtos.get(0).getBooker().getId());
        Assertions.assertEquals(2L, bookingOutputDtos.get(0).getItem().getId());
        Assertions.assertEquals(Status.APPROVED.name(), bookingOutputDtos.get(0).getStatus());
    }

    @Test
    void getAllFutureBookingsByBooker() {
        UserDto createdUser1 = userService.create(userDto1);
        ItemDto createdItem1 = itemService.create(itemDto1, createdUser1);
        UserDto createdUser2 = userService.create(userDto2);
        ItemDto createdItem2 = itemService.create(itemDto2, createdUser2);
        BookingOutputDto bookingOutputDto = bookingService.create(createdUser1, createdItem2, bookingInputDto1);

        Assertions.assertEquals(1L, createdItem1.getId());
        Assertions.assertEquals(2L, createdItem2.getId());
        Assertions.assertEquals(1L, bookingOutputDto.getId());
        Assertions.assertEquals(1L, bookingOutputDto.getBooker().getId());
        Assertions.assertEquals(2L, bookingOutputDto.getItem().getId());
        Assertions.assertEquals(Status.WAITING.name(), bookingOutputDto.getStatus());

        BookingOutputDto approveOutputDto = bookingService
                .approveByOwner(createdUser2.getId(), bookingOutputDto.getId(), true);

        Assertions.assertEquals(1L, approveOutputDto.getId());
        Assertions.assertEquals(1L, approveOutputDto.getBooker().getId());
        Assertions.assertEquals(2L, approveOutputDto.getItem().getId());
        Assertions.assertEquals(Status.APPROVED.name(), approveOutputDto.getStatus());

        List<BookingOutputDto> bookingOutputDtos = bookingService
                .findAllByBooker(createdUser1.getId(), State.FUTURE, 0, 1);

        Assertions.assertEquals(1, bookingOutputDtos.size());

        Assertions.assertEquals(1L, bookingOutputDtos.get(0).getId());
        Assertions.assertEquals(1L, bookingOutputDtos.get(0).getBooker().getId());
        Assertions.assertEquals(2L, bookingOutputDtos.get(0).getItem().getId());
        Assertions.assertEquals(Status.APPROVED.name(), bookingOutputDtos.get(0).getStatus());
    }

    @Test
    void approveByOwnerWrongBookingId() {
        Long id = 2L;

        Assertions
                .assertThrows(NotFoundException.class, () -> bookingService.approveByOwner(id, id, true));
    }

    @Test
    void getBookingByIdAndUserWrongBookingId() {
        Long id = 2L;

        Assertions
                .assertThrows(NotFoundException.class, () -> bookingService.getBookingByIdAndUser(id, id));
    }
}
