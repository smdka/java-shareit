package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto save(@Valid @RequestBody UserDto user) {
        log.info("Получен запрос POST /users");
        return userService.add(UserMapper.toUser(user));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody UserDto patchedUser) {
        log.info("Получен запрос PATCH /users/{}", userId);
        return userService.updateById(userId, UserMapper.toUser(patchedUser));
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable long userId) {
        log.info("Получен запрос GET /users/{}", userId);
        return userService.getById(userId);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("Получен запрос GET /users");
        return userService.getAll();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Получен запрос DELETE /users/{}", userId);
        userService.deleteById(userId);
    }

}
