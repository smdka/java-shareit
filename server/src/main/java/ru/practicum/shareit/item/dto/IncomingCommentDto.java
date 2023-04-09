package ru.practicum.shareit.item.dto;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class IncomingCommentDto {
    String text;
}
