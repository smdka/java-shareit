package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;

import javax.validation.constraints.Min;


@Slf4j
@Validated
@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> post(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                       @RequestBody IncomingBookingDto incomingBookingDto) {
        log.info("Получен запрос POST /bookings с заголовком X-Sharer-User-Id = {}", bookerId);
        return bookingClient.add(bookerId, incomingBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patch(@PathVariable Long bookingId,
                                        @RequestParam Boolean approved,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос PATCH /bookings/{}?approved={} с заголовком X-Sharer-User-Id = {}",
                bookingId, approved, userId);
        return bookingClient.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /bookings/{} с заголовком X-Sharer-User-Id = {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsForUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                     @RequestParam(defaultValue = "10") @Min(1) Integer size,
                                                     @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен запрос GET /bookings?state={}&from={}&size={} с заголовком X-Sharer-User-Id = {}", state, from, size, userId);
        State stateValue;
        try {
            stateValue = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unknown state: " + state);
        }
        return bookingClient.getBookingsByUserId(userId, stateValue, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForItemOwner(@RequestHeader("X-Sharer-User-Id") Long itemOwnerId,
                                                          @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                          @RequestParam(defaultValue = "10") @Min(1) Integer size,
                                                          @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен запрос GET /bookings/owner?state={} с заголовком X-Sharer-User-Id = {}", state, itemOwnerId);
        State stateValue;
        try {
            stateValue = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unknown state: " + state);
        }
        return bookingClient.getBookingsByItemOwnerId(itemOwnerId, stateValue, from, size);
    }
}
