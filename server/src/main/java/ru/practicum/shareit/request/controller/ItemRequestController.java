package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.request.dto.IncomingRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto post(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                               @RequestBody IncomingRequestDto incomingRequestDto) {
        log.info("Получен запрос POST /requests от пользователя с id = {}", requesterId);
        return requestService.add(incomingRequestDto.getDescription(), requesterId);
    }

    @GetMapping
    public List<ItemRequestDto> getByRequesterId(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Получен запрос GET /requests от пользователя с id = {}", requesterId);
        return requestService.getByRequesterId(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос GET /requests/all?from={}&size={} от пользователя с id = {}", from, size, userId);
        return requestService.getByUserId(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long requestId) {
        log.info("Получен запрос GET /requests/{} от пользователя с id = {}", requestId, userId);
        return requestService.getById(userId, requestId);
    }
}
