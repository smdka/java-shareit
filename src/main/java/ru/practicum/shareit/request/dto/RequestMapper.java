package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class RequestMapper {
    private RequestMapper() {
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated());
    }

    public static List<ItemRequestDto> toDtoAll(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(RequestMapper::toDto)
                .collect(toList());
    }

    public static ItemRequest toRequest(String description, Long requesterId) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequesterId(requesterId);
        itemRequest.setDescription(description);
        return itemRequest;
    }
}
