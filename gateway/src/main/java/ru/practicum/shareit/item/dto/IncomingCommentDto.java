package ru.practicum.shareit.item.dto;

import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@NoArgsConstructor(force = true)

public class IncomingCommentDto {
    @NotBlank
    String text;
}
