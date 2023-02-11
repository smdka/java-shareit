package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto post(@Valid @RequestBody UserDto user) {
        log.info("Получен запрос POST /users");
        return UserMapper.toUserDto(userService.add(UserMapper.toUser(user)));
    }

    @PatchMapping("/{userId}")
    public UserDto patch(@PathVariable long userId, @RequestBody UserDto patchedUser) {
        log.info("Получен запрос PATCH /users/{}", userId);
        return UserMapper.toUserDto(userService.updateById(userId, UserMapper.toUser(patchedUser)));
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable long userId) {
        log.info("Получен запрос GET /users/{}", userId);
        return UserMapper.toUserDto(userService.getById(userId));
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("Получен запрос GET /users");
        return UserMapper.toUserDtoAll(userService.getAll());
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Получен запрос DELETE /users/{}", userId);
        userService.deleteById(userId);
    }

}
