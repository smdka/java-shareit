package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto add(long userId, Item item);

    ItemDto updateById(long itemId, long userId, Item itemWithUpdates);

    ItemDto getByItemId(long itemId, long userId);

    Collection<ItemDto> getByUserId(long userId);

    Collection<ItemDto> findIfContainsTextInNameOrDescription(String text, long userId);
}
