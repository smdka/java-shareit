package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto save(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                        @Valid @RequestBody ItemDto item, BindingResult br) {
        if (br.hasErrors()) {
            throw new ValidationException();
        }
        log.info("Получен запрос POST /items с заголовком X-Sharer-User-Id = {}", userId);
        return itemService.add(userId, ItemMapper.toItem(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable long itemId,
                          @NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto itemWithUpdates,
                          BindingResult br) {
        if (br.hasErrors()) {
            throw new ValidationException();
        }
        log.info("Получен запрос PATCH /items/{} с заголовком X-Sharer-User-Id = {}", itemId, userId);
        return itemService.updateById(itemId, userId, ItemMapper.toItem(itemWithUpdates));
    }

    @GetMapping("/{itemId}")
    public ItemDto getByItemId(@PathVariable long itemId,
                            @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /items/{} с заголовком X-Sharer-User-Id = {}", itemId, userId);
        return itemService.getByItemId(itemId, userId);
    }

    @GetMapping
    public Iterable<ItemDto> getByUserId(@NotNull @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /items с заголовком X-Sharer-User-Id = {}", userId);
        return itemService.getByUserId(userId);
    }

    @GetMapping("/search")
    public Iterable<ItemDto> getIfContainsTextInNameOrDescription(@RequestParam String text,
                                                               @NotNull @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /items/search?text={} с заголовком X-Sharer-User-Id = {}", text, userId);
        return itemService.findIfContainsTextInNameOrDescription(text, userId);
    }
}
