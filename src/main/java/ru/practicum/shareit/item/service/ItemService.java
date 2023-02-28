package ru.practicum.shareit.item.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ItemService {
    Item add(Item item);

    Item updateById(long itemId, Item itemWithUpdates);

    Map<Item, List<Booking>> getByItemId(long itemId, long userId);

    Collection<Item> getByUserId(long userId);

    Collection<Item> searchInNameOrDescription(String text);
}
