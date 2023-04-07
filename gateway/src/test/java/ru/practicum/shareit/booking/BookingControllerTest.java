package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.Status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private final LocalDateTime start = LocalDateTime.now().plusHours(1);

    private final IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
            1L,
            start,
            start.plusDays(1),
            Status.WAITING,
            1L,
            1L);

    private final IncomingBookingDto incomingBookingDtoWithWrongEnd = new IncomingBookingDto(
            1L,
            start,
            start.minusDays(1),
            Status.WAITING,
            1L,
            1L);

    private final IncomingBookingDto incomingBookingDtoWithStartEqualsEnd = new IncomingBookingDto(
            1L,
            start,
            start,
            Status.WAITING,
            1L,
            1L);

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
    void addBookingWithStartEqualsEnd() throws Exception {
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(incomingBookingDtoWithStartEqualsEnd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBookingWithEndBeforeStart() throws Exception {
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(incomingBookingDtoWithWrongEnd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }
}
