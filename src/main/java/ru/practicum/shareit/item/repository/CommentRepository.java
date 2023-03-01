package ru.practicum.shareit.item.repository;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    Collection<Comment> findAllByItemId(Long itemId);
}
