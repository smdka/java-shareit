package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.IncomingRequestDto;

import java.util.Map;

@Component
public class RequestClient extends BaseClient {
    private static final String ENDPOINT = "/requests";

    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder rest) {
        super(rest.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + ENDPOINT))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> add(Long userId, IncomingRequestDto incomingRequestDto) {
        return post("", userId, incomingRequestDto);
    }

    public ResponseEntity<Object> getAllByUserId(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getByRequestId(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId, Integer from, Integer size) {
        Map<String, Object> params = Map.of(
                "from", from,
                "size", size);
        return get("/all?from={from}&size={size}", userId, params);
    }
}
