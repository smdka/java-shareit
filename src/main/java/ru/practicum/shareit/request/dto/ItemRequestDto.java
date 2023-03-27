package ru.practicum.shareit.request.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Value
public class ItemRequestDto {
    Long id;

    @NotBlank
    String description;

    LocalDateTime created;
}
