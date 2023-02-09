package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class ItemMapper {
    private ItemMapper() {

    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static Collection<ItemDto> toItemDto(Collection<? extends Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }
}
