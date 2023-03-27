package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.IncomingRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestService requestService;
    @PostMapping
    public ItemRequestDto post(@NotNull @RequestHeader("X-Sharer-User-Id") Long requesterId,
                               @Valid @RequestBody IncomingRequestDto incomingRequestDto) {
        log.info("Получен запрос POST /requests от пользователя с id = {}", requesterId);
        return requestService.add(incomingRequestDto.getDescription(), requesterId);
    }

    @GetMapping
    public List<ItemRequestDto> getByRequesterId(@NotNull @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Получен запрос GET /requests от пользователя с id = {}", requesterId);
        return requestService.getByRequesterId(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                       @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Получен запрос GET /requests/all?from={}&size={} от пользователя с id = {}", from, size, userId);
        return requestService.getByUserId(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long requestId) {
        log.info("Получен запрос GET /requests/{} от пользователя с id = {}", requestId, userId);
        return requestService.getById(userId, requestId);
    }
}
