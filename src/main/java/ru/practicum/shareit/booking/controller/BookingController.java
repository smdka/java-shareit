package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.repository.State;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public OutcomingBookingDto postBooking(@NotNull @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                           @Valid @RequestBody IncomingBookingDto incomingBookingDto) {
        log.info("Получен запрос POST /bookings с заголовком X-Sharer-User-Id = {}", bookerId);
        return BookingMapper.toOutcomingDto(bookingService.add(bookerId, BookingMapper.toBooking(incomingBookingDto, bookerId)));
    }

    @PatchMapping("/{bookingId}")
    public OutcomingBookingDto patchBooking(@PathVariable Long bookingId,
                                            @RequestParam Boolean approved,
                                            @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос PATCH /bookings/{}?approved={} с заголовком X-Sharer-User-Id = {}",
                bookingId, approved, userId);
        return BookingMapper.toOutcomingDto(bookingService.changeStatus(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public OutcomingBookingDto getBookingById(@PathVariable Long bookingId,
                                     @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /bookings/{} с заголовком X-Sharer-User-Id = {}", bookingId, userId);
        return BookingMapper.toOutcomingDto(bookingService.getById(bookingId, userId));
    }

    @GetMapping
    public Collection<OutcomingBookingDto> getBookingsForUser(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL") State state) {
        log.info("Получен запрос GET /bookings?state={} с заголовком X-Sharer-User-Id = {}", state, userId);
        return BookingMapper.toOutcomingDtoAll(bookingService.getAllByUserId(userId, state));
    }

    @GetMapping("/owner")
    public Collection<OutcomingBookingDto> getBookingsForItemOwner(@NotNull @RequestHeader("X-Sharer-User-Id") Long itemOwnerId,
                                                          @RequestParam(defaultValue = "ALL") State state) {
        log.info("Получен запрос GET /bookings/owner?state={} с заголовком X-Sharer-User-Id = {}", state, itemOwnerId);
        return BookingMapper.toOutcomingDtoAll(bookingService.getAllForItemOwnerId(itemOwnerId, state));
    }
}
