package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item save(long userId, Item item);

    Item updateByItemId(long itemId, long userId, Item itemWithUpdates);

    Optional<Item> findByItemId(long itemId);

    Collection<Item> findByUserId(long userId);

    Collection<Item> findByTextAndUserId(String text);
}
