package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RequestServiceIntegrationTest {
    @Autowired
    private RequestService requestService;
    @Autowired
    private UserService userService;

    private final UserDto userDto = new UserDto(
            null,
            "Maxim Nikolaevitch",
            "lmn@gmail.com");

    private final ItemRequestDto requestDto = new ItemRequestDto(
            1L,
            "Request",
            LocalDateTime.now(),
            Collections.emptyList());

    @Test
    void createRequest() {
        UserDto createdUser = userService.add(userDto);
        ItemRequestDto addedRequest = requestService.add(requestDto.getDescription(), createdUser.getId());

        List<ItemRequestDto> requestDtos = requestService.getByRequesterId(createdUser.getId());

        assertEquals(1, requestDtos.size());
        assertEquals(addedRequest.getId(), requestDtos.get(0).getId());
        assertEquals(requestDto.getDescription(), requestDtos.get(0).getDescription());
        assertEquals(addedRequest.getItems().size(), requestDtos.get(0).getItems().size());
    }

    @Test
    void getRequestByWrongRequestId() {
        UserDto user = userService.add(userDto);

        assertThrows(RequestNotFoundException.class, () -> requestService.getById(user.getId(), 1L));
    }
}
