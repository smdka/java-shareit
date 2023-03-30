package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingsGetter;
import ru.practicum.shareit.booking.service.State;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.OutcomingItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.UserChecker;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForBooking;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserChecker userChecker;

    @Mock
    private BookingsGetter bookingsGetter;

    private final UserDto userDto = new UserDto(
            1L,
            "Igor",
            "igor@gmail.dom");

    private final UserDtoForBooking userDtoForBooking = new UserDtoForBooking(1L);

    private final User user = new User(
            1L,
            "Igor",
            "igor@gmail.dom",
            Collections.emptyList());

    private final Item item = new Item(
            1L,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            user,
            1L,
            Collections.emptyList());

    private final Comment comment = new Comment(
            1L,
            "Какой-то текст",
            item,
            user,
            LocalDateTime.now());

    private final CommentDto commentDto = new CommentDto(
            null,
            "Какой-то текст",
            "Igor",
            LocalDateTime.now());

    private final ItemDto itemDto = new ItemDto(
            1L,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            1L);

    private final ItemDtoForBooking itemDtoForBooking = new ItemDtoForBooking(1L, "Какая-то вещь");

    private final Booking booking = new Booking(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            item,
            user,
            Booking.Status.WAITING);

    private final OutcomingBookingDto bookingOutputDto = new OutcomingBookingDto(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Booking.Status.WAITING,
            userDtoForBooking,
            itemDtoForBooking);


    @Test
    void addItem() {
        when(userChecker.getIfExists(anyLong(), any())).thenReturn(item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ItemDto createdItem = itemService.add(itemDto, userDto.getId());

        assertNotNull(createdItem);
        assertEquals(1, createdItem.getId());
        assertEquals(itemDtoForBooking.getName(), createdItem.getName());
        assertTrue(createdItem.getAvailable());
    }

    @Test
    void getByItemIdAndUserId() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingsGetter.forItemOwner(anyCollection(), any(State.class), any(Pageable.class))).thenReturn(List.of(booking));

        OutcomingItemDto itemById = itemService.getByItemId(1L, 1L);

        assertNotNull(itemById);
        assertEquals(1, itemById.getId());
        assertEquals(itemDtoForBooking.getName(), itemById.getName());
        assertTrue(itemById.getAvailable());
        assertEquals(0, itemById.getComments().size());
    }

    @Test
    void getByItemIdAndUserIdWithoutBookings() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        OutcomingItemDto itemById = itemService.getByItemId(1L, 2L);

        assertNotNull(itemById);
        assertEquals(1, itemById.getId());
        assertEquals(itemDtoForBooking.getName(), itemById.getName());
        assertTrue(itemById.getAvailable());
        assertNull(itemById.getLastBooking());
        assertNull(itemById.getNextBooking());
        assertEquals(0, itemById.getComments().size());
    }

    @Test
    void getAllItemsByUserId() {
        when(userChecker.getIfExists(anyLong(), any())).thenReturn(List.of(item));

        List<OutcomingItemDto> items = new ArrayList<>(itemService.getByUserId(1L, 0, 1));

        assertEquals(items.size(), 1);
        assertEquals(1, items.get(0).getId());
        assertEquals(itemDtoForBooking.getName(), items.get(0).getName());
        assertTrue(items.get(0).getAvailable());
        assertEquals(0, items.get(0).getComments().size());
    }

    @Test
    void getAllItemsByText() {
        when(itemRepository.search(anyString(), any(Pageable.class))).thenReturn(List.of(item));

        List<ItemDto> items = new ArrayList<>(itemService.searchInNameOrDescription("Hello", 0, 1));

        assertEquals(1, items.size());
        assertEquals(1, items.get(0).getId());
        assertEquals(itemDtoForBooking.getName(), items.get(0).getName());
        assertTrue(items.get(0).getAvailable());
    }

    @Test
    void getAllItemsByEmptyText() {
        List<ItemDto> items = new ArrayList<>(itemService.searchInNameOrDescription("", 0, 1));

        assertTrue(items.isEmpty());
    }

    @Test
    void updateItem() {
        final Item newItem = new Item(
                1L,
                "Какая-то обновленная вещь",
                "Какое-то описание",
                true,
                user,
                1L,
                Collections.emptyList());

        when(itemRepository.save(any())).thenReturn(newItem);
        when(userChecker.getIfExists(anyLong(), any())).thenReturn(item);

        ItemDto updatedItem = itemService.updateById(item.getId(), ItemMapper.toItemDto(newItem), 1L);

        assertNotNull(updatedItem);
        assertEquals(1, updatedItem.getId());
        assertEquals(newItem.getName(), updatedItem.getName());
        assertTrue(updatedItem.getAvailable());
    }

    @Test
    void updateWithNotFoundItem() {
        final Item newItem = new Item(
                1L,
                "Какая-то обновленная вещь",
                "Какое-то описание",
                true,
                user,
                1L,
                Collections.emptyList());

        when(userChecker.getIfExists(anyLong(), any())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class,
                () -> itemService.updateById(item.getId(), ItemMapper.toItemDto(newItem), 1L));
    }

    @Test
    void addComment() {
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingsGetter.forUser(anyLong(), eq(State.PAST), eq(Pageable.unpaged()))).thenReturn(List.of(booking));

        CommentDto savedComment = itemService.addComment(commentDto.getText(), user.getId(), item.getId());

        assertNotNull(savedComment);
        assertEquals(comment.getText(), savedComment.getText());
        assertEquals(comment.getId(), savedComment.getId());
        assertEquals(comment.getCreatedAt().toString(), savedComment.getCreated().toString());
        assertEquals(comment.getAuthor().getName(), savedComment.getAuthorName());

        verify(commentRepository).save(any(Comment.class));
        verifyNoMoreInteractions(commentRepository);
    }
}
