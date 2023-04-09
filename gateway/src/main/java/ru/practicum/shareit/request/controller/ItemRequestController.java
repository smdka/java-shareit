package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.IncomingRequestDto;
import ru.practicum.shareit.request.client.RequestClient;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@Validated
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> post(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                       @Valid @RequestBody IncomingRequestDto incomingRequestDto) {
        log.info("Получен запрос POST /requests от пользователя с id = {}", requesterId);
        return requestClient.add(requesterId, incomingRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getByRequesterId(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Получен запрос GET /requests от пользователя с id = {}", requesterId);
        return requestClient.getAllByUserId(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Получен запрос GET /requests/all?from={}&size={} от пользователя с id = {}", from, size, userId);
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long requestId) {
        log.info("Получен запрос GET /requests/{} от пользователя с id = {}", requestId, userId);
        return requestClient.getByRequestId(userId, requestId);
    }
}
