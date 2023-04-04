package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.UserChecker;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @InjectMocks
    private RequestServiceImpl requestService;

    @Mock
    private UserChecker userChecker;

    private final UserDto userDto = new UserDto(
            1L,
            "Igor net",
            "barb@gmail.com");

    private final LocalDateTime time = LocalDateTime.now();

    private final ItemRequestDto requestDto = new ItemRequestDto(
            1L,
            "Request",
            time,
            Collections.emptyList());

    @Test
    void addRequest() {
        when(userChecker.getIfExists(anyLong(), any())).thenReturn(requestDto);

        ItemRequestDto createdRequest = requestService.add(requestDto.getDescription(), userDto.getId());

        assertEquals(requestDto, createdRequest);
    }

    @Test
    void getByRequesterId() {
        when(userChecker.getIfExists(anyLong(), any())).thenReturn(List.of(requestDto));

        List<ItemRequestDto> requests = requestService.getByRequesterId(1L);

        assertEquals(1, requests.size());
        assertEquals(requestDto, requests.get(0));
    }

    @Test
    void getById() {
        when(userChecker.getIfExists(anyLong(), any())).thenReturn(requestDto);

        ItemRequestDto foundRequest = requestService.getById(anyLong(), anyLong());

        assertEquals(requestDto, foundRequest);
    }

    @Test
    void getAllRequests() {
        when(userChecker.getIfExists(anyLong(), any())).thenReturn(List.of(requestDto));

        List<ItemRequestDto> requests = requestService.getByUserId(1L, 0, 1);

        assertEquals(1, requests.size());
        assertEquals(requestDto, requests.get(0));
    }
}
