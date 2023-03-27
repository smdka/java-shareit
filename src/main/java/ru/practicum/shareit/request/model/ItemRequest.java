package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;

    @Column(name = "requester_id")
    private Long requesterId;

    @Column(name = "created_at", updatable = false)
    private final LocalDateTime created = LocalDateTime.now();

    @OneToMany
    @JoinColumn(name = "request_id")
    private List<Item> items;
}
