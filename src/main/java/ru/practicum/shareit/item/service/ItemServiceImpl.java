package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private static final String USER_NOT_FOUND_MSG = "Пользователь с id = %d не найден";
    private static final String ITEM_NOT_FOUND_MSG = "Вещь с id = %d не найдена";
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto add(long userId, Item item) {
        return getIfUserExists(userId, () -> ItemMapper.toItemDto(itemStorage.save(userId, item)));
    }

    private <T> T getIfUserExists(long userId, Supplier<T> s) {
        if (userStorage.isUserExist(userId)) {
            return s.get();
        }
        throw new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId));
    }

    @Override
    public ItemDto updateById(long itemId, long userId, Item itemWithUpdates) {
        return getIfUserExists(userId,
                () -> ItemMapper.toItemDto(itemStorage.updateByItemId(itemId, userId, itemWithUpdates)));
    }

    @Override
    public ItemDto getByItemId(long itemId, long userId) {
        return ItemMapper.toItemDto(itemStorage.findByItemId(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MSG, itemId))));
    }

    @Override
    public Collection<ItemDto> getByUserId(long userId) {
        return getIfUserExists(userId, () -> ItemMapper.toItemDto(itemStorage.findByUserId(userId)));
    }

    @Override
    public Collection<ItemDto> findIfContainsTextInNameOrDescription(String text) {
            return ItemMapper.toItemDto(itemStorage.findByTextAndUserId(text));
    }
}
