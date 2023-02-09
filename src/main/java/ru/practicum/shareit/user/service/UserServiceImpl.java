package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.UserEmailAlreadyExist;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto add(User user) {
        String email = user.getEmail();
        if (userStorage.isEmailExist(email)) {
            throw new UserEmailAlreadyExist(String.format("Пользователь с email = %s уже существует", email));
        }
        return UserMapper.toUserDto(userStorage.save(user));
    }

    @Override
    public UserDto updateById(long userId, User userWithUpdates) {
        String email = userWithUpdates.getEmail();
        if (userStorage.isEmailExist(email)) {
            throw new UserEmailAlreadyExist(String.format("Пользователь с email = %s уже существует", email));
        }
        return UserMapper.toUserDto(userStorage.updateById(userId, userWithUpdates));
    }

    @Override
    public UserDto getById(long userId) {
        return UserMapper.toUserDto(userStorage.findById(userId).orElseThrow());
    }

    @Override
    public Collection<UserDto> getAll() {
        return UserMapper.toUserDto(userStorage.findAll());
    }

    @Override
    public void deleteById(long userId) {
        userStorage.deleteById(userId);
    }
}
