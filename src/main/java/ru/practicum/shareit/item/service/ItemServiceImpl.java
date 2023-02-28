package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.State;
import ru.practicum.shareit.booking.service.Strategy;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserHasNoPermissionException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private static final String USER_NOT_FOUND_MSG = "Пользователь с id = %d не найден";
    private static final String ITEM_NOT_FOUND_MSG = "Вещь с id = %d не найдена";
    private static final String NO_PERMISSION_MSG = "У пользователя с id = %d нет прав на изменение вещи с id = %d";
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final Strategy strategy;

    @Override
    public Item add(Item item) {
        return getIfUserExists(item.getOwner().getId(), () -> itemRepository.save(item));
    }

    private <T> T getIfUserExists(long userId, Supplier<T> s) {
        if (userRepository.existsById(userId)) {
            return s.get();
        }
        throw new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId));
    }

    @Override
    public Item updateById(long itemId, Item itemWithUpdates) {
        long ownerId = itemWithUpdates.getOwner().getId();
        Item currItem = getIfUserExists(ownerId, () -> itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MSG, itemId))));

        checkForUserPermissionOrThrowException(ownerId, currItem);

        updateFromDto(currItem, itemWithUpdates);
        itemRepository.save(currItem);
        return currItem;
    }

    private void checkForUserPermissionOrThrowException(long ownerId, Item currItem) {
        long itemId = currItem.getId();
        log.info("Проверка полномочий пользователя с id = {} для изменения вещи с id = {}", ownerId, itemId);
        if (currItem.getOwner().getId() != ownerId) {
            throw new UserHasNoPermissionException(String.format(NO_PERMISSION_MSG, ownerId, itemId));
        }
    }

    @Override
    public Map<Item, List<Booking>> getByItemId(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MSG, itemId)));
        Booking nextBooking = null;
        Booking lastBooking = null;
        if (item.getOwner().getId().equals(userId)) {
            nextBooking = strategy.forItemOwner(List.of(itemId), State.FUTURE).stream()
                    .findFirst()
                    .orElse(null);
            lastBooking = strategy.forItemOwner(List.of(itemId), State.PAST).stream()
                    .findFirst()
                    .orElse(null);
        }
        return Map.of(item, List.of(lastBooking, nextBooking));
    }

    @Override
    public Collection<Item> getByUserId(long userId) {
        return getIfUserExists(userId, () -> itemRepository.findItemsByOwnerId(userId));
    }

    @Override
    public Collection<Item> searchInNameOrDescription(String text) {
        return text.isBlank() ?
                Collections.emptyList() :
                itemRepository.search(text);
    }

    private void updateFromDto(Item item, Item itemDto) {
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
