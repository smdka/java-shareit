package ru.practicum.shareit.user.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserClient extends BaseClient {
    private static final String ENDPOINT = "/users";

    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder rest) {
        super(rest.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + ENDPOINT))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> add(UserDto userDto) {
        return post("", userDto);
    }

    public ResponseEntity<Object> getById(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> getAll() {
        return get("/");
    }

    public ResponseEntity<Object> update(Long userId, UserDto userDto) {
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> deleteById(Long userId) {
        return delete("/" + userId);
    }
}
