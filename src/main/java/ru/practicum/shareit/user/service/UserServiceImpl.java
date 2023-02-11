package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
    public User add(User user) {
        return getIfEmailNotExists(user.getEmail(), () -> userStorage.save(user));
    }

    private <T> T getIfEmailNotExists(String email, Supplier<T> s) {
        if (userStorage.emailExists(email)) {
            throw new UserEmailAlreadyExist(String.format(EMAIL_EXISTS_MSG, email));
        }
        return s.get();
    }

    @Override
    public User updateById(long userId, User userWithUpdates) {
        User currUser = getIfEmailNotExists(userWithUpdates.getEmail(), () -> userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId))));
        updateFrom(currUser, userWithUpdates);
        return userStorage.updateById(currUser);
    }

    @Override
    public User getById(long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.findAll();
    }

    @Override
    public void deleteById(long userId) {
        if (userStorage.deleteById(userId) == null) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId));
        }
    }

    public void updateFrom(User userToUpdate, User userWithUpdates) {
        String newName = userWithUpdates.getName();
        if (newName != null) {
             userToUpdate.setName(newName);
        }

        String newEmail = userWithUpdates.getEmail();
        if (newEmail != null) {
            userToUpdate.setEmail(newEmail);
        }
    }
}
