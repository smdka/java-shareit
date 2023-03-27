package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private static final String USER_NOT_FOUND_MSG = "Пользователь с id = %d не найден";
    private static final String REQUEST_NOT_FOUND_MSG = "Запрос с id = %d не найден";

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public ItemRequestDto add(String description, Long requesterId) {
        if (userRepository.existsById(requesterId)) {
            return RequestMapper.toDto(requestRepository.save(RequestMapper.toRequest(description, requesterId)));
        }
        throw new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, requesterId));
    }

    @Override
    public List<ItemRequestDto> getByRequesterId(Long requesterId) {
        if (userRepository.existsById(requesterId)) {
            return RequestMapper.toDtoAll(requestRepository.findByRequesterIdOrderByCreatedAsc(requesterId));
        }
        throw new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, requesterId));
    }

    @Override
    public List<ItemRequestDto> getByUserId(Long userId, Integer from, Integer size) {
        if (userRepository.existsById(userId)) {
            List<ItemRequest> requests = requestRepository.findByRequesterIdNotOrderByCreatedAsc(userId, PageRequest.of(from, size));
            return RequestMapper.toDtoAll(requests);
        }
        throw new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId));
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        if (userRepository.existsById(userId)) {
            return RequestMapper.toDto(requestRepository.findById(requestId)
                    .orElseThrow(() -> new RequestNotFoundException(String.format(REQUEST_NOT_FOUND_MSG, requestId))));
        }
        throw new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId));
    }
}
