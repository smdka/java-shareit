package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoForBooking;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class BookingMapper {

    private BookingMapper() {
    }

    public static OutcomingBookingDto toOutcomingDto(Booking booking) {
        return new OutcomingBookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new UserDtoForBooking(booking.getBooker().getId()),
                new ItemDtoForBooking(booking.getItem().getId(), booking.getItem().getName()));
    }

    public static Collection<OutcomingBookingDto> toOutcomingDtoAll(Collection<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toOutcomingDto)
                .collect(Collectors.toList());
    }

    public static Booking toBooking(IncomingBookingDto incomingBookingDto, Long bookerId) {
        Booking booking = new Booking();
        booking.setId(incomingBookingDto.getId());
        booking.setStart(incomingBookingDto.getStart());
        booking.setEnd(incomingBookingDto.getEnd());
        Booking.Status status = incomingBookingDto.getStatus();
        if (status != null) {
            booking.setStatus(status);
        }
        User booker = new User();
        booker.setId(bookerId);
        booking.setBooker(booker);
        Item item = new Item();
        item.setId(incomingBookingDto.getItemId());
        booking.setItem(item);
        return booking;
    }
}
