package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingIncomingDtoTest {
    @Autowired
    private JacksonTester<IncomingBookingDto> json;

    private static final String DATE_TIME = "2023-03-28T13:50:28";

    private IncomingBookingDto incomingBookingDto = null;

    @BeforeEach
    public void setup() {
        incomingBookingDto = new IncomingBookingDto(
                2L,
                LocalDateTime.parse(DATE_TIME),
                LocalDateTime.parse(DATE_TIME),
                null, null, null);
    }

    @Test
    void startSerializes() throws IOException {
        assertThat(json.write(incomingBookingDto))
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(DATE_TIME);
    }

    @Test
    void endSerializes() throws IOException {
        assertThat(json.write(incomingBookingDto))
                .extractingJsonPathStringValue("$.end")
                .isEqualTo(DATE_TIME);
    }
}
