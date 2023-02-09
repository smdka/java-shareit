package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class UserMapper {
    private UserMapper() {

    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static Collection<UserDto> toUserDto(Collection<? extends User> users) {
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(toList());
    }
}
