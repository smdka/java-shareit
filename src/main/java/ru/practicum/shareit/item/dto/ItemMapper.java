package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Set;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;

public final class ItemMapper {
    private ItemMapper() {
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static Collection<ItemDto> toItemDtoAll(Collection<? extends Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    public static Item toItem(ItemDto itemDto, long ownerId) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        User user = new User();
        user.setId(ownerId);
        item.setOwner(user);
        return item;
    }

    public static OutcomingItemDto toOutputItemDto(Item item, Booking lastBooking, Booking nextBooking) {
        BookingDtoForItem last = null;
        if (lastBooking != null) {
            last = new BookingDtoForItem(lastBooking.getId(), lastBooking.getBooker().getId());
        }
        BookingDtoForItem next = null;
        if (nextBooking != null) {
            next = new BookingDtoForItem(nextBooking.getId(), nextBooking.getBooker().getId());
        }
        Set<CommentDto> comments = item.getComments().stream()
                .map(comment -> CommentMapper.toCommentDto(comment, item.getOwner().getName()))
                .collect(toSet());
        return new OutcomingItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), last, next, comments);
    }
}
