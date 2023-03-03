package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto add(UserDto userDto);

    UserDto updateById(long userId, UserDto patchedUser);

    UserDto getById(long userId);

    Collection<UserDto> getAll();

    void deleteById(long userId);
}
