package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.UserEmailAlreadyExist;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND_MSG = "Пользователь с id = %d не найден";
    private static final String EMAIL_EXISTS_MSG = "Пользователь с email = %s уже существует";
    private final UserStorage userStorage;

    @Override
    public UserDto add(User user) {
        return getIfEmailNotExists(user.getEmail(), () -> UserMapper.toUserDto(userStorage.save(user)));
    }

    private UserDto getIfEmailNotExists(String email, Supplier<UserDto> s) {
        if (userStorage.isEmailExist(email)) {
            throw new UserEmailAlreadyExist(String.format(EMAIL_EXISTS_MSG, email));
        }
        return s.get();
    }

    @Override
    public UserDto updateById(long userId, User userWithUpdates) {
        return getIfEmailNotExists(userWithUpdates.getEmail(),
                () -> UserMapper.toUserDto(userStorage.updateById(userId, userWithUpdates)));
    }

    @Override
    public UserDto getById(long userId) {
        return UserMapper.toUserDto(userStorage.findById(userId)
                .orElseThrow(
                        () -> new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId))));
    }

    @Override
    public Collection<UserDto> getAll() {
        return UserMapper.toUserDto(userStorage.findAll());
    }

    @Override
    public void deleteById(long userId) {
        if (userStorage.deleteById(userId) == null) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId));
        }
    }
}
