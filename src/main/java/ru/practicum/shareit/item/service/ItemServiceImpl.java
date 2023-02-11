package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserHasNoPermissionException;
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
    private static final String NO_PERMISSION_MSG = "У пользователя с id = %d нет прав на изменение вещи с id = %d";
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Item add(Item item) {
        return getIfUserExists(item.getOwnerId(), () -> itemStorage.save(item));
    }

    private <T> T getIfUserExists(long userId, Supplier<T> s) {
        if (userStorage.isUserExist(userId)) {
            return s.get();
        }
        throw new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId));
    }

    @Override
    public Item updateById(long itemId, long userId, ItemDto itemWithUpdates) {
        Item currItem = getIfUserExists(userId, () -> itemStorage.findByItemId(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MSG, itemId))));
        if (currItem.getOwnerId() != userId) {
            throw new UserHasNoPermissionException(String.format(NO_PERMISSION_MSG,userId, itemId));
        }
        updateFromDto(currItem, itemWithUpdates);
        return itemStorage.updateByItemId(currItem);
    }

    @Override
    public Item getByItemId(long itemId, long userId) {
        return itemStorage.findByItemId(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MSG, itemId)));
    }

    @Override
    public Collection<Item> getByUserId(long userId) {
        return getIfUserExists(userId, () -> itemStorage.findByUserId(userId));
    }

    @Override
    public Collection<Item> findIfContainsTextInNameOrDescription(String text) {
            return itemStorage.findByTextAndUserId(text);
    }

    public void updateFromDto(Item item, ItemDto itemDto) {
        String newName = itemDto.getName();
        if (newName != null) {
            item.setName(newName);
        }

        String newDescription = itemDto.getDescription();
        if (newDescription != null) {
            item.setDescription(newDescription);
        }

        Boolean newAvailable = itemDto.getAvailable();
        if (newAvailable != null) {
            item.setAvailable(newAvailable);
        }
    }
}
