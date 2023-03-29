package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    private final User user = new User(
            1L,
            "Igor",
            "igor@gmail.dom");

    private final Item item = new Item(
            1L,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            1L,
            user,
            new ArrayList<>());

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
            1L,
            null,
            null,
            new ArrayList<>(),
            1L);


    private final Booking booking = new Booking(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            item,
            user,
            Status.WAITING);

    private final BookingOutputDto bookingOutputDto = new BookingOutputDto(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Status.WAITING.name(),
            userDto,
            itemDto);


    @Test
    void createItem() {
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto createdItem = itemService.create(itemDto, userDto);

        Assertions.assertNotNull(createdItem);
        Assertions.assertEquals(1, createdItem.getId());
        Assertions.assertEquals(itemDto.getName(), createdItem.getName());
        Assertions.assertEquals(itemDto.getDescription(), createdItem.getDescription());
        Assertions.assertTrue(createdItem.getAvailable());
        Assertions.assertEquals(itemDto.getOwnerId(), createdItem.getOwnerId());
        Assertions.assertNull(createdItem.getLastBooking());
        Assertions.assertNull(createdItem.getNextBooking());
        Assertions.assertEquals(itemDto.getComments().size(), createdItem.getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), createdItem.getRequestId());

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

        Assertions.assertNotNull(itemById);
        Assertions.assertEquals(1, itemById.getId());
        Assertions.assertEquals(itemDto.getName(), itemById.getName());
        Assertions.assertEquals(itemDto.getDescription(), itemById.getDescription());
        Assertions.assertTrue(itemById.getAvailable());
        Assertions.assertEquals(itemDto.getOwnerId(), itemById.getOwnerId());
        Assertions.assertEquals(bookingOutputDto.getId(), itemById.getLastBooking().getId());
        Assertions.assertEquals(bookingOutputDto.getId(), itemById.getNextBooking().getId());
        Assertions.assertEquals(0, itemById.getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), itemById.getRequestId());

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

        Assertions.assertNotNull(itemById);
        Assertions.assertEquals(1, itemById.getId());
        Assertions.assertEquals(itemDto.getName(), itemById.getName());
        Assertions.assertEquals(itemDto.getDescription(), itemById.getDescription());
        Assertions.assertTrue(itemById.getAvailable());
        Assertions.assertEquals(itemDto.getOwnerId(), itemById.getOwnerId());
        Assertions.assertNull(itemById.getLastBooking());
        Assertions.assertNull(itemById.getNextBooking());
        Assertions.assertEquals(0, itemById.getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), itemById.getRequestId());

        verify(itemRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getAllItemsByUserId() {
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), any(Pageable.class))).thenReturn(List.of(item));
        when(bookingRepository.getLastBooking(anyList(), any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.getNextBooking(anyList(), any(LocalDateTime.class))).thenReturn(List.of(booking));

        List<ItemDto> items = itemService.getAllByUserId(1L, 0, 1);

        Assertions.assertEquals(items.size(), 1);
        Assertions.assertEquals(1, items.get(0).getId());
        Assertions.assertEquals(itemDto.getName(), items.get(0).getName());
        Assertions.assertEquals(itemDto.getDescription(), items.get(0).getDescription());
        Assertions.assertTrue(items.get(0).getAvailable());
        Assertions.assertEquals(itemDto.getOwnerId(), items.get(0).getOwnerId());
        Assertions.assertEquals(bookingOutputDto.getId(), items.get(0).getLastBooking().getId());
        Assertions.assertEquals(bookingOutputDto.getId(), items.get(0).getNextBooking().getId());
        Assertions.assertEquals(0, items.get(0).getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), items.get(0).getRequestId());

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

        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(1, items.get(0).getId());
        Assertions.assertEquals(itemDto.getName(), items.get(0).getName());
        Assertions.assertEquals(itemDto.getDescription(), items.get(0).getDescription());
        Assertions.assertTrue(items.get(0).getAvailable());
        Assertions.assertEquals(itemDto.getOwnerId(), items.get(0).getOwnerId());
        Assertions.assertNull(items.get(0).getLastBooking());
        Assertions.assertNull(items.get(0).getNextBooking());
        Assertions.assertEquals(0, items.get(0).getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), items.get(0).getRequestId());

        verify(itemRepository, times(1)).findAllByRequestIdOrderByIdAsc(anyLong());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getAllItemsByText() {
        when(itemRepository.search(anyString(), any(Pageable.class))).thenReturn(List.of(item));
        when(bookingRepository.getLastBooking(anyList(), any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.getNextBooking(anyList(), any(LocalDateTime.class))).thenReturn(List.of(booking));

        List<ItemDto> items = itemService.getAllByText("Hello", 0, 1);

        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(1, items.get(0).getId());
        Assertions.assertEquals(itemDto.getName(), items.get(0).getName());
        Assertions.assertEquals(itemDto.getDescription(), items.get(0).getDescription());
        Assertions.assertTrue(items.get(0).getAvailable());
        Assertions.assertEquals(itemDto.getOwnerId(), items.get(0).getOwnerId());
        Assertions.assertEquals(bookingOutputDto.getId(), items.get(0).getLastBooking().getId());
        Assertions.assertEquals(bookingOutputDto.getId(), items.get(0).getNextBooking().getId());
        Assertions.assertEquals(0, items.get(0).getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), items.get(0).getRequestId());

        verify(itemRepository, times(1)).search(anyString(), any(Pageable.class));
        verifyNoMoreInteractions(itemRepository);
        verify(bookingRepository, times(1)).getLastBooking(anyList(), any(LocalDateTime.class));
        verify(bookingRepository, times(1)).getNextBooking(anyList(), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllItemsByEmptyText() {
        List<ItemDto> items = itemService.getAllByText("", 0, 1);

        Assertions.assertEquals(items.size(), 0);
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

        ItemDto updatedItem = itemService.update(userDto, 1L, itemDto);

        Assertions.assertNotNull(updatedItem);
        Assertions.assertEquals(1, updatedItem.getId());
        Assertions.assertEquals(newItem.getName(), updatedItem.getName());
        Assertions.assertEquals(itemDto.getDescription(), updatedItem.getDescription());
        Assertions.assertTrue(updatedItem.getAvailable());
        Assertions.assertEquals(itemDto.getOwnerId(), updatedItem.getOwnerId());
        Assertions.assertEquals(bookingOutputDto.getId(), updatedItem.getLastBooking().getId());
        Assertions.assertEquals(bookingOutputDto.getId(), updatedItem.getNextBooking().getId());
        Assertions.assertEquals(1, updatedItem.getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), updatedItem.getRequestId());

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

        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.update(userDto, itemId, itemDto));
    }

    @Test
    void createComment() {
        when(bookingRepository.getAllUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto createdComment = itemService.createComment(commentDto, userDto, itemDto, LocalDateTime.now());

        Assertions.assertNotNull(createdComment);
        Assertions.assertEquals(comment.getText(), createdComment.getText());
        Assertions.assertEquals(comment.getId(), createdComment.getId());
        Assertions.assertEquals(comment.getCreated().toString(), createdComment.getCreated().toString());
        Assertions.assertEquals(comment.getAuthor().getName(), createdComment.getAuthorName());

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

        Assertions.assertThrows(BadRequestException.class,
                () -> itemService.createComment(commentDto, userDto, itemDto, LocalDateTime.now()));
    }
}
