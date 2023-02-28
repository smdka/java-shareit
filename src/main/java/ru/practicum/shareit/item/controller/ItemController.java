package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
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
        return ItemMapper.toItemDto(itemService.add(ItemMapper.toItem(item, ownerId)));
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@PathVariable long itemId,
                             @NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody ItemDto itemWithUpdates) {
        log.info("Получен запрос PATCH /items/{} с заголовком X-Sharer-User-Id = {}", itemId, userId);
        return ItemMapper.toItemDto(itemService.updateById(itemId, ItemMapper.toItem(itemWithUpdates, userId)));
    }

    @GetMapping("/{itemId}")
    public ItemDto getByItemId(@PathVariable long itemId,
                               @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /items/{} с заголовком X-Sharer-User-Id = {}", itemId, userId);
        return ItemMapper.toItemDto(itemService.getByItemId(itemId));
    }

    @GetMapping
    public Collection<ItemDto> getByUserId(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /items с заголовком X-Sharer-User-Id = {}", userId);
        return ItemMapper.toItemDtoAll(itemService.getByUserId(userId));
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text,
                                      @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /items/search?text={} с заголовком X-Sharer-User-Id = {}", text, userId);
        return ItemMapper.toItemDtoAll(itemService.searchInNameOrDescription(text));
    }
}
