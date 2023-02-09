package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

import static java.util.stream.Collectors.*;

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
        Item item = itemsByUserId.stream()
                .filter(i -> i.getId() == itemId)
                .findFirst()
                .orElseThrow();
        item.updateFrom(itemWithUpdates);
        items.put(userId, itemsByUserId);
        log.info("Вещь с id = {} пользователя с id = {} обновлена", itemId, userId);
        return item;
    }

    @Override
    public Optional<Item> findByItemId(long itemId, long userId) {
        log.info("Вещь с id = {} пользователя с id = {} отправлена", itemId, userId);
        return items.get(userId).stream()
                .filter(i -> i.getId() == itemId)
                .findFirst();
    }

    @Override
    public Collection<Item> findByUserId(long userId) {
        log.info("Вещи пользователя с id = {} отправлена", userId);
        List<Item> itemsByUserId = items.get(userId);
        return itemsByUserId == null ?
                Collections.emptyList() :
                itemsByUserId;
    }

    @Override
    public Collection<Item> findByTextAndUserId(String text, long userId) {
        List<Item> itemsByUserId = items.get(userId);
        return itemsByUserId.stream()
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
