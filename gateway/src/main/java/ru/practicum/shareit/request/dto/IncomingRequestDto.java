package ru.practicum.shareit.request.dto;

import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@NoArgsConstructor(force = true)
public class IncomingRequestDto {
    @NotBlank
    String description;
}
