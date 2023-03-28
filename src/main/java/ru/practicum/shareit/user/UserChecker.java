package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class UserChecker {
    private final UserRepository userRepository;
    public <T> T getIfExists(Long userId, Supplier<T> s) {
        if (userRepository.existsById(userId)) {
            return s.get();
        }
        throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId));
    }
}
