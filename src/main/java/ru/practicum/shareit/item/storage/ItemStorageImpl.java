package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.util.StringUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
@Repository
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Map<Long, Item>> items = new HashMap<>();
    private long id;

    @Override
    public Item save(Item item) {
        item.setId(++id);
        long ownerId = item.getOwner().getId();
        items.computeIfAbsent(ownerId, k -> new HashMap<>()).put(id, item);
        log.info("Вещь '{}' пользователя с id = {} добавлена и ей присвоен id = {}", item.getName(), ownerId, id);
        return item;
    }

    @Override
    public void update(Item updatedItem) {
        long ownerId = updatedItem.getOwner().getId();
        long itemId = updatedItem.getId();
        items.get(ownerId).put(itemId, updatedItem);
        log.info("Вещь с id = {} пользователя с id = {} обновлена", itemId, ownerId);
    }

    @Override
    public Optional<Item> findByItemId(long itemId) {
        log.info("Поиск вещи с id = {}", itemId);
        return items.values().stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .filter(item -> item.getId() == itemId)
                .findFirst();
    }

    @Override
    public Collection<Item> findByUserId(long userId) {
        log.info("Поиск вещей пользователя с id = {}", userId);
        Map<Long, Item> itemList = items.get(userId);
        return itemList == null ?
                Collections.emptyList() :
                itemList.values();
    }

    @Override
    public Collection<Item> findByTextAndUserId(String text) {
        log.info("Вещи, содержащие в названии или описании '{}' отправлены", text);
        return text.isBlank() ?
                Collections.emptyList() :
                items.values().stream()
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .filter(item -> (StringUtil.containsTextIgnoreCase(item.getName(), text) ||
                                StringUtil.containsTextIgnoreCase(item.getDescription(), text)) &&
                                item.getAvailable())
                        .collect(toList());
    }
}
