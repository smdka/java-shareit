package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.util.StringUtil;

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
    public Item save(Item item) {
        item.setId(++id);
        long ownerId = item.getOwnerId();
        items.computeIfAbsent(ownerId, k -> new ArrayList<>()).add(item);
        log.info("Вещь '{}' пользователя с id = {} добавлена и ей присвоен id = {}",item.getName(), ownerId, id);
        return item;
    }

    @Override
    public Item updateByItemId(long itemId, long userId, Item updatedItem) {
        List<Item> itemList = items.get(userId);
        itemList.add(updatedItem);
        log.info("Вещь с id = {} пользователя с id = {} обновлена", itemId, userId);
        return updatedItem;
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
                        .filter(item -> (StringUtil.containsTextIgnoreCase(item.getName(), text) ||
                                StringUtil.containsTextIgnoreCase(item.getDescription(), text)) &&
                                item.getAvailable())
                        .collect(toList());
    }
}
