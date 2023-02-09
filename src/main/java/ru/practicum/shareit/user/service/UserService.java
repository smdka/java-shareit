package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    UserDto add(User user);

    UserDto updateById(long userId, User patchedUser);

    UserDto getById(long userId);

    Collection<UserDto> getAll();

    void deleteById(long userId);
}
