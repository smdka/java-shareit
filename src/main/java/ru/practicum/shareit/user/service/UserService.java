package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    User add(User user);

    User updateById(long userId, User patchedUser);

    User getById(long userId);

    Collection<User> getAll();

    void deleteById(long userId);
}
