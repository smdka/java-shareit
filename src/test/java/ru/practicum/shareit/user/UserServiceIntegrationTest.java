package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    private final UserDto userDto = new UserDto(
            null,
            "Frodo",
            "midget@gmail.com");

    @Test
    void createNewUser() {
        UserDto createdUser = userService.add(userDto);

        assertEquals(userDto.getName(), createdUser.getName());
        assertEquals(userDto.getEmail(), createdUser.getEmail());
    }

    @Test
    void getUserByWrongId() {
        assertThrows(UserNotFoundException.class, () -> userService.getById(2L));
    }
}
