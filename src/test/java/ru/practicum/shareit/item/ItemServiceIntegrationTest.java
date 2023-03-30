package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserHasNoPermissionException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIntegrationTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestService requestService;


    private final UserDto userDto = new UserDto(
            null,
            "Igor",
            "igor@gmail.dom");

    private final ItemDto itemDto = new ItemDto(
            null,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            1L);

    private final ItemDto itemDto2 = new ItemDto(
            null,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            2L);

    private final UserDto userDto2 = new UserDto(
            null,
            "Stas",
            "stas@gmail.dom");

    private final ItemDto itemDtoToRequest = new ItemDto(
            null,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            1L);

    private final ItemRequestDto requestDto = new ItemRequestDto(
            1L,
            "Какой-то запрос",
            null,
            Collections.emptyList());

    private final CommentDto commentDto = new CommentDto(
            null,
            "Коммент",
            null,
            null);

    @Test
    void createItem() {
        UserDto createdUser = userService.add(userDto);
        ItemDto createdItem = itemService.add(itemDto, createdUser.getId());

        assertEquals(1L, createdItem.getId());
        assertEquals(itemDto.getName(), createdItem.getName());
        assertEquals(itemDto.getDescription(), createdItem.getDescription());
    }

    @Test
    void addItemToRequest() {
        UserDto createdUser = userService.add(userDto);
        requestService.add(requestDto.getDescription(), createdUser.getId());

        ItemDto createdItemToRequest = itemService.add(itemDtoToRequest, createdUser.getId());

        assertEquals(1L, createdItemToRequest.getRequestId());
        assertEquals(itemDtoToRequest.getName(), createdItemToRequest.getName());
        assertEquals(itemDtoToRequest.getDescription(), createdItemToRequest.getDescription());
    }

    @Test
    void getItemByWrongItemId() {
        Long id = 2L;

        assertThrows(ItemNotFoundException.class, () -> itemService.getByItemId(id, id));
    }

    @Test
    void noPermission() {
        UserDto addedUser1 = userService.add(userDto);
        ItemDto addedItem = itemService.add(itemDto, addedUser1.getId());

        UserDto addedUser2 = userService.add(userDto2);

        assertThrows(
                UserHasNoPermissionException.class,
                () -> itemService.updateById(addedItem.getId(), itemDto, addedUser2.getId()));
    }
}
