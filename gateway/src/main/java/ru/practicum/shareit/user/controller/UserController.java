package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> post(@Valid @RequestBody UserDto user) {
        log.info("Получен запрос POST /users");
        return userClient.add(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> patch(@PathVariable Long userId,
                                        @RequestBody UserDto patchedUser) {
        log.info("Получен запрос PATCH /users/{}", userId);
        return userClient.update(userId, patchedUser);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable Long userId) {
        log.info("Получен запрос GET /users/{}", userId);
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Получен запрос GET /users");
        return userClient.getAll();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        log.info("Получен запрос DELETE /users/{}", userId);
        return userClient.deleteById(userId);
    }
}
