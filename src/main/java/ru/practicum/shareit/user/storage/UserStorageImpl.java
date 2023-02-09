package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class UserStorageImpl implements UserStorage {
    public static final Map<Long, User> users = new HashMap<>();
    private long id;

    @Override
    public boolean isExist(long userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean isEmailExist(String email) {
        return !users.isEmpty() && users.values().stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public User save(User user) {
        user.setId(++id);
        users.put(id, user);
        log.info("Пользователь добавлен и ему присвоен id = {}", id);
        return user;
    }

    @Override
    public User updateById(long userId, User userWithUpdates) {
        User userToUpdate = users.get(userId);
        userToUpdate.updateFrom(userWithUpdates);
        log.info("Пользователь с id = {} обновлен", userId);
        return userToUpdate;
    }

    @Override
    public Optional<User> findById(long userId) {
        log.info("Пользователь с id = {} отправлен", userId);
        User user = users.get(userId);
        return user == null ?
                Optional.empty() :
                Optional.of(user);
    }

    @Override
    public Collection<User> findAll() {
        log.info("Список всех пользователей в количестве {} шт. отправлен", users.keySet().size());
        return users.values();
    }

    @Override
    public void deleteById(long userId) {
        users.remove(userId);
        log.info("Пользователь с id = {} удален", userId);
    }
}
