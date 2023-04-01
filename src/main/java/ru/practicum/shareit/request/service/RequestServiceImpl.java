package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.UserChecker;
import ru.practicum.shareit.util.PageableUtil;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private static final String REQUEST_NOT_FOUND_MSG = "Запрос с id = %d не найден";
    private final RequestRepository requestRepository;
    private final UserChecker userChecker;

    @Override
    @Transactional
    public ItemRequestDto add(String description, Long requesterId) {
        return userChecker.getIfExists(requesterId,
                () -> RequestMapper.toDto(requestRepository.save(RequestMapper.toRequest(description, requesterId))));
    }

    @Override
    public List<ItemRequestDto> getByRequesterId(Long requesterId) {
        return userChecker.getIfExists(requesterId,
                () -> RequestMapper.toDtoAll(requestRepository.findByRequesterIdOrderByCreatedAsc(requesterId)));
    }

    @Override
    public List<ItemRequestDto> getByUserId(Long userId, Integer from, Integer size) {
        return userChecker.getIfExists(userId,
                () -> RequestMapper.toDtoAll(requestRepository.findByRequesterIdNotOrderByCreatedAsc(userId, PageableUtil.getPageRequest(from, size))));
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        return userChecker.getIfExists(userId,
                () -> RequestMapper.toDto(requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(String.format(REQUEST_NOT_FOUND_MSG, requestId)))));
    }
}
