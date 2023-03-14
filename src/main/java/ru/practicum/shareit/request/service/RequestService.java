package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Service
public interface RequestService {
    ItemRequestDto add(ItemRequest itemRequest);

    List<ItemRequestDto> getByRequesterId(Long requesterId);

    List<ItemRequestDto> getByUserId(Long userId, Long from, Long size);

    ItemRequestDto getById(Long userId, Long requestId);
}
