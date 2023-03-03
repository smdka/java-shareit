package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto postItem(@NotNull @RequestHeader("X-Sharer-User-Id") Long ownerId,
                            @Valid @RequestBody ItemDto item) {
        log.info("Получен запрос POST /items с заголовком X-Sharer-User-Id = {}", ownerId);
        return itemService.add(item, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@NotNull @RequestHeader("X-Sharer-User-Id") Long authorId,
                                  @PathVariable Long itemId,
                                  @Valid @RequestBody IncomingCommentDto commentDto) {
        log.info("Получен запрос POST /items/{}/comment с заголовком X-Sharer-User-Id = {}", itemId, authorId);
        return itemService.addComment(commentDto.getText(), authorId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@PathVariable long itemId,
                             @NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody ItemDto itemWithUpdates) {
        log.info("Получен запрос PATCH /items/{} с заголовком X-Sharer-User-Id = {}", itemId, userId);
        return itemService.updateById(itemId, itemWithUpdates, userId);
    }

    @GetMapping("/{itemId}")
    public OutcomingItemDto getByItemId(@PathVariable long itemId,
                                        @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /items/{} с заголовком X-Sharer-User-Id = {}", itemId, userId);
        return itemService.getByItemId(itemId, userId);
    }

    @GetMapping
    public Collection<OutcomingItemDto> getByUserId(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /items с заголовком X-Sharer-User-Id = {}", userId);
        return itemService.getByUserId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text,
                                      @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /items/search?text={} с заголовком X-Sharer-User-Id = {}", text, userId);
        return itemService.searchInNameOrDescription(text);
    }
}
