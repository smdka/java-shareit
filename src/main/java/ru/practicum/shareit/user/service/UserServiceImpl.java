package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.UserEmailAlreadyExist;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND_MSG = "Пользователь с id = %d не найден";
    private static final String EMAIL_EXISTS_MSG = "Пользователь с email = %s уже существует";
    private final UserRepository userRepository;

    @Override
    public User add(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateById(long userId, User userWithUpdates) {
        User currUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));

        ifNotSameEmailsOrEmailExistsThrowException(userWithUpdates, currUser);

        updateFrom(currUser, userWithUpdates);
        userRepository.save(currUser);
        return currUser;
    }

    private void ifNotSameEmailsOrEmailExistsThrowException(User userWithUpdates, User currUser) {
        String newEmail = userWithUpdates.getEmail();
        log.info("Проверка наличия email = {} и его сравнение с email пользователя с id = {}" +
                " перед обновлением пользователя", newEmail, currUser.getId());
        if (!currUser.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new UserEmailAlreadyExist(String.format(EMAIL_EXISTS_MSG, newEmail));
        }
    }

    @Override
    public User getById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));
    }

    @Override
    public Collection<User> getAll() {
        return (Collection<User>) userRepository.findAll();
    }

    @Override
    public void deleteById(long userId) {
        userRepository.deleteById(userId);
    }

    private void updateFrom(User userToUpdate, User userWithUpdates) {
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
