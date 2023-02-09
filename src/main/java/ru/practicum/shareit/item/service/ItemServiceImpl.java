package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserStorageImpl;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    @Override
    public ItemDto add(long userId, Item item) {
        if (UserStorageImpl.users.containsKey(userId)) {
            return ItemMapper.toItemDto(itemStorage.save(userId, item));
        }
        throw new UserNotFoundException(String.format("Пользователь с id = %d не существует", userId));
    }

    @Override
    public ItemDto updateById(long itemId, long userId, Item itemWithUpdates) {
        if (UserStorageImpl.users.containsKey(userId)) {
            return ItemMapper.toItemDto(itemStorage.updateByItemId(itemId, userId, itemWithUpdates));
        }
        throw new UserNotFoundException(String.format("Пользователь с id = %d не существует", userId));
    }

    @Override
    public ItemDto getByItemId(long itemId, long userId) {
        return ItemMapper.toItemDto(itemStorage.findByItemId(itemId, userId).orElseThrow());
    }

    @Override
    public Collection<ItemDto> getByUserId(long userId) {
        return ItemMapper.toItemDto(itemStorage.findByUserId(userId));
    }

    @Override
    public Collection<ItemDto> findIfContainsTextInNameOrDescription(String text, long userId) {
        return ItemMapper.toItemDto(itemStorage.findByTextAndUserId(text, userId));
    }
}
