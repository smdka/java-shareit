package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item add(Item item);

    Item updateById(long itemId, Item itemWithUpdates);

    Item getByItemId(long itemId, long userId);

    Collection<Item> getByUserId(long userId);

    Collection<Item> findIfContainsTextInNameOrDescription(String text);
}
