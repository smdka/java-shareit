package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForBooking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
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
    void createItem() {
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto createdItem = itemService.add(itemDto, userDto.getId());

        assertNotNull(createdItem);
        assertEquals(1, createdItem.getId());
        assertEquals(itemDtoForBooking.getName(), createdItem.getName());
        assertTrue(createdItem.getAvailable());

        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getByItemIdAndUserId() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(bookingRepository.getLastBooking(anyList(), any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.getNextBooking(anyList(), any(LocalDateTime.class))).thenReturn(List.of(booking));

        ItemDto itemById = itemService.getByItemIdAndUserId(1L, 1L);

        assertNotNull(itemById);
        assertEquals(1, itemById.getId());
        assertEquals(itemDtoForBooking.getName(), itemById.getName());
        assertEquals(itemDtoForBooking.getDescription(), itemById.getDescription());
        assertTrue(itemById.getAvailable());
        assertEquals(itemDtoForBooking.getOwnerId(), itemById.getOwnerId());
        assertEquals(bookingOutputDto.getId(), itemById.getLastBooking().getId());
        assertEquals(bookingOutputDto.getId(), itemById.getNextBooking().getId());
        assertEquals(0, itemById.getComments().size());
        assertEquals(itemDtoForBooking.getRequestId(), itemById.getRequestId());

        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(itemRepository);
        verify(bookingRepository, times(1)).getLastBooking(anyList(), any(LocalDateTime.class));
        verify(bookingRepository, times(1)).getNextBooking(anyList(), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getByItemIdAndUserIdWithoutBookings() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto itemById = itemService.getByItemIdAndUserId(1L, 2L);

        assertNotNull(itemById);
        assertEquals(1, itemById.getId());
        assertEquals(itemDtoForBooking.getName(), itemById.getName());
        assertEquals(itemDtoForBooking.getDescription(), itemById.getDescription());
        assertTrue(itemById.getAvailable());
        assertEquals(itemDtoForBooking.getOwnerId(), itemById.getOwnerId());
        assertNull(itemById.getLastBooking());
        assertNull(itemById.getNextBooking());
        assertEquals(0, itemById.getComments().size());
        assertEquals(itemDtoForBooking.getRequestId(), itemById.getRequestId());

        verify(itemRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getAllItemsByUserId() {
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), any(Pageable.class))).thenReturn(List.of(item));
        when(bookingRepository.getLastBooking(anyList(), any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.getNextBooking(anyList(), any(LocalDateTime.class))).thenReturn(List.of(booking));

        List<ItemDto> items = itemService.getAllByUserId(1L, 0, 1);

        assertEquals(items.size(), 1);
        assertEquals(1, items.get(0).getId());
        assertEquals(itemDtoForBooking.getName(), items.get(0).getName());
        assertEquals(itemDtoForBooking.getDescription(), items.get(0).getDescription());
        assertTrue(items.get(0).getAvailable());
        assertEquals(itemDtoForBooking.getOwnerId(), items.get(0).getOwnerId());
        assertEquals(bookingOutputDto.getId(), items.get(0).getLastBooking().getId());
        assertEquals(bookingOutputDto.getId(), items.get(0).getNextBooking().getId());
        assertEquals(0, items.get(0).getComments().size());
        assertEquals(itemDtoForBooking.getRequestId(), items.get(0).getRequestId());

        verify(itemRepository, times(1)).findAllByOwnerIdOrderByIdAsc(anyLong(), any(Pageable.class));
        verifyNoMoreInteractions(itemRepository);
        verify(bookingRepository, times(1)).getLastBooking(anyList(), any(LocalDateTime.class));
        verify(bookingRepository, times(1)).getNextBooking(anyList(), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllItemsByRequestId() {
        when(itemRepository.findAllByRequestIdOrderByIdAsc(anyLong())).thenReturn(List.of(item));

        List<ItemDto> items = itemService.getAllByRequestId(1L);

        assertEquals(1, items.size());
        assertEquals(1, items.get(0).getId());
        assertEquals(itemDtoForBooking.getName(), items.get(0).getName());
        assertEquals(itemDtoForBooking.getDescription(), items.get(0).getDescription());
        assertTrue(items.get(0).getAvailable());
        assertEquals(itemDtoForBooking.getOwnerId(), items.get(0).getOwnerId());
        assertNull(items.get(0).getLastBooking());
        assertNull(items.get(0).getNextBooking());
        assertEquals(0, items.get(0).getComments().size());
        assertEquals(itemDtoForBooking.getRequestId(), items.get(0).getRequestId());

        verify(itemRepository, times(1)).findAllByRequestIdOrderByIdAsc(anyLong());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getAllItemsByText() {
        when(itemRepository.search(anyString(), any(Pageable.class))).thenReturn(List.of(item));
        when(bookingRepository.getLastBooking(anyList(), any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.getNextBooking(anyList(), any(LocalDateTime.class))).thenReturn(List.of(booking));

        List<ItemDto> items = itemService.getAllByText("Hello", 0, 1);

        assertEquals(1, items.size());
        assertEquals(1, items.get(0).getId());
        assertEquals(itemDtoForBooking.getName(), items.get(0).getName());
        assertEquals(itemDtoForBooking.getDescription(), items.get(0).getDescription());
        assertTrue(items.get(0).getAvailable());
        assertEquals(itemDtoForBooking.getOwnerId(), items.get(0).getOwnerId());
        assertEquals(bookingOutputDto.getId(), items.get(0).getLastBooking().getId());
        assertEquals(bookingOutputDto.getId(), items.get(0).getNextBooking().getId());
        assertEquals(0, items.get(0).getComments().size());
        assertEquals(itemDtoForBooking.getRequestId(), items.get(0).getRequestId());

        verify(itemRepository, times(1)).search(anyString(), any(Pageable.class));
        verifyNoMoreInteractions(itemRepository);
        verify(bookingRepository, times(1)).getLastBooking(anyList(), any(LocalDateTime.class));
        verify(bookingRepository, times(1)).getNextBooking(anyList(), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllItemsByEmptyText() {
        List<ItemDto> items = itemService.getAllByText("", 0, 1);

        assertEquals(items.size(), 0);
    }

    @Test
    void updateItem() {
        final Item newItem = new Item(
                1L,
                "Какая-то обновленная вещь",
                "Какое-то описание",
                true,
                1L,
                user,
                List.of(comment));

        when(itemRepository.findByOwnerIdAndId(anyLong(), anyLong())).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(newItem);
        when(bookingRepository.getLastBooking(anyList(), any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.getNextBooking(anyList(), any(LocalDateTime.class))).thenReturn(List.of(booking));

        ItemDto updatedItem = itemService.update(userDtoForBooking, 1L, itemDtoForBooking);

        assertNotNull(updatedItem);
        assertEquals(1, updatedItem.getId());
        assertEquals(newItem.getName(), updatedItem.getName());
        assertEquals(itemDtoForBooking.getDescription(), updatedItem.getDescription());
        assertTrue(updatedItem.getAvailable());
        assertEquals(itemDtoForBooking.getOwnerId(), updatedItem.getOwnerId());
        assertEquals(bookingOutputDto.getId(), updatedItem.getLastBooking().getId());
        assertEquals(bookingOutputDto.getId(), updatedItem.getNextBooking().getId());
        assertEquals(1, updatedItem.getComments().size());
        assertEquals(itemDtoForBooking.getRequestId(), updatedItem.getRequestId());

        verify(itemRepository, times(1)).findByOwnerIdAndId(anyLong(), anyLong());
        verify(itemRepository, times(1)).save(any());
        verifyNoMoreInteractions(itemRepository);
        verify(bookingRepository, times(1)).getLastBooking(anyList(), any(LocalDateTime.class));
        verify(bookingRepository, times(1)).getNextBooking(anyList(), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void updateWithNotFoundItem() {
        Long itemId = 1L;

        when(itemRepository.findByOwnerIdAndId(anyLong(), anyLong())).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> itemService.update(userDtoForBooking, itemId, itemDtoForBooking));
    }

    @Test
    void createComment() {
        when(bookingRepository.getAllUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto createdComment = itemService.createComment(commentDto, userDtoForBooking, itemDtoForBooking, LocalDateTime.now());

        assertNotNull(createdComment);
        assertEquals(comment.getText(), createdComment.getText());
        assertEquals(comment.getId(), createdComment.getId());
        assertEquals(comment.getCreated().toString(), createdComment.getCreated().toString());
        assertEquals(comment.getAuthor().getName(), createdComment.getAuthorName());

        verify(bookingRepository, times(1))
                .getAllUserBookings(anyLong(), anyLong(), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingRepository);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void createCommentWithEmptyBookings() {
        when(bookingRepository.getAllUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class,
                () -> itemService.createComment(commentDto, userDtoForBooking, itemDtoForBooking, LocalDateTime.now()));
    }
}
