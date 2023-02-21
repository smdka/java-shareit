package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    boolean hasUser(long userId);

    boolean hasEmail(String email);

    User save(User user);

    void update(User userWithUpdates);

    Optional<User> findById(long userId);

    Collection<User> findAll();

    User deleteById(long userId);
}
