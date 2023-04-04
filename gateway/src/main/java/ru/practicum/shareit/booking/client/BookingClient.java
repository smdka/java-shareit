package ru.practicum.shareit.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;

import java.util.Map;

@Component
public class BookingClient extends BaseClient {
    private static final String ENDPOINT = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder rest) {
        super(rest.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + ENDPOINT))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getBookingsByUserId(Long userId, State state, Integer from, Integer size) {
        Map<String, Object> params = Map.of(
                "state", state.name(),
                "from", from,
                "size", size);
        return get("?state={state}&from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> add(Long userId, IncomingBookingDto bookingDto) {
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> changeStatus(Long userId, Long bookingId, Boolean approved) {
        Map<String, Object> params = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, params, null);
    }

    public ResponseEntity<Object> getBookingsByItemOwnerId(Long itemOwnerId, State state, Integer from, Integer size) {
        Map<String, Object> params = Map.of(
                "state", state.name(),
                "from", from,
                "size", size);
        return get("/owner?state={state}&from={from}&size{size}", itemOwnerId, params);
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }
}
