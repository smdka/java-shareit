package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {
    private static final String ENDPOINT = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder rest) {
        super(rest.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + ENDPOINT))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> add(ItemDto itemDto, Long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> getItemsByUserId(Long userId, Integer from, Integer size) {
        Map<String, Object> params = Map.of(
                "from", from,
                "size", size);
        return get("?from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> addComment(IncomingCommentDto commentDto, Long userId, Long itemId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

    public ResponseEntity<Object> getByItemIdAndUserId(Long itemId, Long userId) {
        return get("/" + itemId, userId, null);
    }

    public ResponseEntity<Object> getAllWithText(Long userId, String text, Integer from, Integer size) {
        Map<String, Object> params = Map.of(
                "from", from,
                "size", size,
                "text", text);
        return get("/search?from={from}&size={size}&text={text}", userId, params);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }
}
