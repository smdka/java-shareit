package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item save(Item item);

    Item updateByItemId(Item updatedItem);

    Optional<Item> findByItemId(long itemId);

    Collection<Item> findByUserId(long userId);

    Collection<Item> findByTextAndUserId(String text);
}
