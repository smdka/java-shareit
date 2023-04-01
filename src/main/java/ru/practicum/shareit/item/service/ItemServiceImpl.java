package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingsGetter;
import ru.practicum.shareit.booking.service.State;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.OutcomingItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserHasNoPermissionException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserChecker;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.PageableUtil;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private static final String USER_NOT_FOUND_MSG = "Пользователь с id = %d не найден";
    private static final String ITEM_NOT_FOUND_MSG = "Вещь с id = %d не найдена";
    private static final String NO_PERMISSION_MSG = "У пользователя с id = %d нет прав на изменение вещи с id = %d";
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingsGetter bookingsGetter;
    private final CommentRepository commentRepository;
    private final UserChecker userChecker;

    @Override
    @Transactional
    public ItemDto add(ItemDto itemDto, Long ownerId) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, ownerId)));
        Item item = ItemMapper.toItem(itemDto, user);
        return ItemMapper.toItemDto(userChecker.getIfExists(ownerId, () -> itemRepository.save(item)));
    }

    @Transactional
    public ItemDto updateById(Long itemId, ItemDto itemWithUpdates, Long ownerId) {
        Item currItem = userChecker.getIfExists(ownerId, () -> itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MSG, itemId))));

        checkForUserPermissionOrThrowException(ownerId, currItem);

        updateFromDto(currItem, itemWithUpdates);

        return ItemMapper.toItemDto(itemRepository.save(currItem));
    }

    private void checkForUserPermissionOrThrowException(long ownerId, Item currItem) {
        long itemId = currItem.getId();
        log.info("Проверка полномочий пользователя с id = {} для изменения вещи с id = {}", ownerId, itemId);
        if (currItem.getOwner().getId() != ownerId) {
            throw new UserHasNoPermissionException(String.format(NO_PERMISSION_MSG, ownerId, itemId));
        }
    }

    @Override
    public OutcomingItemDto getByItemId(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MSG, itemId)));
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (item.getOwner().getId().equals(userId)) {
            lastBooking = getLastBooking(itemId);
            nextBooking = getNextBooking(itemId);
        }
        return ItemMapper.toOutputItemDto(item, lastBooking, nextBooking);
    }

    private Booking getLastBooking(long itemId) {
        return bookingsGetter.forItemOwner(List.of(itemId), State.ALL, Pageable.unpaged()).stream()
                .filter(booking -> (!Booking.Status.REJECTED.equals(booking.getStatus())) &&
                        booking.getStart().isBefore(LocalDateTime.now()))
                .max(comparing(Booking::getEnd))
                .orElse(null);
    }

    private Booking getNextBooking(long itemId) {
        return bookingsGetter.forItemOwner(List.of(itemId), State.ALL, Pageable.unpaged()).stream()
                .filter(booking -> (!Booking.Status.REJECTED.equals(booking.getStatus())) &&
                        booking.getStart().isAfter(LocalDateTime.now()))
                .min(comparing(Booking::getStart))
                .orElse(null);
    }


    @Override
    public Collection<OutcomingItemDto> getByUserId(long userId, Integer from, Integer size) {
        Collection<Item> items = userChecker.getIfExists(userId,
                () -> itemRepository.findByOwnerIdOrderByIdAsc(userId, PageRequest.of(from / size, size)));
        return items.stream()
                .map(item -> ItemMapper.toOutputItemDto(item, getLastBooking(item.getId()), getNextBooking(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchInNameOrDescription(String text, Integer from, Integer size) {
        return text.isBlank() ?
                Collections.emptyList() :
                ItemMapper.toItemDtoAll(itemRepository.search(text, PageableUtil.getPageRequest(from, size)));
    }

    @Override
    @Transactional
    public CommentDto addComment(String text, Long authorId, Long itemId) {
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, authorId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MSG, itemId)));
        Collection<Booking> bookings = bookingsGetter.forUser(authorId, State.PAST, Pageable.unpaged());

        ifNotBookingAuthorThrowNoPermissionException(itemId, bookings);

        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(text, user, item)));
    }

    private void ifNotBookingAuthorThrowNoPermissionException(Long itemId, Collection<Booking> bookings) {
        boolean isBookingAuthor = bookings.stream()
                .anyMatch(b -> b.getItem().getId().equals(itemId) &&
                        !b.getStatus().equals(Booking.Status.REJECTED));
        if (!isBookingAuthor) {
            throw new UserHasNoPermissionException("Пользователь не может оставлять комментарий");
        }
    }

    private void updateFromDto(Item item, ItemDto itemDto) {
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
