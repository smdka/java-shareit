package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Service
public interface RequestService {
    ItemRequestDto add(String description, Long requesterId);

    List<ItemRequestDto> getByRequesterId(Long requesterId);

    List<ItemRequestDto> getByUserId(Long userId, Integer from, Integer size);

    ItemRequestDto getById(Long userId, Long requestId);
}
