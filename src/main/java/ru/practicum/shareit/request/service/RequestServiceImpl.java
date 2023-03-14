package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public class RequestServiceImpl implements RequestService {
    @Override
    public ItemRequestDto add(ItemRequest itemRequest) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getByRequesterId(Long requesterId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getByUserId(Long userId, Long from, Long size) {
        return null;
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        return null;
    }
}
