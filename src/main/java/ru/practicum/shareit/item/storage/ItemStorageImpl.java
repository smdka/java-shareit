package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
@Repository
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, List<Item>> items = new HashMap<>();
    private long id;

    @Override
    public Item save(long userId, Item item) {
        item.setId(++id);
        items.computeIfAbsent(userId, k -> new ArrayList<>()).add(item);
        log.info("Вещь '{}' пользователя с id = {} добавлена и ей присвоен id = {}",item.getName(), userId, id);
        return item;
    }

    @Override
    public Item updateByItemId(long itemId, long userId, Item itemWithUpdates) {
        List<Item> itemsByUserId = items.get(userId);
        if (itemsByUserId == null) {
            throw new ItemNotFoundException(String.format("У пользователя с id = %d нет вещей", userId));
        }
        Item item = itemsByUserId.stream()
                .filter(i -> i.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id = %d не существует", itemId)));
        item.updateFrom(itemWithUpdates);
        log.info("Вещь с id = {} пользователя с id = {} обновлена", itemId, userId);
        return item;
    }

    @Override
    public Optional<Item> findByItemId(long itemId) {
        return items.values().stream()
                .flatMap(List::stream)
                .filter(item -> item.getId() == itemId)
                .findFirst();
    }

    @Override
    public Collection<Item> findByUserId(long userId) {
        log.info("Вещи пользователя с id = {} отправлены", userId);
        List<Item> itemList = items.get(userId);
        return itemList == null ?
                Collections.emptyList() :
                itemList;
    }

    @Override
    public Collection<Item> findByTextAndUserId(String text) {
        log.info("Вещи, содержащие в названии или описании '{}' отправлены", text);
        return text.isBlank() ?
                Collections.emptyList() :
                items.values().stream()
                        .flatMap(List::stream)
                        .filter(item -> (isNameContains(text, item) || isDescriptionContains(text, item))
                                && item.getAvailable())
                        .collect(toList());
    }

    private boolean isDescriptionContains(String text, Item item) {
        return item.getDescription().toLowerCase().contains(text.toLowerCase());
    }

    private boolean isNameContains(String text, Item item) {
        return item.getName().toLowerCase().contains(text.toLowerCase());
    }
}
