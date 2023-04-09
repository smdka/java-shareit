package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OutcomingItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private final ItemDto itemDto = new ItemDto(
            1L,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            null);

    private final OutcomingItemDto outcomingItemDto = new OutcomingItemDto(
            1L,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            null,
            null,
            Collections.emptyList());

    private final UserDto userDto = new UserDto(
            1L,
            "Igor",
            "igor@gmail.dom");

    private final CommentDto commentDto = new CommentDto(
            1L,
            "Коммент",
            "Igor",
            LocalDateTime.now()
                    .withNano(0));

    @BeforeEach
    void setup() {
        when(userService.getById(anyLong())).thenReturn(userDto);
    }

    @Test
    void addItem() throws Exception {
        when(itemService.add(any(ItemDto.class), anyLong())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void getAllItemsByUserId() throws Exception {
        when(itemService.getByUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(outcomingItemDto));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(outcomingItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(outcomingItemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(outcomingItemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(outcomingItemDto.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", is(outcomingItemDto.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(outcomingItemDto.getNextBooking())))
                .andExpect(jsonPath("$[0].comments", hasSize(0)));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.getByItemId(anyLong(), anyLong())).thenReturn(outcomingItemDto);
        when(itemService.addComment(anyString(), anyLong(), anyLong())).thenReturn(commentDto);

        mvc.perform(post("/items/{id}/comment", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getByItemId(anyLong(), anyLong())).thenReturn(outcomingItemDto);

        mvc.perform(get("/items/{id}", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outcomingItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(outcomingItemDto.getName())))
                .andExpect(jsonPath("$.description", is(outcomingItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(outcomingItemDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(outcomingItemDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(outcomingItemDto.getNextBooking())))
                .andExpect(jsonPath("$.comments", hasSize(0)));
    }

    @Test
    void getItemByText() throws Exception {
        when(itemService.searchInNameOrDescription(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void updateItemById() throws Exception {
        when(itemService.updateById(anyLong(), any(ItemDto.class), anyLong())).thenReturn(itemDto);

        mvc.perform(patch("/items/{id}", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }
}
