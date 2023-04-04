package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.UserEmailAlreadyExist;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND_MSG = "Пользователь с id = %d не найден";
    private static final String EMAIL_EXISTS_MSG = "Пользователь с email = %s уже существует";
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto add(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    @Transactional
    public UserDto updateById(long userId, UserDto userWithUpdates) {
        User patchedUser = UserMapper.toUser(userWithUpdates);
        User currUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));

        ifNotSameEmailsOrEmailExistsThrowException(patchedUser, currUser);

        updateFromDto(currUser, patchedUser);

        userRepository.save(currUser);
        return UserMapper.toUserDto(currUser);
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
    public UserDto getById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));
        return UserMapper.toUserDto(user);
    }

    @Override
    public Collection<UserDto> getAll() {
        return UserMapper.toUserDtoAll((Collection<User>) userRepository.findAll());
    }

    @Override
    @Transactional
    public void deleteById(long userId) {
        userRepository.deleteById(userId);
    }

    private void updateFromDto(User userToUpdate, User userWithUpdates) {
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
