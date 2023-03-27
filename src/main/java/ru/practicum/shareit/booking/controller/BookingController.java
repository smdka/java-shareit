package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.State;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public OutcomingBookingDto post(@NotNull @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                    @Valid @RequestBody IncomingBookingDto incomingBookingDto) {
        log.info("Получен запрос POST /bookings с заголовком X-Sharer-User-Id = {}", bookerId);
        return bookingService.add(bookerId, incomingBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public OutcomingBookingDto patch(@PathVariable Long bookingId,
                                     @RequestParam Boolean approved,
                                     @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос PATCH /bookings/{}?approved={} с заголовком X-Sharer-User-Id = {}",
                bookingId, approved, userId);
        return bookingService.changeStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public OutcomingBookingDto getBookingById(@PathVariable Long bookingId,
                                              @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /bookings/{} с заголовком X-Sharer-User-Id = {}", bookingId, userId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public Collection<OutcomingBookingDto> getBookingsForUser(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
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
        return bookingService.getAllByUserId(userId, stateValue, from, size);
    }

    @GetMapping("/owner")
    public Collection<OutcomingBookingDto> getBookingsForItemOwner(@NotNull @RequestHeader("X-Sharer-User-Id") Long itemOwnerId,
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
        return bookingService.getAllForItemOwnerId(itemOwnerId, stateValue, from, size);
    }
}
