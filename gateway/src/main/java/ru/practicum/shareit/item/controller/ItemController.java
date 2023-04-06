package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.client.ItemClient;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@Validated
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> postItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                           @Valid @RequestBody ItemDto item) {
        log.info("Получен запрос POST /items с заголовком X-Sharer-User-Id = {}", ownerId);
        return itemClient.add(item, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                              @PathVariable Long itemId,
                                              @Valid @RequestBody IncomingCommentDto commentDto) {
        log.info("Получен запрос POST /items/{}/comment с заголовком X-Sharer-User-Id = {}", itemId, authorId);
        return itemClient.addComment(commentDto, authorId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@PathVariable Long itemId,
                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemDto itemWithUpdates) {
        log.info("Получен запрос PATCH /items/{} с заголовком X-Sharer-User-Id = {}", itemId, userId);
        return itemClient.update(userId, itemId, itemWithUpdates);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getByItemId(@PathVariable long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /items/{} с заголовком X-Sharer-User-Id = {}", itemId, userId);
        return itemClient.getByItemIdAndUserId(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Получен запрос GET /items с заголовком X-Sharer-User-Id = {}", userId);
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Получен запрос GET /items/search?text={} с заголовком X-Sharer-User-Id = {}", text, userId);
        return itemClient.getAllWithText(userId, text, from, size);
    }
}
