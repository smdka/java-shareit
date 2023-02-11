package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users;
    private long id;

    @Override
    public boolean userExists(long userId) {
        log.info("Проверка наличия пользователя с id = {}", userId);
        return users.containsKey(userId);
    }

    @Override
    public boolean emailExists(String email) {
        log.info("Проверка наличия email = {}", email);
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
    public User updateById(User userWithUpdates) {
        long userId = userWithUpdates.getId();
        log.info("Обновление пользователя с id = {}", userId);
        User userToUpdate = users.get(userId);
        userToUpdate = userWithUpdates;
        log.info("Пользователь с id = {} обновлен", userId);
        return userToUpdate;
    }

    @Override
    public Optional<User> findById(long userId) {
        log.info("Поиск пользователя с id = {}", userId);
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
    public User deleteById(long userId) {
        log.info("Удаление пользователя с id = {}", userId);
        return users.remove(userId);
    }
}
