package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    boolean isExist(long userId);
    boolean isEmailExist(String email);

    User save(User user);

    User updateById(long userId, User userWithUpdates);

    Optional<User> findById(long userId);

    Collection<User> findAll();

    void deleteById(long userId);
}
