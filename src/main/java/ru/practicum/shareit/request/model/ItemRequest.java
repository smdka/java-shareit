package ru.practicum.shareit.request.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    private Long id;
    private final String description;

    @Column(name = "requester_id")
    private final Long requesterId;
    private LocalDateTime created = LocalDateTime.now();
}
