package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.OutcomingItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item add(Item item);

    Item updateById(long itemId, Item itemWithUpdates);

    OutcomingItemDto getByItemId(long itemId, long userId);

    Collection<OutcomingItemDto> getByUserId(long userId);

    Collection<Item> searchInNameOrDescription(String text);

    CommentDto addComment(String text, Long authorId, Long itemId);
}
