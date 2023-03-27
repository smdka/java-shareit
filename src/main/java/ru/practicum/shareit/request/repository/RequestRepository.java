package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends CrudRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequesterIdOrderByCreatedAsc(Long requesterId);

    List<ItemRequest> findByRequesterIdNotOrderByCreatedAsc(Long userId, PageRequest pageRequest);
}
