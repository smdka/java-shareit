package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OutcomingItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto add(ItemDto item, Long ownerId);

    ItemDto updateById(Long itemId, ItemDto itemWithUpdates, Long userId);

    OutcomingItemDto getByItemId(long itemId, long userId);

    Collection<OutcomingItemDto> getByUserId(long userId, Integer from, Integer size);

    Collection<ItemDto> searchInNameOrDescription(String text, Integer from, Integer size);

    CommentDto addComment(String text, Long authorId, Long itemId);
}
