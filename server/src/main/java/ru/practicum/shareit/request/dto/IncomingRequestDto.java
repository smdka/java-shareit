package ru.practicum.shareit.request.dto;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class IncomingRequestDto {
    String description;
}
