package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.State;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.dto.OutcomingItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForBooking;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = new UserDto(1L, "User", "bolek@yandex.ru");

    private final ItemDto itemDto = new ItemDto(1L, "Item", "Description", true, 1L);

    private final OutcomingBookingDto outcomingBookingDto = new OutcomingBookingDto(
            1L,
            LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS),
            Booking.Status.WAITING,
            new UserDtoForBooking(1L),
            new ItemDtoForBooking(1L, "Item"));

    private final OutcomingItemDto outcomingItemDto = new OutcomingItemDto(
            1L,
            "Item",
            "Description",
            true,
            null, null, null);

    private final IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
            1L,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusDays(1),
            Booking.Status.WAITING,
            1L,
            1L);

    @BeforeEach
    void setup() {
        when(userService.getById(anyLong())).thenReturn(userDto);
    }

    @Test
    void addBooking() throws Exception {
        when(userService.getById(anyLong())).thenReturn(userDto);
        when(itemService.getByItemId(anyLong(), anyLong())).thenReturn(outcomingItemDto);
        when(bookingService.add(anyLong(), any(IncomingBookingDto.class))).thenReturn(outcomingBookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(incomingBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outcomingBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(outcomingBookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(outcomingBookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(outcomingBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(outcomingBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(outcomingBookingDto.getItem().getId()), Long.class));
    }

    @Test
    void changeStatus() throws Exception {
        when(bookingService.changeStatus(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(outcomingBookingDto);

        mvc.perform(patch("/bookings/{bookingId}", "1")
                        .content(mapper.writeValueAsString(incomingBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outcomingBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(outcomingBookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(outcomingBookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(outcomingBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(outcomingBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(outcomingBookingDto.getItem().getId()), Long.class));
    }

    @Test
    void getById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(outcomingBookingDto);

        mvc.perform(get("/bookings/{bookingId}", "1")
                        .content(mapper.writeValueAsString(incomingBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outcomingBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(outcomingBookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(outcomingBookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(outcomingBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(outcomingBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(outcomingBookingDto.getItem().getId()), Long.class));
    }

    @Test
    void getAllByUserId() throws Exception {
        when(userService.getById(anyLong())).thenReturn(userDto);
        when(bookingService.getAllByUserId(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(outcomingBookingDto));

        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(incomingBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(outcomingBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(outcomingBookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(outcomingBookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(outcomingBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(outcomingBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(outcomingBookingDto.getItem().getId()), Long.class));
    }

    @Test
    void getAllByUserIdWithWrongState() throws Exception {
        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(incomingBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALLL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Unknown state: ALLL")));
    }

    @Test
    void getAllByItemOwnerId() throws Exception {
        when(userService.getById(anyLong())).thenReturn(userDto);
        when(bookingService.getAllForItemOwnerId(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(outcomingBookingDto));

        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(incomingBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(outcomingBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(outcomingBookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(outcomingBookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(outcomingBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(outcomingBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(outcomingBookingDto.getItem().getId()), Long.class));
    }
}
