package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private final UserDto userDto = new UserDto(
            null,
            "Snoop Dogg",
            "d_o_double_g@gmail.com");

    private final User user = new User(
            1L,
            "Snoop Dogg",
            "d_o_double_g@gmail.com",
            Collections.emptyList());

    @Test
    void createUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto createdUser = userService.add(userDto);

        assertNotNull(createdUser);
        assertEquals(1, createdUser.getId());
        assertEquals(userDto.getName(), createdUser.getName());
        assertEquals(userDto.getEmail(), createdUser.getEmail());

        verify(userRepository).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> users = new ArrayList<>(userService.getAll());

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(1, users.get(0).getId());
        assertEquals(userDto.getName(), users.get(0).getName());
        assertEquals(userDto.getEmail(), users.get(0).getEmail());

        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto user = userService.getById(1L);

        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());

        verify(userRepository).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserById() {
        UserDto newUpdatedUserDto = new UserDto(null, "Dr Dre", "dr@dre.com");
        User newUpdatedUser = new User(1L, "Dr Dre", "dr@dre.com", Collections.emptyList());

        when(userRepository.save(any(User.class))).thenReturn(newUpdatedUser);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto updatedUser = userService.updateById( 1L, newUpdatedUserDto);

        assertNotNull(updatedUser);
        assertEquals(1, updatedUser.getId());
        assertEquals(newUpdatedUserDto.getName(), updatedUser.getName());
        assertEquals(newUpdatedUserDto.getEmail(), updatedUser.getEmail());

        verify(userRepository).save(any(User.class));
        verify(userRepository).findById(anyLong());
    }

    @Test
    void deleteUserById() {
        userService.deleteById(1L);

        verify(userRepository).deleteById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }
}
