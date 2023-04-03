package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingOutcomingDtoTest {
    @Autowired
    private JacksonTester<OutcomingBookingDto> json;

    private static final String DATE_TIME = "2023-03-15T14:38:28";

    private OutcomingBookingDto outcomingBookingDto = null;

    @BeforeEach
    public void setup() {
        outcomingBookingDto = new OutcomingBookingDto(
                2L,
                LocalDateTime.parse(DATE_TIME),
                LocalDateTime.parse(DATE_TIME),
                Booking.Status.WAITING,
                null,
                null);
    }

    @Test
    void startSerializes() throws IOException {
        assertThat(json.write(outcomingBookingDto))
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(DATE_TIME);
    }

    @Test
    void endSerializes() throws IOException {
        assertThat(json.write(outcomingBookingDto))
                .extractingJsonPathStringValue("$.end")
                .isEqualTo(DATE_TIME);
    }
}
